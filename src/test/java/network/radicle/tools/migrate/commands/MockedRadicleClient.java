package network.radicle.tools.migrate.commands;

import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Singleton;
import network.radicle.tools.migrate.clients.radicle.IRadicleClient;
import network.radicle.tools.migrate.core.radicle.Session;
import network.radicle.tools.migrate.core.radicle.actions.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Alternative
@Singleton
public class MockedRadicleClient implements IRadicleClient {
    private static final Logger logger = LoggerFactory.getLogger(MockedRadicleClient.class);

    @Override
    public Session createSession() {
        var session = new Session();
        session.id = UUID.randomUUID().toString();
        return session;
    }

    @Override
    public String createIssue(Session session, network.radicle.tools.migrate.core.radicle.Issue issue) {
        var id = UUID.randomUUID().toString();
        return id;
    }

    @Override
    public boolean updateIssue(Session session, String id, Action action) {
        return true;
    }
}
