package network.radicle.tools.migrate;

import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import network.radicle.tools.migrate.commands.Command.State;

import java.net.URL;
import java.time.Instant;

@ApplicationScoped
public class Config {
    private GitLabConfig gitlab;
    private GitHubConfig github;
    private RadicleConfig radicle;

    public Config() {
    }

    public Config(RadicleConfig radConfig, GitHubConfig ghConfig, GitLabConfig glConfig) {
        this.radicle = radConfig;
        this.github = ghConfig;
        this.gitlab = glConfig;
    }

    public GitHubConfig github() {
        return github;
    }

    public GitLabConfig gitlab() {
        return gitlab;
    }

    public RadicleConfig radicle() {
        return radicle;
    }

    public Config github(GitHubConfig config) {
        this.github = config;
        return this;
    }

    public Config gitlab(GitLabConfig config) {
        this.gitlab = config;
        return this;
    }

    public Config radicle(RadicleConfig config) {
        this.radicle = config;
        return this;
    }

    @Override
    public String toString() {
        return "Config{" +
                "github=" + github +
                "gitlab=" + gitlab +
                ", radicle=" + radicle +
                '}';
    }

    public record GitHubConfig(String domain, String session, String token, URL url, String version, String owner, String repo,
                               Filters filters, int pageSize) {
        @Override
        public String toString() {
            return "GitHubConfig{" +
                    "url=" + url +
                    ", version='" + version + '\'' +
                    ", owner='" + owner + '\'' +
                    ", repo='" + repo + '\'' +
                    ", filters=" + filters +
                    ", pageSize=" + pageSize +
                    '}';
        }
    }

    public record GitLabConfig(String domain, String session, String token, URL url, String version, String namespace, String project,
                               Filters filters, int pageSize) {
        @Override
        public String toString() {
            return "GitLabConfig{" +
                    "domain='" + domain + '\'' +
                    ", url=" + url +
                    ", version='" + version + '\'' +
                    ", namespace='" + namespace + '\'' +
                    ", project='" + project + '\'' +
                    ", filters=" + filters +
                    ", pageSize=" + pageSize +
                    '}';
        }
    }

    public record RadicleConfig(URL url, String version, String project, String passphrase, String path,
                                Boolean dryRun) {

        @Override
        public String toString() {
            return "RadicleConfig{" +
                    "url=" + url +
                    ", version='" + version + '\'' +
                    ", project='" + project + '\'' +
                    ", path='" + path + '\'' +
                    ", dryRun=" + dryRun +
                    '}';
        }
    }

    public record Filters(Instant since, String labels, State state, String milestone, String assignee,
                          String creator) {
        public Filters withSince(Instant s) {
            return new Filters(s, labels(), state(), milestone(), assignee(), creator());
        }

        @Override
        public String toString() {
            return "[" +
                    "since='" + (since != null ? since : "") + '\'' +
                    ", labels='" + Strings.nullToEmpty(labels) + '\'' +
                    ", state='" + state + '\'' +
                    ", milestone='" + (milestone != null ? milestone : "") + '\'' +
                    ", assignee='" + Strings.nullToEmpty(assignee) + '\'' +
                    ", creator='" + Strings.nullToEmpty(creator) + '\'' +
                    ']';
        }
    }

}
