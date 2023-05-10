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

    public record GitHubConfig(String token, String url, String version, String owner, String repo, int pageSize) {
        @Override
        public String toString() {
            return "GitHubConfig{" +
                    "url='" + url + '\'' +
                    ", version='" + version + '\'' +
                    ", owner='" + owner + '\'' +
                    ", repo='" + repo + '\'' +
                    ", pageSize=" + pageSize +
                    '}';
        }
    }
    public record RadicleConfig(String url, String version, String project) {
        @Override
        public String toString() {
            return "RadicleConfig{" +
                    "url='" + url + '\'' +
                    ", version='" + version + '\'' +
                    ", project='" + project + '\'' +
                    '}';
        }
    }

}
