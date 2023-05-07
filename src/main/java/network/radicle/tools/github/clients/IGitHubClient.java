package network.radicle.tools.github.clients;

import network.radicle.tools.github.core.Issue;

import java.util.List;

public interface IGitHubClient {
    List<Issue> getIssues(int page) throws Exception;
}
