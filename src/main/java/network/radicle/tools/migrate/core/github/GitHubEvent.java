package network.radicle.tools.migrate.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import network.radicle.tools.migrate.core.Timeline;

import java.time.Instant;
import java.util.Arrays;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitHubEvent extends Timeline {

    public enum Type {
        COMMENT("comment"),
        ASSIGNED("assigned"),
        UNASSIGNED("unassigned"),
        CLOSED("closed"),
        REOPENED("reopened"),
        CROSS_REFERENCED("cross-referenced"),
        MILESTONED("milestoned"),
        DEMILESTONED("demilestoned"),
        LABELED("labeled"),
        UNLABELED("unlabeled"),
        MENTIONED("mentioned"),
        REFERENCED("referenced"),
        RENAMED("renamed"),
        //CONNECTED("connected"),
        //DISCONNECTED("disconnected"),
        UNSUPPORTED("unsupported");

        public final String value;

        Type(String value) {
            this.value = value;
        }

        public static boolean isValid(String value) {
            return Arrays.stream(Type.values()).anyMatch(v -> v.value.equalsIgnoreCase(value));
        }

        public static Type from(String value) {
            for (var v : Type.values()) {
                if (v.value.equalsIgnoreCase(value)) {
                    return v;
                }
            }
            return UNSUPPORTED;
        }
    }

    @JsonProperty("id")
    public Long id;
    @JsonProperty("node_id")
    public String nodeId;
    @JsonProperty("url")
    public String url;
    @JsonProperty("actor")
    public GitHubUser actor;
    @JsonProperty("event")
    public String event;
    @JsonProperty("commit_id")
    public String commitId;
    @JsonProperty("commit_url")
    public String commitUrl;
    @JsonProperty("created_at")
    public Instant createdAt;

    //type specific
    @JsonProperty("issue")
    public GitHubIssue issue;

    //type specific attributes for `assigned`, `unassigned`
    @JsonProperty("assignee")
    public GitHubUser assignee;
    @JsonProperty("assigner")
    public GitHubUser assigner;

    //type specific attributes for `cross-referenced`
    @JsonProperty("source")
    public GitHubSource source;

    //type specific attributes for `milestoned`, `demilestoned`
    @JsonProperty("milestone")
    public GitHubMilestone milestone;

    //type specific attributes for `labeled`, `unlabeled`
    @JsonProperty("label")
    public GitHubLabel label;

    //type specific attributes for `renamed`
    @JsonProperty("rename")
    public GitHubRename rename;

    //type specific attributes for `closed`
    @JsonProperty("state_reason")
    public String stateReason;

    //type specific attributes for `referenced`
    @JsonProperty("commit")
    public GitHubCommit commit;

    @Override
    public String getBody() {
        return "";
    }

    @Override
    public String getType() {
        return this.event;
    }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", nodeId='" + nodeId + '\'' +
                ", url='" + url + '\'' +
                ", actor=" + actor +
                ", event='" + event + '\'' +
                ", commitId='" + commitId + '\'' +
                ", commitUrl='" + commitUrl + '\'' +
                ", createdAt=" + createdAt +
                ", issue=" + issue +
                ", assignee=" + assignee +
                ", assigner=" + assigner +
                ", source=" + source +
                ", milestone=" + milestone +
                ", label=" + label +
                ", rename=" + rename +
                '}';
    }
}

