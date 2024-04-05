package network.radicle.tools.migrate.clients.radicle;

import network.radicle.tools.migrate.core.radicle.Issue;
import network.radicle.tools.migrate.core.radicle.Session;
import network.radicle.tools.migrate.core.radicle.actions.Action;

public interface IRadicleClient {
    Session createSession() throws Exception;
    String createIssue(Session session, Issue issue) throws Exception;
    boolean updateIssue(Session session, String id, Action action) throws Exception;
}
