package network.radicle.tools.github.clients;

import network.radicle.tools.github.Config;
import network.radicle.tools.github.core.github.Comment;
import network.radicle.tools.github.core.github.Commit;
import network.radicle.tools.github.core.github.Event;
import network.radicle.tools.github.core.github.Issue;

import java.util.List;

public interface IGitHubClient {
    List<Issue> getIssues(int page, Config.Filters since) throws Exception;

    List<Comment> getComments(long issueNumber, int page) throws Exception;

    List<Event> getEvents(long issueNumber, int page, boolean timeline) throws Exception;

    Commit getCommit(String commitId) throws Exception;
}
