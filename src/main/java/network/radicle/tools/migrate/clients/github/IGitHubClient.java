package network.radicle.tools.migrate.clients.github;

import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.core.github.GitHubComment;
import network.radicle.tools.migrate.core.github.GitHubCommit;
import network.radicle.tools.migrate.core.github.GitHubEvent;
import network.radicle.tools.migrate.core.github.GitHubIssue;

import java.util.List;

public interface IGitHubClient {
    List<GitHubIssue> getIssues(int page, Config.Filters since) throws Exception;

    List<GitHubComment> getComments(long issueNumber, int page) throws Exception;

    List<GitHubEvent> getEvents(long issueNumber, int page, boolean timeline) throws Exception;

    GitHubCommit getCommit(String commitId) throws Exception;

    String getAssetOrFile(String url);

    String execSubtreeCmd(String owner, String repo, String token, String path);
}
