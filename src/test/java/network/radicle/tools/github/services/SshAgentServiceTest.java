package network.radicle.tools.github.services;

import network.radicle.tools.github.core.radicle.Session;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SshAgentServiceTest {

    private final static String SESSION_ID = "GdPg6opLHgS9VBTdaV1ACESEc9wG2W68";
    private final static String PUBLIC_KEY = "z6Mkf2deXEQBXenu829pKGcvSutjko6g7L6J4bapiBaJ1NYk";
    private final static String SIGNATURE = "z5tCYabHbMxz3mSwBbhdHSxP7XZnZHQjAciHTYy6gGHTarUksye9grJR3rs9grYnUtZ7DE7woGQDLxUarvDioY7BM";

    @Test
    public void testSignSessionUsingTheLocalAgent() {
        var agent = new SshAgentService();
        var session = new Session();

        session.id = SESSION_ID;
        session.publicKey = PUBLIC_KEY;

        var signature = agent.sign(session);
        assertThat(signature).isEqualTo(SIGNATURE);
    }

}
