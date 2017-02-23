package se.olapetersson.lagom.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import org.springframework.util.Assert;

import java.util.UUID;

import static play.Logger.debug;
import static play.Logger.info;

public class ConsulService {

    private String serviceName;
    private String hostname;
    private int port;
    private String serviceId;
    private NewService.Check serviceCheck;

    public ConsulService(String serviceName, String hostname, int port) {
        this.serviceName = serviceName;
        this.hostname = hostname;
        this.port = port;
        this.serviceId = UUID.randomUUID().toString();
    }

    public ConsulService(String serviceName, String hostname, int port, NewService.Check serviceCheck) {
        this(serviceName, hostname, port);
        this.serviceCheck = serviceCheck;
    }

    public ConsulService(String serviceName, String hostname, int port, String healthEndpoint) {
        this(serviceName, hostname, port);
        Assert.hasText(healthEndpoint, "healthEndpoint must be set");
        String healthCheckUrl = String.format("http://%s:%s/%s", hostname, port, healthEndpoint);
        debug("The healthcheck url {}", healthCheckUrl);

        NewService.Check serviceCheck = new NewService.Check();
        serviceCheck.setHttp(healthCheckUrl);
        serviceCheck.setInterval("10s");
        serviceCheck.setTimeout("1s");
        this.serviceCheck = serviceCheck;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void registerService(String consulHostname, int consulPort) {
        Assert.hasText(serviceName, "serviceName must be set");
        Assert.hasText(hostname, "hostname must be set");
        Assert.state(port > 0, "port must be more than 0");

        NewService service = new NewService();
        service.setId(serviceId);
        service.setName(serviceName);
        service.setPort(port);
        service.setAddress(hostname);
        if (serviceCheck != null) {
            service.setCheck(serviceCheck);
        }

        ConsulClient client = new ConsulClient(consulHostname, consulPort);
        client.agentServiceRegister(service);
        info("Registered service {} with {}:{} at {}:{}", serviceName, hostname, port, consulHostname, consulPort);
    }

}
