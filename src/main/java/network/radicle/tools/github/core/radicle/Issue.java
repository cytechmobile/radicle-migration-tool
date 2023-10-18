package network.radicle.tools.github.core.radicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Issue {
    @JsonProperty("id")
    public String id;
    @JsonProperty("author")
    public Author author;
    @JsonProperty("assignees")
    public List<String> assignees;
    @JsonProperty("title")
    public String title;
    @JsonProperty("description")
    public String description;
    @JsonProperty("state")
    public State state;
    @JsonProperty("discussion")
    public List<Discussion> discussion;
    @JsonProperty("labels")
    public List<String> labels;
    @JsonProperty("embeds")
    public List<Embed> embeds;

    @Override
    public String toString() {
        return "Issue{" +
                "id='" + id + '\'' +
                ", author=" + author +
                ", assignees=" + assignees +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", state=" + state +
                ", discussion=" + discussion +
                ", labels=" + labels +
                ", embeds=" + embeds +
                '}';
    }
}
