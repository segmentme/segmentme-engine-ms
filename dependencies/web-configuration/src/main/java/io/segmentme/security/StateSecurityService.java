package io.segmentme.security;

public interface StateSecurityService {
    boolean isManagedState(String stateId, String userId);
}
