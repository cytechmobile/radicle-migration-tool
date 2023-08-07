package network.radicle.tools.github.services;

import network.radicle.tools.github.core.radicle.Session;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SshAgentServiceTest {

    private final static String SESSION_ID = "tuOsGrjR8iqXBM3lLb4ACiDyLkKI1HFk";
    private final static String PUBLIC_KEY = "z6MkkpfXb92R2pTibirh1fJkSVc6UtpJxrS8k8g3zNftsXL3";
    private final static String SIGNATURE = "z3LwYFBFWbrMoVxPb5RUFsphmBK71amUKgBwSJT9pHgGmeRPCCUxdx8WhEWAjwQghRXqwYuPFx39wdb7bEB2SJpjC";

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
