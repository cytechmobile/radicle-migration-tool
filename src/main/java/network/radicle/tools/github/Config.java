package network.radicle.tools.github;

import jakarta.enterprise.context.ApplicationScoped;

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

    public record GitHubConfig(String token, String url, String version, String owner, String repo, String since,
                               String labels, String state, String milestone, String assignee, String creator,
                               int pageSize) {
        @Override
        public String toString() {
            return "GitHubConfig{" +
                    "url='" + url + '\'' +
                    ", version='" + version + '\'' +
                    ", owner='" + owner + '\'' +
                    ", repo='" + repo + '\'' +
                    ", since='" + since + '\'' +
                    ", labels='" + labels + '\'' +
                    ", state='" + state + '\'' +
                    ", milestone='" + milestone + '\'' +
                    ", assignee='" + assignee + '\'' +
                    ", creator='" + creator + '\'' +
                    ", pageSize=" + pageSize +
                    '}';
        }
    }
    public record RadicleConfig(String url, String version, String project) { }

}
