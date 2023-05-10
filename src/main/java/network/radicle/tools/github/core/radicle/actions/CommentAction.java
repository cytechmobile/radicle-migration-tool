package network.radicle.tools.github.core.radicle.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.HashMap;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentAction extends ThreadAction {

    public CommentAction(String body, String replyTo) {
        super(new HashMap<>() {{
            put("type", "comment");
            put("body", body);
            put("replyTo", replyTo);
        }});
    }

    public CommentAction() {
    }

    public CommentAction(String body) {
        this(body, null);
    }

    @Override
    public String toString() {
        return "CommentAction{" +
                "type='" + type + '\'' +
                ", action=" + action +
                '}';
    }
}
