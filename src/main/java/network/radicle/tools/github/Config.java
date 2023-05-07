package network.radicle.tools.github;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Config {
    private String token;
    private String url;
    private String version;
    private String owner;
    private String repo;
    private int pageSize;

    public Config() {
    }

    public Config(String token, String url, String version, String owner, String repo, int pageSize) {
        this.token = token;
        this.url = url;
        this.version = version;
        this.owner = owner;
        this.repo = repo;
        this.pageSize = pageSize;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "Config {" +
                "url='" + url + '\'' +
                ", version='" + version + '\'' +
                ", owner='" + owner + '\'' +
                ", repo='" + repo + '\'' +
                '}';
    }

}
