package io.segmentme.measurement.service;

import io.segmentme.analysis.domain.statistic.AnalyzedData;
import io.segmentme.analysis.domain.statistic.SegmentStatisticCount;
import io.segmentme.analysis.domain.statistic.StatisticLog;
import io.segmentme.core.db.service.AbstractDatabaseService;
import io.segmentme.core.domain.DbObject;
import io.segmentme.measurement.domain.ContextStatistic;
import io.segmentme.measurement.domain.ParticipantStatistic;
import io.segmentme.measurement.repository.AnalyzedDataRepository;
import io.segmentme.measurement.repository.ContextStatisticsRepository;
import io.segmentme.measurement.repository.ParticipantsStatisticRepository;
import io.segmentme.measurement.repository.StatisticRepository;
import io.segmentme.models.shared.analysis.AggregatedAnalysisCount;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticService extends AbstractDatabaseService<StatisticLog, StatisticRepository> {


    private final MongoTemplate mongoTemplate;

    private final AnalyzedDataRepository analyzedDataRepository;
    private final ParticipantsStatisticRepository participantsStatisticRepository;
    private final ContextStatisticsRepository segmentAnalysisRepository;

    private final StatisticRepository statisticRepository;


    public ParticipantStatistic getParticipant(String contextId, Object uniquenessValue) {
        return participantsStatisticRepository.findByUniquenessValueAndContextId(uniquenessValue, contextId);
    }

    public List<ParticipantStatistic> getParticipants(List<Object> uniquenessValue) {
        return participantsStatisticRepository.findAllByUniquenessValueIn(uniquenessValue);
    }

    public List<SegmentStatisticCount> getSegmentStatistic(String workspaceId, int period) {
        LocalDateTime localDateTime = LocalDate.now().minus(period, ChronoUnit.DAYS).atTime(LocalTime.MIDNIGHT);
        MatchOperation dateFilter = match(new Criteria(PROP_WORKSPACE_ID)
            .is(workspaceId)
            .and(CREATED_DATE)
            .gte(localDateTime));
        UnwindOperation unwind = Aggregation.unwind(PROP_SEGMENT_STATISTICS);


        ProjectionOperation segmentInfo = Aggregation
            .project(PROP_WORKSPACE_ID)
            .and(SEGMENT_STATISTICS_SEGMENT_ID).as(SEGMENT_ID)
            .and(SEGMENT_STATISTICS_RESULT).as(SEGMENT_RESULT);

        MatchOperation trueSegmentFilter = match(new Criteria(SEGMENT_RESULT).is(true));
        GroupOperation groupBySegment = group(SEGMENT_ID).count().as(COUNT);
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, COUNT);

        ProjectionOperation segmentCountProjection = Aggregation.project(COUNT).and(ID).as(SEGMENT_ID);

        TypedAggregation<StatisticLog> aggregation
            = new TypedAggregation<>(StatisticLog.class, dateFilter, unwind, segmentInfo, trueSegmentFilter, groupBySegment, sortOperation, segmentCountProjection);

        AggregationResults<SegmentStatisticCount> result = mongoTemplate.aggregate(aggregation, SegmentStatisticCount.class);

        return result.getMappedResults();

    }

    public List<AggregatedAnalysisCount> getAnalysisCount(String workspaceId, int period) {
        LocalDateTime localDateTime = LocalDate.now().minus(period, ChronoUnit.DAYS).atTime(LocalTime.MIDNIGHT);

        ProjectionOperation projectStage = Aggregation
            .project(PROP_WORKSPACE_ID, CREATED_DATE, ANALYSIS_TIME, INTEGRATION_POINT_KEY)
            .and(CREATED_DATE).dateAsFormattedString(DATE_FORMATTER).as(DATE_HOUR);

        MatchOperation matchStage = match(new Criteria(PROP_WORKSPACE_ID)
            .is(workspaceId)
            .and(CREATED_DATE)
            .gte(localDateTime));

        GroupOperation groupOperation = group(DATE_HOUR, INTEGRATION_POINT_KEY).count()
            .as(COUNT).sum(ANALYSIS_TIME).as(TOTAL_ANALYSIS_TIME);


        ProjectionOperation finalProjections = Aggregation.project(COUNT, TOTAL_ANALYSIS_TIME)
            .and(ID_DATE_HOUR).as(DATE_TIME).and(ID_INTEGRATION_POINT_KEY).as(INTEGRATION_POINT_KEY);

        TypedAggregation<StatisticLog> aggregation
            = new TypedAggregation<>(StatisticLog.class, projectStage, matchStage, groupOperation, finalProjections);

        AggregationResults<AggregatedAnalysisCount> result = mongoTemplate.aggregate(aggregation, AggregatedAnalysisCount.class);

        return result.getMappedResults();

    }

    public List<AggregatedAnalysisCount> getAnalysisCountForCriteriaValue(String workspaceId, int period, String criteria, String value) {
        LocalDateTime localDateTime = LocalDate.now().minus(period, ChronoUnit.DAYS).atTime(LocalTime.MIDNIGHT);
        String criteriaField = convertToNodeValues(criteria);

        List<Object> values = resolvePossibleValueType(value);
        MatchOperation dateFilter = match(new Criteria(PROP_WORKSPACE_ID).is(workspaceId).and(LAST_MODIFIED_DATE).gte(localDateTime));

        MatchOperation nodeValuePath = match(new Criteria(criteriaField).in(values));

        ProjectionOperation analyzedDataProjection = Aggregation.project(HASH);

        TypedAggregation<AnalyzedData> analyzedDataAggregation
            = new TypedAggregation<>(AnalyzedData.class, dateFilter, nodeValuePath, analyzedDataProjection);
        AggregationResults<AnalyzedData> analyzedDataList = mongoTemplate.aggregate(analyzedDataAggregation, AnalyzedData.class);

        dateFilter = match(new Criteria(PROP_WORKSPACE_ID).is(workspaceId).and(CREATED_DATE).gte(localDateTime));
        MatchOperation analyzedDataMatch = match(new Criteria(ANALYZED_DATA_KEY).in(analyzedDataList.getMappedResults().stream().map(AnalyzedData::getHash).collect(Collectors.toList())));
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, CREATED_DATE);

        ProjectionOperation projectStage = Aggregation
            .project(PROP_WORKSPACE_ID, CREATED_DATE, ANALYSIS_TIME, INTEGRATION_POINT_KEY)
            .and(CREATED_DATE).dateAsFormattedString(DATE_FORMATTER).as(DATE_HOUR);

        ProjectionOperation finalProjections = Aggregation.project(COUNT, TOTAL_ANALYSIS_TIME)
            .and(ID_DATE_HOUR).as(DATE_TIME)
            .and(ID_INTEGRATION_POINT_KEY).as(INTEGRATION_POINT_KEY);

        GroupOperation groupOperation = group(DATE_HOUR, INTEGRATION_POINT_KEY).count().as(COUNT)
            .sum(ANALYSIS_TIME).as(TOTAL_ANALYSIS_TIME);

        TypedAggregation<StatisticLog> aggregation
            = new TypedAggregation<>(StatisticLog.class, dateFilter, sort, analyzedDataMatch, projectStage, groupOperation, finalProjections);

        AggregationResults<AggregatedAnalysisCount> result = mongoTemplate.aggregate(aggregation, AggregatedAnalysisCount.class);

        return result.getMappedResults();
    }

    public Page<StatisticLog> getSegmentStatistic(String workspaceId, LocalDateTime start, LocalDateTime end, String criteria, String value, Pageable pageable) {
        var startDate = start.toInstant(ZoneOffset.UTC);
        var endDate = end.toInstant(ZoneOffset.UTC);
        var criteriaField = convertToNodeValues(criteria);
        var values = resolvePossibleValueType(value);

        var analyzedDataQuery = Arrays.asList(
            match(Criteria.where(PROP_WORKSPACE_ID).is(workspaceId).and(LAST_MODIFIED_DATE).gte(startDate).lte(endDate).and(criteriaField).in(values)),
            project(HASH));

        var analyzedDataHashes = this.aggregate(AnalyzedData.class, analyzedDataQuery).getMappedResults().stream().map(AnalyzedData::getHash).collect(Collectors.toList());

        var statisticMatch = match(Criteria.where(PROP_WORKSPACE_ID).is(workspaceId)
            .and(CREATED_DATE).gte(startDate).lt(endDate)
            .and(ANALYZED_DATA_KEY).in(analyzedDataHashes)
        );

        var statisticCount = count(StatisticLog.class, statisticMatch);

        var statisticAggregations = new ArrayList<>(Arrays.asList(statisticMatch,
            new SkipOperation((long) pageable.getPageNumber() * pageable.getPageSize()),
            limit(pageable.getPageSize())));

        addStatisticOrder(pageable.getSort(), statisticAggregations);

        return new PageImpl<>(this.aggregate(StatisticLog.class, statisticAggregations).getMappedResults(), pageable, statisticCount);
    }

    private String convertToNodeValues(String criteria) {
        return NODE_VALUES + criteria.replace(".", "#");
    }

    private void addStatisticOrder(Sort sort, List<AggregationOperation> aggregationOperation) {
        if (sort.isUnsorted()) {
            aggregationOperation.add(Aggregation.sort(Sort.Direction.DESC, CREATED_DATE));
        } else {
            boolean segmentsCount = sort.stream().anyMatch(it -> SEGMENTS_COUNT.equals(it.getProperty()));
            if (segmentsCount) {
                ArrayOperators.Size segmentsCountField = ArrayOperators.Size.lengthOfArray(ConditionalOperators.ifNull(SEGMENTS_COUNT).then(Collections.emptyList()));
                aggregationOperation.add(Aggregation.addFields().addField(SEGMENTS_COUNT).withValueOf(segmentsCountField).build());
            }
            sort.forEach(it -> aggregationOperation.add(Aggregation.sort(it.getDirection(), it.getProperty())));
        }
    }

    private <T> long count(Class<T> clazz, AggregationOperation... operations) {
        var countOperations = new ArrayList<>(Arrays.asList(operations));
        countOperations.add(Aggregation.count().as(COUNT));

        var countAggregation = newAggregation(clazz, countOperations);

        return Optional.of(mongoTemplate.aggregate(countAggregation, TotalCount.class))
            .map(AggregationResults::getUniqueMappedResult)
            .map(TotalCount::getCount)
            .orElse(0L);
    }

    private <T> AggregationResults<T> aggregate(Class<T> clazz, List<AggregationOperation> operations) {
        return mongoTemplate.aggregate(newAggregation(clazz, operations), clazz);
    }

    private List<Object> resolvePossibleValueType(String value) {
        List<Object> values = new ArrayList<>();
        values.add(value);
        try {
            values.add(Integer.valueOf(value));
        } catch (Throwable ex) {
            //mute
        }
        try {
            values.add(Double.valueOf(value));
        } catch (Throwable ex) {
            //mute
        }
        try {
            Optional.ofNullable(BooleanUtils.toBooleanObject(value)).ifPresent(values::add);
        } catch (Throwable ex) {
            //mute
        }


        return values;
    }

    public ExploreStatisticLog getStatisticLogOverview(String statisticLogId) {
        return repository.findById(statisticLogId).map(log -> {
            AnalyzedData hash = analyzedDataRepository.findByHash(log.getAnalyzedDataKey());
            return new ExploreStatisticLog().setRawPayload(hash.getPayload());
        }).orElseGet(ExploreStatisticLog::new);
    }


    public void createParticipant(ParticipantStatistic participantStatistic) {

        Query query = new Query(Criteria.where(PROP_UNIQUENESS_INDICATOR)
            .is(participantStatistic.getUniquenessIndicator())
            .and(PROP_UNIQUENESS_VALUE)
            .is(participantStatistic.getUniquenessValue())
            .and(PROP_CONTEXT_ID).is(participantStatistic.getContextId()));
        Instant now = Instant.now();
        Update update = new Update()
            .set(PROP_CONTEXT_ID, participantStatistic.getContextId())
            .set(PROP_UNIQUENESS_INDICATOR, participantStatistic.getUniquenessIndicator())
            .set(CREATED_DATE, now)
            .set(LAST_MODIFIED_DATE, now)
            .set(PROP_UNIQUENESS_VALUE, participantStatistic.getUniquenessValue());

        mongoTemplate.upsert(query, update, ParticipantStatistic.class);

        Update contextTotalUsersCounter = new Update()
            .set(PROP_CONTEXT_ID, participantStatistic.getContextId())
            .inc(PROP_TOTAL_PARTICIPANTS, 1);
        mongoTemplate.upsert(new Query(Criteria.where(PROP_CONTEXT_ID).is(participantStatistic.getContextId())), contextTotalUsersCounter, ContextStatistic.class);
    }

    public void update(ParticipantStatistic participantStatistic) {
        Query query = new Query(Criteria.where(PROP_UNIQUENESS_INDICATOR)
            .is(participantStatistic.getUniquenessIndicator())
            .and(PROP_UNIQUENESS_VALUE)
            .is(participantStatistic.getUniquenessValue())
            .and(PROP_CONTEXT_ID).is(participantStatistic.getContextId()));
        Update update = new Update()
            .set(PROP_CONTEXT_ID, participantStatistic.getContextId())
            .set(PROP_UNIQUENESS_INDICATOR, participantStatistic.getUniquenessIndicator())
            .set(LAST_MODIFIED_DATE, Instant.now())
            .set(PROP_UNIQUENESS_VALUE, participantStatistic.getUniquenessValue());

        if (participantStatistic.getLastSegmentStatistic() != null) {
            update.set(PROP_LAST_SEGMENT_STATISTICS, participantStatistic.getLastSegmentStatistic());
        }
        mongoTemplate.findAndModify(query, update, ParticipantStatistic.class);
    }

    public ContextStatistic findContextStatistic(String contextId) {
        return segmentAnalysisRepository.findByContextId(contextId);
    }


