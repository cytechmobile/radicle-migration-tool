package network.radicle.tools.github.core.radicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Embed {
    @JsonProperty("oid")
    public String oid;

    @JsonProperty("name")
    public String name;

    @JsonProperty("content")
    public String content;

    public Embed() {
    }

    public Embed(String name, String content) {
        this.oid = null;
        this.name = name;
        this.content = content;
    }

    public Embed(String oid, String name, String content) {
        this.oid = oid;
        this.name = name;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Embed{" +
                "oid='" + oid + '\'' +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
