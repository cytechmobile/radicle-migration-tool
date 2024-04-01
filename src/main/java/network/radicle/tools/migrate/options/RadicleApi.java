package network.radicle.tools.migrate.options;

import picocli.CommandLine;

import java.net.URL;

public class RadicleApi {
    @CommandLine.Option(
            names = {"-rv", "--radicle-api-version"},
            order = 70,
            defaultValue = "${RAD_API_VERSION:-v1}",
            description = "The version of the Radicle HTTP API (default: v1).")
    public String rVersion;

    @CommandLine.Option(
            names = {"-ru", "--radicle-api-url"},
            order = 80,
            defaultValue = "${RAD_API_URL:-http://localhost:8080/api}",
            description = "The base url of Radicle HTTP API (default: http://localhost:8080/api).")
    public URL rUrl;
}
