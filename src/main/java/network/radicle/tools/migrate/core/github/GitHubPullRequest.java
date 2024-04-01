
package network.radicle.tools.migrate.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitHubPullRequest {
    @JsonProperty("url")
    public String url;
    @JsonProperty("html_url")
    public String htmlUrl;
    @JsonProperty("diff_url")
    public String diffUrl;
    @JsonProperty("patch_url")
    public String patchUrl;

    @Override
    public String toString() {
        return "PullRequest{" +
                "url='" + url + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", diffUrl='" + diffUrl + '\'' +
                ", patchUrl='" + patchUrl + '\'' +
                '}';
    }
}
