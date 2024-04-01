package network.radicle.tools.migrate.core.github;

import java.time.Instant;

public abstract class Timeline {

    public abstract String getType();

    public abstract Instant getCreatedAt();

    public abstract String getBody();
}
