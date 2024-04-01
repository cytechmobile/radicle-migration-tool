package network.radicle.tools.migrate.options;

import picocli.CommandLine;

public class Generic {
    @CommandLine.Option(
            names = {"-dr", "--dry-run"},
            order = 200,
            defaultValue = "${DRY_RUN:false}",
            description = "Run the whole migration process without actually creating the issues in the target Radicle project.")
    public boolean dryRun;
}
