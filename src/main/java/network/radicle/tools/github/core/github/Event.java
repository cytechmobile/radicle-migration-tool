package network.radicle.tools.github.core.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event extends Timeline {

    public enum Type {
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
    public User actor;
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
    public Issue issue;

    //type specific attributes for `assigned`, `unassigned`
    @JsonProperty("assignee")
    public User assignee;
    @JsonProperty("assigner")
    public User assigner;

    //type specific attributes for `cross-referenced`
    @JsonProperty("source")
    public Source source;

    //type specific attributes for `milestoned`, `demilestoned`
    @JsonProperty("milestone")
    public Milestone milestone;

    //type specific attributes for `labeled`, `unlabeled`
    @JsonProperty("label")
    public Label label;

    //type specific attributes for `renamed`
    @JsonProperty("rename")
    public Rename rename;

    //type specific attributes for `closed`
    @JsonProperty("state_reason")
    public String stateReason;

    //type specific attributes for `referenced`
    @JsonProperty("commit")
    public Commit commit;

    public String getMetadata() {
        return null;
    }

    public String getBody() {
        var body = new ArrayList<String>();
        //actor avatar
        //todo: image resizing is not permitted in markdown. check alternatives
        //body.add("![" + this.actor.login + "](" + this.actor.avatarUrl + ")");

        //actor profile link
        if (!Type.CROSS_REFERENCED.value.equals(event)) {
            body.add("**[" + this.actor.login + "](" + this.actor.htmlUrl + ")**");
        }

        switch (Type.from(event)) {
            case ASSIGNED, UNASSIGNED -> {
                body.add(this.event);
                body.add("**[" + this.assignee.login + "](" + this.assignee.htmlUrl + ")**");
            }
            case LABELED -> {
                body.add("added the");
                body.add("**" + this.label.name + "**");
                body.add("label");
            }
            case UNLABELED -> {
                body.add("removed the");
                body.add("**" + this.label.name + "**");
                body.add("label");
            }
            case MILESTONED -> {
                body.add("added this to the");
                body.add("**" + this.milestone.title + "**");
                body.add("milestone");
            }
            case DEMILESTONED -> {
                body.add("removed this from the");
                body.add("**" + this.milestone.title + "**");
                body.add("milestone");
            }
            case CLOSED -> {
                body.add("closed this");
                if (!Strings.isNullOrEmpty(this.stateReason)) {
                    body.add("as " + this.stateReason);
                }
                if (commit != null) {
                    var message = this.commit.metadata.message.split("\n")[0];
                    body.add("in **[" + message + "](" + this.commit.htmlUrl + ")**");
                }
            }
            case REOPENED -> body.add("reopened this");
            case RENAMED -> {
                body.add("changed the title");
                body.add("~~" + this.rename.from + "~~");
                body.add("**" + this.rename.to + "**");
            }
            case CROSS_REFERENCED -> {
                body.add("This was referenced by");
                body.add("**[" + this.source.issue.title + "#" + this.source.issue.number + "](" +
                        this.source.issue.htmlUrl + ")**");
            }
            case MENTIONED -> body.add("was mentioned");
            case REFERENCED -> {
                body.add("added the commit");
                var message = this.commit.metadata.message.split("\n")[0];
                body.add("**[" + message + "](" + this.commit.htmlUrl + ")**");
                body.add("that referenced this issue");
            }
            /*case CONNECTED -> {
                body.add("linked the pull request");
                body.add("**[" + this.source.issue.title + "#" + this.source.issue.number + "](" + this.source.issue.htmlUrl + ")**");
            }
            case DISCONNECTED -> {
                body.add("removed a link to the pull request");
                body.add("**[" + this.source.issue.title + "#" + this.source.issue.number + "](" + this.source.issue.htmlUrl + ")**");
            }*/
            case UNSUPPORTED -> body.add(this.event);
            default -> body.add(this.event);
        }

        //escape : to properly get displayed in radicle web app
        body.add("on " + DTF.format(this.createdAt).replace(":", "\\:"));
        return String.join(" ", body);
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

