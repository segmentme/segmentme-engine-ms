package io.segmentme.access.service.repository;

import io.segmentme.access.service.domain.context.ContextSchema;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContextSchemaRepository extends MongoRepository<ContextSchema, String> {

}
