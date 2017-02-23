package se.olapetersson.lagom.consul;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.springframework.util.Assert;
import play.Configuration;

import static java.lang.String.format;

@Singleton
public class ConsulConfiguration {

    private String agentHostname;
    private int agentPort;
    private String serviceProtocol;

    private Configuration configuration;

    @Inject
    public ConsulConfiguration(Configuration configuration) {
        this.configuration = configuration;

        String hostnameKey = "lagom.discovery.consul.agent-hostname";
        this.agentHostname = configuration.getString(hostnameKey);
        Assert.hasText(agentHostname, format("You need to configure key %s with a consul agent hostname", hostnameKey));

        String portKey = "lagom.discovery.consul.agent-port";
        this.agentPort = Integer.parseInt(configuration.getString(portKey));
        Assert.state(agentPort > 0, format("You need to configure %s with a consul agent port", portKey));

        String serviceProtocolKey = "lagom.discovery.consul.uri-scheme";
        this.serviceProtocol = configuration.getString(serviceProtocolKey);
        Assert.hasText(serviceProtocol, format("You need to configure %s with a service protocol (http/https)", serviceProtocolKey));

    }

    public String getAgentHostname() {
        return agentHostname;
    }

    public int getAgentPort() {
        return agentPort;
    }

    public String getServiceProtocol() {
        return serviceProtocol;
    }
}
