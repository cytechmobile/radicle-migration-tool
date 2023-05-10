package network.radicle.tools.github.clients;

import network.radicle.tools.github.core.radicle.actions.Action;
import network.radicle.tools.github.core.radicle.Issue;
import network.radicle.tools.github.core.radicle.Session;

public interface IRadicleClient {
    Session createSession() throws Exception;
    String createIssue(Session session, Issue issue) throws Exception;
    boolean updateIssue(Session session, String id, Action action) throws Exception;
}
