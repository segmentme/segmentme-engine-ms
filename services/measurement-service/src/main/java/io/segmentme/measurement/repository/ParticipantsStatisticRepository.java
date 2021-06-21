package io.segmentme.measurement.repository;

import io.segmentme.measurement.domain.ParticipantStatistic;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ParticipantsStatisticRepository extends MongoRepository<ParticipantStatistic, String> {


    List<ParticipantStatistic> findAllByUniquenessValueIn(List<Object> uniquenessValue);
    long countAllByInSegmentContaining(String segmentId);

    ParticipantStatistic findByUniquenessValueAndContextId(Object uniquenessValue, String contextId);


}
