package network.radicle.tools.migrate.core.radicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Session {
    @JsonProperty("sessionId")
    public String id;

    @JsonProperty("status")
    public String status;

    @JsonProperty("publicKey")
    public String publicKey;

    @JsonProperty("signature")
    public String signature;

    @JsonProperty("issuedAt")
    public Long issuedAt;

    @JsonProperty("expiresAt")
    public Long expiresAt;

    @Override
    public String toString() {
        return "Session{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
