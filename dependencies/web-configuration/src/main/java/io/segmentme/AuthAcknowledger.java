package io.segmentme;

public interface AuthAcknowledger {

    String acknowledgeUser(String id, String email, String fullName);
}
