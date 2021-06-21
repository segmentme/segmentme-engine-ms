package io.segmentme.measurement.domain;

import io.segmentme.analysis.domain.statistic.StatisticLog;
import io.segmentme.core.domain.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "participantStatistic")
public class ParticipantStatistic extends DbObject {
    @Indexed
    private String contextId;

    @Indexed
    private String uniquenessIndicator;

    @Indexed
    private Object uniquenessValue;

    private long analysisCount;

    private List<StatisticLog.SegmentStatistic> lastSegmentStatistic;

    private List<String> inSegment = new ArrayList<>();
}
