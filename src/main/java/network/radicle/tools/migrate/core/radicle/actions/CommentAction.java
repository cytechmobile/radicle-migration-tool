package network.radicle.tools.migrate.core.radicle.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;
import network.radicle.tools.migrate.core.radicle.Embed;

import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentAction extends Action {

    public String body;
    public String replyTo;
    public List<Embed> embeds;

    public CommentAction(String body, List<Embed> embeds, String replyTo) {
        this.type = "comment";
        this.body = body;
        this.embeds = embeds;
        this.replyTo = replyTo;
    }

    public CommentAction() {
    }

    @Override
    public String toString() {
        return "CommentAction{" +
                "body='" + body + '\'' +
                ", replyTo='" + replyTo + '\'' +
                ", embeds=" + embeds +
                ", type='" + type + '\'' +
                '}';
    }
}
