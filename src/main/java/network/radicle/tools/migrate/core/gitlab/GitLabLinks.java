
package network.radicle.tools.migrate.core.gitlab;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitLabLinks {

    @JsonProperty("self")
    public String self;
    @JsonProperty("notes")
    public String notes;
    @JsonProperty("award_emoji")
    public String awardEmoji;
    @JsonProperty("project")
    public String project;
    @JsonProperty("closed_as_duplicate_of")
    public String closedAsDuplicateOf;

    @Override
    public String toString() {
        return "Links{" +
                "self='" + self + '\'' +
                ", notes='" + notes + '\'' +
                ", awardEmoji='" + awardEmoji + '\'' +
                ", project='" + project + '\'' +
                ", closedAsDuplicateOf='" + closedAsDuplicateOf + '\'' +
                '}';
    }
}
