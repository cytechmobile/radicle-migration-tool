package network.radicle.tools.github;

public record GitHubConfig(String token, String url, String version,
                           String owner, String repo) {

    @Override
    public String toString() {
        return "GitHubConfig {" +
                "url='" + url + '\'' +
                ", version='" + version + '\'' +
                ", owner='" + owner + '\'' +
                ", repo='" + repo + '\'' +
                '}';
    }
}
