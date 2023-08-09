package network.radicle.tools.github.core.radicle.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentAction extends Action {

    public String body;
    public String replyTo;

    public CommentAction(String body, String replyTo) {
        this.type = "comment";
        this.body = body;
        this.replyTo = replyTo;
    }

    public CommentAction() {
    }

    @Override
    public String toString() {
        return "CommentAction{" +
                "body='" + body + '\'' +
                ", replyTo='" + replyTo + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
