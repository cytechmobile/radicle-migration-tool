
package network.radicle.tools.github.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PullRequest {
    @JsonProperty("url")
    public String url;
    @JsonProperty("html_url")
    public String htmlUrl;
    @JsonProperty("diff_url")
    public String diffUrl;
    @JsonProperty("patch_url")
    public String patchUrl;

}
