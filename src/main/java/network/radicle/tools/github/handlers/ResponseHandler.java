package network.radicle.tools.github.handlers;

import com.google.common.base.Strings;
import jakarta.ws.rs.core.Response;

public class ResponseHandler {
    public static String handleResponse(Response resp) throws Exception {
        var status = resp.getStatus();
        var json = Strings.nullToEmpty(resp.readEntity(String.class));

        if (Response.Status.Family.familyOf(status) != Response.Status.Family.SUCCESSFUL) {
            throw ExceptionsHandler.createException(status, json);
        }
        return json;
    }
}
