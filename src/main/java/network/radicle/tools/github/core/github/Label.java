
package network.radicle.tools.github.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Label {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("node_id")
    public String nodeId;
    @JsonProperty("url")
    public String url;
    @JsonProperty("name")
    public String name;
    @JsonProperty("description")
    public String description;
    @JsonProperty("color")
    public String color;
    @JsonProperty("default")
    public Boolean isDefault;

    @Override
    public String toString() {
        return "Label{" +
                "id=" + id +
                ", nodeId='" + nodeId + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", color='" + color + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
