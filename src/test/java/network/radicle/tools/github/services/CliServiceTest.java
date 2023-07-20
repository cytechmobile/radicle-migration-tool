package network.radicle.tools.github.services;

import network.radicle.tools.github.core.radicle.Session;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class CliServiceTest {

    private final static String SESSION_ID = "GdPg6opLHgS9VBTdaV1ACESEc9wG2W68";
    private final static String PUBLIC_KEY = "z6Mkf2deXEQBXenu829pKGcvSutjko6g7L6J4bapiBaJ1NYk";
    private final static String SIGNATURE = "z5tCYabHbMxz3mSwBbhdHSxP7XZnZHQjAciHTYy6gGHTarUksye9grJR3rs9grYnUtZ7DE7woGQDLxUarvDioY7BM";

    @Disabled
    @Test
    public void testCli() {
        var service = new CliService();
        service.radHome = Optional.of("/home/tdakanalis/.radicle");
        service.passphrase = Optional.of("test");
        var session = new Session();
        session.publicKey = PUBLIC_KEY;
        session.id = SESSION_ID;

        service.sign(session);

        assertThat(session.signature).isEqualTo(SIGNATURE);
    }

}
