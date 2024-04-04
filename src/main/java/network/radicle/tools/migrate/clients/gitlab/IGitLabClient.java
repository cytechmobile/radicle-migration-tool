package network.radicle.tools.migrate.clients.gitlab;

import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.core.gitlab.GitLabComment;
import network.radicle.tools.migrate.core.gitlab.GitLabEvent;
import network.radicle.tools.migrate.core.gitlab.GitLabEvent.Type;
import network.radicle.tools.migrate.core.gitlab.GitLabIssue;

import java.util.List;

public interface IGitLabClient {
    List<GitLabIssue> getIssues(int page, Config.Filters since) throws Exception;

    List<GitLabComment> getComments(long issueNumber, int page) throws Exception;

    List<GitLabEvent> getEvents(long issueNumber, int page, Type type) throws Exception;

    String getAssetOrFile(String url);
}
