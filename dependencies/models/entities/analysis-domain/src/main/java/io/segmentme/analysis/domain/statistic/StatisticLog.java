package io.segmentme.analysis.domain.statistic;

import io.segmentme.core.domain.DbObject;
import io.segmentme.models.shared.analysis.InlineType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "statistic")
@Data
public class StatisticLog extends DbObject {
    private long analysisTime;

    @Indexed
    private String integrationPointKey;

    private int executedConditionsCount;

    private List<ConditionStatistic> conditionStatistics;

    private List<SegmentStatistic> segmentStatistics;

    private Map<String, InlineType> knownTypes;

    @Indexed
    private String analyzedDataKey;

    @Indexed
    private String workspaceId;

    @Data
    public static class SegmentStatistic {
        private String segmentId;

        private String hash;

        private Map<String, Integer> conditionsHash;

        private boolean analysisResult;

        private boolean finalResult;

        private long analysisTime;
    }

    @Data
    public static class ConditionStatistic {
        private String hash;
        private String criteria;
        private String errors;
    }
}