//    public void updateContextSegmentStatistic(Map<String, Integer> segmentCounters, String contextId, Long timestamp) {
//
//        Update openedFor = new Update();
//        openedFor.set(PROP_TIMESTAMP, timestamp);
//
//        log.info("Update time --- {} , segments --- {}", timestamp, segmentCounters);
//        for (Map.Entry<String, Integer> segRes : segmentCounters.entrySet()) {
//            openedFor.set("segmentStatistics." + segRes.getKey() + "." + PROP_SEGMENT_ID, segRes.getKey());
//            openedFor.inc("segmentStatistics." + segRes.getKey() + "." + PROP_OPENED_PARTICIPANTS, segRes.getValue());
//        }
//
//        Query query = new Query(Criteria.where(PROP_CONTEXT_ID)
//            .is(contextId)
//            .orOperator(
//                Criteria.where(PROP_TIMESTAMP).exists(false),
//                Criteria.where(PROP_TIMESTAMP).lt(timestamp)
//            ));
//        mongoTemplate.findAndModify(query, openedFor, ContextStatistic.class);
//    }

    @Retryable(value = {Exception.class}, backoff = @Backoff(500L))
    private void bulkExecute(BulkOperations bulkOperations) {
        bulkOperations.execute();
    }

    public void update(List<StatisticLog> statisticLogs) {
        statisticRepository.saveAll(statisticLogs);
    }

    public long participantsInSegmentCounts(String segmentId) {
        return participantsStatisticRepository.countAllByInSegmentContaining(segmentId);

    }

    public void includeUsersToSegment(int usersToInclude, String segmentId, String contextId) {

        MatchOperation match = match(Criteria.where(PROP_CONTEXT_ID)
            .is(contextId)
            .and(PROP_IN_SEGMENT).not().in(segmentId)
            .and(PROP_LAST_SEGMENT_STATISTICS)
            .elemMatch(Criteria.where(SEGMENT_ID).is(segmentId).and(PRO_ANALYSIS_RESULT).is(true)));
        SortOperation sort = sort(Sort.Direction.DESC, LAST_MODIFIED_DATE);
        LimitOperation limit = limit(usersToInclude);
        ProjectionOperation project = project("id");

        TypedAggregation<ParticipantStatistic> idAggregations = new TypedAggregation<>(ParticipantStatistic.class, match, sort, limit, project);

        AggregationResults<ParticipantStatistic> result = mongoTemplate.aggregate(idAggregations, ParticipantStatistic.class);
        List<String> idsToUpdate = result.getMappedResults().stream().map(DbObject::getId).collect(Collectors.toList());

        Update update = new Update();
        update.addToSet(PROP_IN_SEGMENT, segmentId);

        Query query = new Query().addCriteria(Criteria.where(ID).in(idsToUpdate));
        mongoTemplate.updateMulti(query, update, ParticipantStatistic.class);
    }
    public void excludeUsersFromSegment(int usersToExclude, String segmentId, String contextId) {

        MatchOperation match = match(Criteria.where(PROP_CONTEXT_ID)
            .is(contextId)
            .and(PROP_IN_SEGMENT).in(segmentId)
            .and(PROP_LAST_SEGMENT_STATISTICS)
            .elemMatch(Criteria.where(SEGMENT_ID).is(segmentId).and(PRO_ANALYSIS_RESULT).is(true)));
        SortOperation sort = sort(Sort.Direction.ASC, LAST_MODIFIED_DATE);
        LimitOperation limit = limit(usersToExclude);
        ProjectionOperation project = project("id");

        TypedAggregation<ParticipantStatistic> idAggregations = new TypedAggregation<>(ParticipantStatistic.class, match, sort, limit, project);

        AggregationResults<ParticipantStatistic> result = mongoTemplate.aggregate(idAggregations, ParticipantStatistic.class);
        List<String> idsToUpdate = result.getMappedResults().stream().map(DbObject::getId).collect(Collectors.toList());

        Update update = new Update();
        update.pull(PROP_IN_SEGMENT, segmentId);

        Query query = new Query().addCriteria(Criteria.where(ID).in(idsToUpdate));
        mongoTemplate.updateMulti(query, update, ParticipantStatistic.class);
    }

    @Data
    public static class ExploreStatisticLog {
        private String rawPayload;
    }

    @Data
    public static class TotalCount {
        private Long count;
    }
}
