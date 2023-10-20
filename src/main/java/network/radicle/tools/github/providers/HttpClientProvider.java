package network.radicle.tools.github.providers;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import jakarta.ws.rs.client.Client;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import java.util.concurrent.TimeUnit;

public class HttpClientProvider {
    @Produces
    @Singleton
    public Client provide() {
        ResteasyClientBuilder builder = (ResteasyClientBuilder) ResteasyClientBuilder.newBuilder();
        return builder.connectionPoolSize(10)
                .setFollowRedirects(true)
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .build();
    }
}
