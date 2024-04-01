package network.radicle.tools.migrate.commands.gitlab;

import jakarta.enterprise.context.Dependent;
import picocli.CommandLine;

@Dependent
@CommandLine.Command(
        name = "gitlab",
        description = "Migrate from a GitLab repository to a Radicle project.",
        headerHeading = "@|bold,underline Usage|@:%n",
        synopsisHeading = "",
        descriptionHeading = "%n@|bold,underline Description|@:%n",
        parameterListHeading = "%n@|bold,underline Parameters|@:%n",
        optionListHeading = "%n@|bold,underline Options|@:%n")
public class GitLabCommand {
}
