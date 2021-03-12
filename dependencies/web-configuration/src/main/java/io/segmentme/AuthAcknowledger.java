package io.segmentme;

public interface AuthAcknowledger {

    void acknowledgeUser(String id, String email, String fullName);
}
