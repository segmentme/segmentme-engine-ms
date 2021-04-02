package io.segmentme.web.configuration;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthUser {

    @JsonAlias("https://segmentme.io:userId")
    private String id;

    @JsonAlias("https://segmentme.io:fullName")
    private String fullName;

    @JsonAlias("https://segmentme.io:email")
    private String email;

    @JsonAlias("https://segmentme.io:acknowledged")
    private boolean acknowledged;

    @JsonAlias("sub")
    private String externalId;

}
