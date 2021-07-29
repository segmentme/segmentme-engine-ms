package io.segmentme.management.service.service.context;

import io.segmentme.core.db.service.AbstractDatabaseService;
import io.segmentme.management.service.domain.context.ContextSchema;
import io.segmentme.management.service.repository.ContextSchemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContextSchemaService extends AbstractDatabaseService<ContextSchema, ContextSchemaRepository> {

    public Optional<ContextSchema> findByHashAndIntegrationPointKey(String integrationPointKey, String hash) {
        return repository.findByIntegrationPointKeyAndHash(integrationPointKey, hash);
    }


    public List<ContextSchema> findByIntegrationPointKeys(Collection<String> integrationPointKeys, boolean shortForm) {
        return shortForm ? repository.findShortFormByIntegrationPointKeyIn(integrationPointKeys) : repository.findByIntegrationPointKeyIn(integrationPointKeys);
    }
}
