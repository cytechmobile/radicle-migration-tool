package network.radicle.tools.github.services;

import com.sshtools.agent.client.SshAgentClient;
import com.sshtools.common.ssh.components.SshPublicKey;
import com.sshtools.common.ssh.components.jce.SshEd25519PublicKeyJCE;
import com.sshtools.common.util.Base64;
import jakarta.enterprise.context.ApplicationScoped;
import network.radicle.tools.github.core.radicle.Session;
import network.radicle.tools.github.utils.Multibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@ApplicationScoped
public class SshAgentService {
    private static final Logger logger = LoggerFactory.getLogger(SshAgentService.class);
    public static final byte[] MULTI_CODEC_TYPE = new byte[]{(byte) 0xED, (byte) 0x1};

    public String sign(Session session) {
        SshAgentClient agent;
        try {
            agent = SshAgentClient.connectOpenSSHAgent("radicle-github-migrate");
        } catch (Exception e) {
            logger.debug("Failed to connect to the local ssh agent. Message: {}", e.getMessage());
            return null;
        }

        // the public key is encoded as follows:
        // multi-base(base58-btc, multi-codec(public-key-type, raw-public-key-bytes))
        var multiCodedPKey = Multibase.decode(session.publicKey);

        // the first two bytes are the multi-codec key type for Ed25519 keys [0xED, 0x1]
        // the rest are the bytes if the public key
        var decodedPublicKey = Arrays.copyOfRange(multiCodedPKey, MULTI_CODEC_TYPE.length, multiCodedPKey.length);

        SshPublicKey publicKey;
        try {
            publicKey = new SshEd25519PublicKeyJCE(decodedPublicKey);
            logger.debug("Decoded public key for signing: {}",
                    publicKey.getAlgorithm() + " " + Base64.encodeBytes(publicKey.getEncoded(), true));
        } catch (Exception e) {
            logger.debug("Failed to decode the public key", e);
            return null;
        }

        var dataToSign = session.id + ":" + session.publicKey;

        byte[] signed;
        try {
            signed = agent.sign(publicKey, publicKey.getSigningAlgorithm(),
                    dataToSign.getBytes(StandardCharsets.UTF_8));

            logger.debug("Got signature from agent: {}", signed);

            //the 8 is for the integers that keep the header's and signature's lengths
            var signature = Arrays.copyOfRange(signed, publicKey.getSigningAlgorithm().length() + 8,
                    signed.length);

            return Multibase.encode(Multibase.Base.Base58BTC, signature);
        } catch (Exception e) {
            logger.debug("Failed to sign the session. Error: {}", e.getMessage());
            return null;
        }
    }
}











