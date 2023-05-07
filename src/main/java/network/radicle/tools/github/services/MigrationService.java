package network.radicle.tools.github.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.clients.IGitHubClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MigrationService {
    private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);

    @Inject IGitHubClient github;
    @Inject Config config;

    public boolean migrateIssues() {
        var page = 1;
        var hasMorePages = true;
        var total = 0;

        try {
            while (hasMorePages) {
                logger.info("Migrating page {}", page);

                var issues = github.getIssues(page);
                var batchSize = issues.size();
                total += batchSize;

                logger.info("Fetched page {} with {} issues. Total fetched {}", page, batchSize, total);

                // todo: migrate to radicle here

                hasMorePages = config.getPageSize() == issues.size();
                page++;
            }
            logger.info("Migrated {} issues in total", total);
            return true;
        } catch (Exception ex) {
            logger.error("Migration failed with error message: {}", ex.getMessage());
            return false;
        }
    }

}
