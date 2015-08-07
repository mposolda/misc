package org.mposolda;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class GoogleRequestToken {

    @JsonProperty("iss")
    private String issuer;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("aud")
    private String audience;

    @JsonProperty("sub")
    private String subject;

    @JsonProperty("exp")
    private Long expiration;

    @JsonProperty("iat")
    private Long issuedAt;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public Long getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Long issuedAt) {
        this.issuedAt = issuedAt;
    }
}
