package io.segmentme.redis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SegmentAnalysisResutl {

    private String name;

    private boolean value;

    private long analysisTime;
}
