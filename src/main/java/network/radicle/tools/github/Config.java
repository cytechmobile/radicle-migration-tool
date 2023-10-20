package network.radicle.tools.github;

import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import network.radicle.tools.github.commands.Command.State;

import java.net.URL;
import java.time.Instant;

@ApplicationScoped
public class Config {
    private GitHubConfig github;
    private RadicleConfig radicle;

    public Config() {
    }

    public GitHubConfig getGithub() {
        return github;
    }

    public RadicleConfig getRadicle() {
        return radicle;
    }

    public void setGithub(GitHubConfig github) {
        this.github = github;
    }

    public void setRadicle(RadicleConfig radicle) {
        this.radicle = radicle;
    }

    @Override
    public String toString() {
        return "Config{" +
                "github=" + github +
                ", radicle=" + radicle +
                '}';
    }

    public record GitHubConfig(String session, String token, URL url, String version, String owner, String repo,
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
    public record RadicleConfig(URL url, String version, String project, Boolean dryRun) { }

    public record Filters(Instant since, String labels, State state, Integer milestone, String assignee,
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
