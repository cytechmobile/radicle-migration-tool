package network.radicle.tools.migrate.core.radicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Discussion {
    @JsonProperty("id")
    public String id;
    @JsonProperty("author")
    public Author author;
    @JsonProperty("body")
    public String body;
    @JsonProperty("reactions")
    public List<Reaction> reactions;
    @JsonProperty("embeds")
    public List<Embed> embeds;
    @JsonProperty("timestamp")
    public String timestamp;
    @JsonProperty("replyTo")
    public String replyTo;

    @Override
    public String toString() {
        return "Discussion{" +
                "id='" + id + '\'' +
                ", author=" + author +
                ", body='" + body + '\'' +
                ", reactions=" + reactions +
                ", embeds=" + embeds +
                ", timestamp='" + timestamp + '\'' +
                ", replyTo='" + replyTo + '\'' +
                '}';
    }
}
