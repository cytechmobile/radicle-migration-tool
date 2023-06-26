package network.radicle.tools.github.handlers;

import com.google.common.base.Strings;
import jakarta.ws.rs.core.Response;
import network.radicle.tools.github.clients.RadicleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(RadicleClient.class);

    public static String handleResponse(Response resp) throws Exception {
        var status = resp.getStatus();
        var json = Strings.nullToEmpty(resp.readEntity(String.class));

        if (Response.Status.Family.familyOf(status) != Response.Status.Family.SUCCESSFUL) {
            logger.debug("Got response code: {}", resp.getStatus());
            throw ExceptionsHandler.createException(status, json);
        }
        return json;
    }
}
