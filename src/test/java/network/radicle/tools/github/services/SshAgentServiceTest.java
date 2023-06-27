package network.radicle.tools.github.services;

import network.radicle.tools.github.core.radicle.Session;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SshAgentServiceTest {

    private final static String SESSION_ID = "nJEXNXbcgINa1JOeXBzLNCYx5UVxa6Zr";
    private final static String PUBLIC_KEY = "z6Mkeyoeu6AZrxs79RcX5TUdBAhwxQNbfwXEXPQGvXJtVeHU";
    private final static String SIGNATURE = "z5GEN9FjvAhvZk3awpwNBiHXwKoaDUH3kRew1zuXHK6j35j3K1puHg3dHPovVe8QGgfqqBbEakKhsWchBngkqPyaT";

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
