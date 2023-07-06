package network.radicle.tools.github.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import network.radicle.tools.github.utils.Markdown;

import java.time.Instant;
import java.util.ArrayList;

import static network.radicle.tools.github.core.github.Issue.METADATA_TITLE;
import static network.radicle.tools.github.utils.Markdown.escape;
import static network.radicle.tools.github.utils.Markdown.link;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment extends Timeline {

    @JsonProperty("id")
    public Long id;
    @JsonProperty("node_id")
    public String nodeId;
    @JsonProperty("url")
    public String url;
    @JsonProperty("html_url")
    public String htmlUrl;
    @JsonProperty("body")
    public String body;
    @JsonProperty("user")
    public User user;
    @JsonProperty("created_at")
    public Instant createdAt;
    @JsonProperty("updated_at")
    public Instant updatedAt;
    @JsonProperty("issue_url")
    public String issueUrl;
    @JsonProperty("author_association")
    public String authorAssociation;

    public String getMetadata() {
        var metadata = new Markdown().openDropDown(METADATA_TITLE);

        var headers = new ArrayList<>();
        headers.add("Number");
        headers.add("Created On");
        headers.add("Created By");
        metadata.openTable(headers.toArray());

        var rows = new ArrayList<>();
        rows.add(link(String.valueOf(this.id), this.htmlUrl));
        rows.add(escape(DTF.format(this.createdAt)));
        rows.add(link(this.user.login, this.user.htmlUrl));

        metadata.addTableRow(rows.toArray());
        metadata.closeTable().closeDropDown();

        return metadata.build();
    }

    public String getBody() {
        return this.body;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", nodeId='" + nodeId + '\'' +
                ", url='" + url + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", body='" + body + '\'' +
                ", user=" + user +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", issueUrl='" + issueUrl + '\'' +
                ", authorAssociation='" + authorAssociation + '\'' +
                '}';
    }

    @Override
    public String getType() {
        return "comment";
    }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }
}
