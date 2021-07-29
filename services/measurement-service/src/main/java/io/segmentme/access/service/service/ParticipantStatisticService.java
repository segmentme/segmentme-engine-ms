package io.segmentme.access.service.service;

import io.segmentme.access.service.domain.statistic.ContextStatistic;
import io.segmentme.access.service.domain.statistic.ParticipantStatistic;
import io.segmentme.access.service.dto.ParticipantAcknowledgeRequest;
import io.segmentme.access.service.repository.ParticipantsStatisticRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import io.segmentme.core.domain.DbObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Data
@Component
@RequiredArgsConstructor
@Slf4j
public class ParticipantStatisticService extends AbstractDatabaseService<ParticipantStatistic, ParticipantsStatisticRepository> {

    private final MongoTemplate mongoTemplate;

    public void acknowledgeParticipant(ParticipantAcknowledgeRequest contextDataHolder) {
        if (StringUtils.isEmpty(contextDataHolder.getUniquenessIndicator())) {
            return;
        }
        Object value = contextDataHolder.getValues().get(contextDataHolder.getUniquenessIndicator());
        ParticipantStatistic participant = getParticipant(contextDataHolder.getContextId(), value);
        if (participant != null) {
            return;
        }
        participant = new ParticipantStatistic();
        participant.setContextId(contextDataHolder.getContextId());
        participant.setUniquenessIndicator(contextDataHolder.getUniquenessIndicator());
        participant.setUniquenessValue(value);
        createParticipant(participant);
    }

    public long participantsInSegmentCounts(String segmentId) {
        return this.repository.countAllByInSegmentContaining(segmentId);

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

    @Override
    public ParticipantStatistic update(ParticipantStatistic participantStatistic) {
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
        return mongoTemplate.findAndModify(query, update, ParticipantStatistic.class);
    }


    public ParticipantStatistic getParticipant(String contextId, Object uniquenessValue) {
        return this.repository.findByUniquenessValueAndContextId(uniquenessValue, contextId);
    }


    public void includeParticipantIntoSegment(int usersToInclude, String segmentId, String contextId) {

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

    public void excludeParticipantFromSegment(int usersToExclude, String segmentId, String contextId) {

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
}
