package io.segmentme.analysis.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.analysis.api.dto.ContextSchemaRefreshRequest;
import io.segmentme.analysis.api.dto.ContextSchemaShortInfo;
import io.segmentme.models.shared.analysis.IntegrationPoint;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;

@Data
@RequiredArgsConstructor
public class ManagementClient {
    private static final String WORKSPACE_INTEGRATION_POINT_INTEGRATION_POINT_KEY_PATH = "/workspace/integration-point?integrationPointKey=";
    private static final String CONTEXT_SCHEMA_ACTUALIZE_PATH = "/context-schema/actualize";

    @Qualifier("management-service-client")
    private final WebClient managementServiceClient;

    public IntegrationPoint getIntegrationPointKey(String integrationPointKey) {
        return managementServiceClient.get()
            .uri(WORKSPACE_INTEGRATION_POINT_INTEGRATION_POINT_KEY_PATH + integrationPointKey)
            .retrieve()
            .bodyToMono(IntegrationPoint.class)
            .block();
    }

    public String actualizeSchema(String contextKey, String integrationPointKey, JsonNode jsonNode) {
        ContextSchemaShortInfo block = managementServiceClient.put()
            .uri(CONTEXT_SCHEMA_ACTUALIZE_PATH)
            .bodyValue(new ContextSchemaRefreshRequest()
                .setIntegrationPointId(integrationPointKey)
                .setContextKey(contextKey)
                .setPayload(jsonNode)
            )
            .retrieve().bodyToMono(ContextSchemaShortInfo.class).block();
        return block.getId();
    }
}
