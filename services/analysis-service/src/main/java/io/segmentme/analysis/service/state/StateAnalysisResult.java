package io.segmentme.analysis.service.state;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class StateAnalysisResult {

    private String name;

    private JsonNode value;
}
