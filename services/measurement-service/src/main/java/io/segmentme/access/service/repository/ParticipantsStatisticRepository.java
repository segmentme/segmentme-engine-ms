package io.segmentme.access.service.repository;

import io.segmentme.access.service.domain.statistic.ParticipantStatistic;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ParticipantsStatisticRepository extends MongoRepository<ParticipantStatistic, String> {


    List<ParticipantStatistic> findAllByUniquenessValueIn(List<Object> uniquenessValue);
    long countAllByInSegmentContaining(String segmentId);

    ParticipantStatistic findByUniquenessValueAndContextId(Object uniquenessValue, String contextId);


}
