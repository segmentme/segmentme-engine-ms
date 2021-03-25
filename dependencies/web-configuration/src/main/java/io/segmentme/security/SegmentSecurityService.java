package io.segmentme.security;

public interface SegmentSecurityService {
    boolean isManaged(String segmentId, String userId);
}
