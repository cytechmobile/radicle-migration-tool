package network.radicle.tools.migrate.options;

import picocli.CommandLine;

public class RadicleRepo {
    @CommandLine.Option(
            names = {"-rp", "--radicle-project"},
            order = 90,
            required = true,
            defaultValue = "${RAD_PROJECT}",
            description = "The target Radicle project.")
    public String rProject;

    @CommandLine.Option(
            names = {"-rh", "--radicle-passphrase"},
            order = 100,
            required = true,
            interactive = true,
            defaultValue = "${RAD_PASSPHRASE}",
            description = "Your radicle passphrase.")
    public String rPassphrase;
}
