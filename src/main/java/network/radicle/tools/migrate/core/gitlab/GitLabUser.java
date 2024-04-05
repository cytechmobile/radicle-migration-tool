
package network.radicle.tools.migrate.core.gitlab;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitLabUser {

    @JsonProperty("state")
    public String state;
    @JsonProperty("id")
    public Long id;
    @JsonProperty("web_url")
    public String webUrl;
    @JsonProperty("name")
    public String name;
    @JsonProperty("avatar_url")
    public String avatarUrl;
    @JsonProperty("username")
    public String username;

    @Override
    public String toString() {
        return "User{" +
                "state='" + state + '\'' +
                ", id=" + id +
                ", webUrl='" + webUrl + '\'' +
                ", name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
