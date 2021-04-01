package io.segmentme.security;

public interface IntegrationPointKeySecurityService {

    boolean isManaged(String userId, String... integrationPointKeys);
}
