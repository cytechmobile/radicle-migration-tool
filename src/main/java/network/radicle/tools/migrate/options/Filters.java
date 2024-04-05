package network.radicle.tools.migrate.options;

import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.commands.Command;
import network.radicle.tools.migrate.utils.InstantConverter;
import picocli.CommandLine;

import java.time.Instant;

public class Filters {
    @CommandLine.Option(
            converter = InstantConverter.class,
            order = 110,
            names = {"-fs", "--filter-since"},
            defaultValue = "${FILTER_SINCE}",
            description = "Migrate issues created after the given time (default: timestamp of the last run, " +
                    "example: 2023-01-01T10:15:30+01:00).")
    public Instant fSince;

    @CommandLine.Option(
            names = {"-fl", "--filter-labels"},
            order = 120,
            defaultValue = "${FILTER_LABELS}",
            description = "Migrate issues with the given labels given in a csv format (example: bug,ui,@high).")
    public String fLabels;

    @CommandLine.Option(
            names = {"-ft", "--filter-state"},
            order = 130,
            defaultValue = "${FILTER_STATE:-all}",
            description = "Migrate issues in this state (default: all, can be one of: ${COMPLETION-CANDIDATES}).")
    public Command.State fState;

    @CommandLine.Option(
            names = {"-fm", "--filter-milestone"},
            order = 140,
            defaultValue = "${FILTER_MILESTONE}",
            description = "Migrate issues belonging to the given milestone number (example: 3).")
    public String fMilestone;

    @CommandLine.Option(
            names = {"-fa", "--filter-assignee"},
            order = 150,
            defaultValue = "${FILTER_ASSIGNEE}",
            description = "Migrate issues assigned to the given user name. ")
    public String fAssignee;

    @CommandLine.Option(
            names = {"-fc", "--filter-creator"},
            order = 160,
            defaultValue = "${FILTER_CREATOR}",
            description = "Migrate issues created by the given user name.")
    public String fCreator;

    public Config.Filters getFilters() {
        return new Config.Filters(fSince, fLabels, fState, fMilestone, fAssignee, fCreator);
    }
}
