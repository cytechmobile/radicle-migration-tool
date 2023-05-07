package network.radicle.tools.github.clients;

import network.radicle.tools.github.core.GitHubIssue;

import java.util.List;

public interface IGitHubClient {
    List<GitHubIssue> getIssues(int page) throws Exception;
}
