package io.segmentme.access.service.service;

import io.segmentme.access.service.domain.context.ContextSchema;
import io.segmentme.access.service.repository.ContextSchemaRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContextSchemaService extends AbstractDatabaseService<ContextSchema, ContextSchemaRepository> {

}
