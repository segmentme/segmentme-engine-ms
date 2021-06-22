package io.segmentme.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SegmentAnalysisResult {

    private String name;

    private String segmentId;

    private String hash;

    private boolean value;

    private boolean opened;

    private long analysisTime;
}
