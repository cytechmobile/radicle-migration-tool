package network.radicle.tools.github.services;

import com.google.common.base.Strings;
import jakarta.inject.Inject;

import java.time.Instant;

public abstract class AbstractMigrationService {

    @Inject
    AppStateService appStateService;

    public abstract String getLastRunPropertyName();

    public Instant getLastRun() {
        var lastRunPropertyName = appStateService.getProperty(getLastRunPropertyName());
        return Strings.isNullOrEmpty(lastRunPropertyName) ?
            Instant.EPOCH : Instant.ofEpochMilli(Long.parseLong(lastRunPropertyName));
    }

    public void setLastRun(Instant lastRun) {
        appStateService.setProperty(getLastRunPropertyName(), String.valueOf(lastRun.toEpochMilli()));
    }
}
