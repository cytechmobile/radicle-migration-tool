package network.radicle.tools.migrate.services;

import com.google.common.base.Strings;
import jakarta.inject.Inject;
import network.radicle.tools.migrate.services.AppStateService.Property;
import network.radicle.tools.migrate.services.AppStateService.Service;

import java.time.Instant;
import java.util.List;

public abstract class AbstractMigrationService {

    @Inject AppStateService appStateService;

    public Instant getLastRun(Service service) {
        var lastRun = appStateService.getProperty(service, Property.LAST_RUN);
        return Strings.isNullOrEmpty(lastRun) ?
            Instant.EPOCH : Instant.ofEpochMilli(Long.parseLong(lastRun));
    }

    public void setLastRun(Service service, Instant lastRun) {
        appStateService.setProperty(service, Property.LAST_RUN, String.valueOf(lastRun.toEpochMilli()));
    }

    public String getRadIssueId(Service service, String sourceIssueId) {
        var radIssueId = appStateService.getProperty(service, Property.MAP_COMMENT, sourceIssueId);
        return Strings.emptyToNull(radIssueId);
    }

    public void setRadIssueId(Service service, String sourceIssueId, String radIssueId) {
        appStateService.setProperty(service, Property.MAP_COMMENT, radIssueId, sourceIssueId);
    }

    protected String addEmbedsInline(List<MarkdownService.MarkdownLink> links, String body) {
        for (var link : links) {
            if (!Strings.isNullOrEmpty(link.oid)) {
                body = body.replace(link.url, link.oid);
            }
        }
        return body;
    }
}
