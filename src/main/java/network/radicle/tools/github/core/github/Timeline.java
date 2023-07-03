package network.radicle.tools.github.core.github;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public abstract class Timeline {
    public static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss 'UTC'").withZone(ZoneId.of("UTC"));

    public abstract String getType();

    public abstract Instant getCreatedAt();

    public abstract String getMetadata();

    public abstract String getBody();

    public String getBodyWithMetadata() {
        var metadata = this.getMetadata();
        return metadata != null ? metadata + "\n\n" + this.getBody() : this.getBody();
    }
}
