package network.radicle.tools.migrate.handlers;

import io.quarkus.security.UnauthorizedException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.core.Response;

public class ExceptionsHandler {
    public static Exception createException(int statusCode, String message) {
        var family = Response.Status.Family.familyOf(statusCode);
        if (statusCode == Response.Status.UNAUTHORIZED.getStatusCode()) {
            return new UnauthorizedException(message);
        } else if (statusCode == Response.Status.FORBIDDEN.getStatusCode()) {
            return new ForbiddenException(message);
        } else if (statusCode == Response.Status.SERVICE_UNAVAILABLE.getStatusCode()) {
            return new ServiceUnavailableException(message);
        } else if (statusCode == Response.Status.NOT_FOUND.getStatusCode()) {
            return new NotFoundException(message);
        } else if (family == Response.Status.Family.SERVER_ERROR) {
            return new InternalServerErrorException(message);
        } else {
            return new BadRequestException(message);
        }
    }
}
