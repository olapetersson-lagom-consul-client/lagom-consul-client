package se.olapetersson.lagom.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceLocator;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

import javax.inject.Singleton;

public class ConsulServiceLocatorModule extends Module {

    private ConsulConfiguration consulConfiguration;

    @Inject
    public ConsulServiceLocatorModule(play.Environment environment, play.Configuration configuration) {
        this.consulConfiguration = new ConsulConfiguration(configuration);
    }

    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        return seq(bind(ServiceLocator.class).to(ConsulServiceLocator.class).in(Singleton.class),
                bind(ConsulClient.class).toInstance(new ConsulClient(consulConfiguration.getAgentHostname(),
                        consulConfiguration.getAgentPort()))
        );
    }

}
