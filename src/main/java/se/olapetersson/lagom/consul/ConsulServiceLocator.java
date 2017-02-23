package se.olapetersson.lagom.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.catalog.model.CatalogService;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.ServiceLocator;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.util.Assert;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.Logger.debug;
import static play.Logger.info;

public class ConsulServiceLocator implements ServiceLocator {

    private ConsulClient consulClient;
    private static String serviceProtocol;

    @Inject
    public ConsulServiceLocator(ConsulClient consulClient, ConsulConfiguration configuration) {
        info("started ConsulServiceLocator");
        this.consulClient = consulClient;
        this.serviceProtocol = configuration.getServiceProtocol();
        Assert.hasText(serviceProtocol, format("You need to configure {} with a service protocol (http/https)", serviceProtocol));
    }

    @Override
    public CompletionStage<Optional<URI>> locate(String serviceName) {
        debug(format("Locating %s", serviceName));
        return supplyAsync(() -> Optional.ofNullable(getRandomServiceUri(serviceName)));
    }

    @Override
    public CompletionStage<Optional<URI>> locate(String s, Descriptor.Call<?, ?> call) {
        new NotImplementedException("locate isn't implemented");
        return null;
    }

    @Override
    public <T> CompletionStage<Optional<T>> doWithService(String serviceName, Descriptor.Call<?, ?> call, Function<URI, CompletionStage<T>> function) {
        info("doingWithService {}", serviceName);
        URI randomServiceUri = getRandomServiceUri(serviceName);
        info("Got a serviceUri {} ", randomServiceUri.getHost());
        CompletionStage<Optional<T>> optionalCompletionStage = function.apply(randomServiceUri).thenApply(x -> Optional.of(x));
        return optionalCompletionStage;
    }

    private URI getRandomServiceUri(String serviceName) {
        List<CatalogService> services = consulClient.getCatalogService(serviceName, QueryParams.DEFAULT).getValue();
        Assert.notEmpty(services, format("No services registered for %s", serviceName));
        CatalogService service = services.get(ThreadLocalRandom.current().nextInt(0, services.size()));
        String serviceAddress = service.getServiceAddress();
        int servicePort = service.getServicePort();
        try {
            return new URI(format("%s://%s:%s", serviceProtocol, serviceAddress, servicePort));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(format("Could not create URI from %s:%s", serviceAddress, servicePort));
        }
    }


}
