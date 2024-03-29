package network.radicle.tools.migrate.clients;

import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.core.github.Comment;
import network.radicle.tools.migrate.core.github.Commit;
import network.radicle.tools.migrate.core.github.Event;
import network.radicle.tools.migrate.core.github.Issue;

import java.util.List;

public interface IGitHubClient {
    List<Issue> getIssues(int page, Config.Filters since) throws Exception;

    List<Comment> getComments(long issueNumber, int page) throws Exception;

    List<Event> getEvents(long issueNumber, int page, boolean timeline) throws Exception;

    Commit getCommit(String commitId) throws Exception;

    String getAssetOrFile(String url);

    String execSubtreeCmd(String owner, String repo, String token, String path);
}
