package network.radicle.tools.github.services;

import network.radicle.tools.github.core.radicle.Session;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SshAgentServiceTest {

    private final static String SESSION_ID = "FGpMw9qaLEIhRyiGFaAXfbmFlQ2KpEJL";
    private final static String PUBLIC_KEY = "z6Mkeyoeu6AZrxs79RcX5TUdBAhwxQNbfwXEXPQGvXJtVeHU";
    private final static String SIGNATURE = "z5mNmthQNYVReeCfhnP7rfrkB996YMqwzZU1LQxsNiGEPw7buumskoCN3RYC58J5FfAuSgeJ1PJf2U558wNbYq8zr";

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
