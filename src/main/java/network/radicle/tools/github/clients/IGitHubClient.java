package network.radicle.tools.github.clients;

import network.radicle.tools.github.core.github.Comment;
import network.radicle.tools.github.core.github.Issue;

import java.util.List;

public interface IGitHubClient {
    List<Issue> getIssues(int page) throws Exception;
    List<Comment> getComments(long issueId, int page) throws Exception;
}
