package network.radicle.tools.github.services;

import network.radicle.tools.github.Config;
import network.radicle.tools.github.Config.RadicleConfig;
import network.radicle.tools.github.core.radicle.Session;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class AuthServiceTest {
    private final static String SESSION_ID = "O5uUlwXVtqriZ8qSjstNnMUaVkqjhw1G";
    private final static String PUBLIC_KEY = "z6Mkn31Jx243wXDJMAC5rNQuLDKJp5Rfe3tXr7wXxHhQqaGd";
    private final static String SIGNATURE = "z3oJuT3cPvNXopAjxWWMAB3qQfd7PkRoAGGnyvxqcbViYuvdmoYpZSVRMuSrTPADkUNmdb1CdeBLr3D75JUh4WLiW";

    @Test
    void testSessionSign() throws FileNotFoundException {
        var authService = new AuthService();
        authService.config = new Config();
        authService.config.setRadicle(new RadicleConfig(null, null, null, "test", false));
        var session = new Session();
        session.publicKey = PUBLIC_KEY;
        session.id = SESSION_ID;

        var signature = authService.sign(session, new FileInputStream("src/test/resources/radicle/keys/radicle"));
        assertThat(signature).isEqualTo(SIGNATURE);
    }
}