package network.radicle.tools.migrate.clients.radicle;

import network.radicle.tools.migrate.core.radicle.Issue;
import network.radicle.tools.migrate.core.radicle.Session;
import network.radicle.tools.migrate.core.radicle.actions.Action;

import java.util.List;

public interface IRadicleClient {
    Session createSession() throws Exception;
    List<Issue> getIssues(Session session, String state) throws Exception;
    String createIssue(Session session, Issue issue) throws Exception;
    boolean updateIssue(Session session, String id, Action action) throws Exception;
}
