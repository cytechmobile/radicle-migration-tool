package network.radicle.tools.migrate.core.gitlab;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import network.radicle.tools.migrate.core.Timeline;

import java.time.Instant;
import java.util.Arrays;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitLabEvent extends Timeline {

    public enum Type {

        COMMENT("comment"),

        //detect them from the body
        ASSIGNED("assigned to"),
        UNASSIGNED("unassigned"),
        CHANGED_DUE_DATE("changed due date"),
        CHANGED_TYPE("changed type"),
        RENAMED("changed title"),

        //get them from separate end points
        STATE("state"),
        MILESTONE("milestone"),
        LABEL("label"),

        MILESTONED("milestoned"),
        DEMILESTONED("demilestoned"),
        LABELED("labeled"),
        UNLABELED("unlabeled"),
        CLOSED("closed"),
        OPENED("opened"),
        REOPENED("reopened"),

        UNSUPPORTED("unsupported");

        public final String value;

        Type(String value) {
            this.value = value;
        }

        public static boolean isValid(String value) {
            return Arrays.stream(GitLabEvent.Type.values()).anyMatch(v -> v.value.equalsIgnoreCase(value));
        }

        public static GitLabEvent.Type from(String value) {
            for (var v : GitLabEvent.Type.values()) {
                if (v.value.equalsIgnoreCase(value)) {
                    return v;
                }
            }
            return UNSUPPORTED;
        }
    }

    @JsonProperty("id")
    public Long id;
    @JsonProperty("user")
    public GitLabUser user;
    @JsonProperty("created_at")
    public Instant createdAt;
    @JsonProperty("resource_type")
    public String resourceType;
    @JsonProperty("resource_id")
    public Long resourceId;
    @JsonProperty("milestone")
    public GitLabMilestone milestone;
    @JsonProperty("label")
    public GitLabLabel label;
    @JsonProperty("state")
    public String state;
    @JsonProperty("action")
    public String action;

    @Override
    public String getType() {
        if (this.milestone != null) {
            return action.equalsIgnoreCase("add") ? Type.MILESTONED.value : Type.DEMILESTONED.value;
        } else if (this.label != null) {
            return action.equalsIgnoreCase("add") ? Type.LABELED.value : Type.UNLABELED.value;
        } else if (this.state != null) {
            return state.equalsIgnoreCase("closed") ? Type.CLOSED.value :
                    state.equalsIgnoreCase("reopened") ? Type.REOPENED.value :
                            Type.OPENED.value;
        }
        return Type.UNSUPPORTED.value;
    }

    @Override
    public Instant getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public String getBody() {
        return "";
    }
}
