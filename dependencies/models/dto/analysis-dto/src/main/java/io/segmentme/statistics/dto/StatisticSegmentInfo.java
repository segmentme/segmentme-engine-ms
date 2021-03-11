package io.segmentme.statistics.dto;

import lombok.Data;

@Data
public class StatisticSegmentInfo {

    private String id;

    private String name;

    private boolean result;

    private long analysisTime;
}
