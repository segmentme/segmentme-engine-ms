package io.segmentme.web.configuration;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthUser {

    @JsonAlias("https://dev-j1y3rcr7oreu5sq8:userId")
    private String id;

    @JsonAlias("https://dev-j1y3rcr7oreu5sq8:fullName")
    private String fullName;

    @JsonAlias("https://dev-j1y3rcr7oreu5sq8:email")
    private String email;

    @JsonAlias("https://dev-j1y3rcr7oreu5sq8:acknowledged")
    private boolean acknowledged;

    @JsonAlias("sub")
    private String externalId;

}
