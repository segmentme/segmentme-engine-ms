package io.segmentme.measurement.service.service;

import io.segmentme.core.db.service.AbstractDatabaseService;
import io.segmentme.measurement.service.domain.statistic.ContextStatistic;
import io.segmentme.measurement.service.domain.statistic.ParticipantStatistic;
import io.segmentme.measurement.service.domain.statistic.StatisticLog;
import io.segmentme.measurement.service.repository.ContextStatisticsRepository;
import io.segmentme.measurement.service.repository.StatisticRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.count;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeasurementService extends AbstractDatabaseService<StatisticLog, StatisticRepository> {

    private final StatisticService statisticService;

    private final ParticipantStatisticService participantStatisticService;

    private final MongoTemplate mongoTemplate;

    private final ContextStatisticsRepository contextStatisticsRepository;

    public void redistributePercentage(String contextId, String segmentId, int percentage) {
        ContextStatistic contextStatistic = statisticService.findContextStatistic(contextId);
        long inSegmentCounts = participantStatisticService.participantsInSegmentCounts(segmentId);
        long totalParticipants = contextStatistic.getTotalParticipants();
        long usersShouldBeIncluded = percentage * totalParticipants / 100;

        if (usersShouldBeIncluded == inSegmentCounts) {
            return;
        }
        log.info("Redistribute segmentation percentage {} to {} %, number {}", segmentId, percentage, (usersShouldBeIncluded - inSegmentCounts));
        int modAmount = (int) Math.abs(usersShouldBeIncluded - inSegmentCounts);
        if (usersShouldBeIncluded > inSegmentCounts) {
            participantStatisticService.includeParticipantIntoSegment(modAmount, segmentId, contextId);
        } else {
            participantStatisticService.excludeParticipantFromSegment(modAmount, segmentId, contextId);
        }
    }

    public void refreshParticipants(String contextId, String uniquenessIdentifier) {
        ContextStatistic contextStatistic = contextStatisticsRepository.findByContextId(contextId);
        if(contextStatistic==null){
            return;
        }
        contextStatistic.setTotalParticipants(0);
        contextStatisticsRepository.save(contextStatistic);

        String identifierPath = NODE_VALUES + uniquenessIdentifier.replace(".", "#");
        MatchOperation match = match(Criteria.where(PROP_CONTEXT_ID).is(contextId).and(identifierPath).exists(true));
        GroupOperation groupOperation = group(PROP_CONTEXT_ID, HASH).first(identifierPath).as(ID);

        Aggregation aggregation = Aggregation.newAggregation(match, groupOperation, count().as("contextId"));
        var analyzedData = mongoTemplate.aggregate(aggregation, "analyzedData", ParticipantCandidate.class).iterator();

        String result = null;

        if (analyzedData.hasNext()) {
            result = analyzedData.next().contextId;
        }

        long count = StringUtils.isNotEmpty(result) ? Long.parseLong(result) : 0;

        int pageSize = 1000;
        int page = 0;
        int totalPages = Math.round((count / pageSize) + 0.5f);
        while (page < totalPages) {
            List<ParticipantCandidate> participantsToCreate = getNextPageResult(page, pageSize, contextId, uniquenessIdentifier);
            participantsToCreate.forEach(it -> {
                participantStatisticService.createParticipant(new ParticipantStatistic().setContextId(contextId).setUniquenessIndicator(uniquenessIdentifier).setUniquenessValue(it.getId()));
                //fetch last segment statistic
            });

            page++;
        }

    }

    private List<ParticipantCandidate> getNextPageResult(int page, int pageSize, String contextId, String uniquenessIdentifier) {
        String identifierPath = NODE_VALUES + uniquenessIdentifier.replace(".", "#");
        MatchOperation match = match(Criteria.where(PROP_CONTEXT_ID).is(contextId).and(identifierPath).exists(true));
        GroupOperation groupOperation = group(PROP_CONTEXT_ID, HASH).first(identifierPath).as(ID).first(PROP_CONTEXT_ID).as(PROP_CONTEXT_ID).first(HASH).as(HASH);
        TypedAggregation<ParticipantCandidate> aggregation = new TypedAggregation<>(ParticipantCandidate.class, match, groupOperation, skip(Integer.toUnsignedLong(page * pageSize)), limit(pageSize));
        return mongoTemplate.aggregate(aggregation, "analyzedData", ParticipantCandidate.class).getMappedResults();
    }

    @Data
    public static class ParticipantCandidate {
        private Object id;
        private String hash;
        private String contextId;
    }
}
