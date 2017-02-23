# A Service Locator for Consul written in Java

**This work is based on the [scala version](https://raw.githubusercontent.com/jboner/lagom-service-locator-consul) written by Jonas Bonér**

## Usage

### Prereq:
You need to have a [consul instance](https://www.consul.io/) running which your services can register to.  

Your application needs to enable the module in `src/main/resources/application.conf` and add 
configuration for your consul agents hostname, port and the protocol of your service:

As the lib is currently not on maven central you need to clone it and perform a `mvn clean install`

### Configuration

Add the mvn dependency in the pom of your service

```
<dependency>
  <groupId>se.olapetersson.lagom</groupId>
  <artifactId>consul-discovery-example</artifactId>
  <version>VERSION</version>
</dependency>
```

```
play.modules.enabled += se.olapetersson.lagom.consul.ConsulServiceLocatorModule

lagom {
  discovery {
    consul {
      agent-hostname = "dev-consul"   # hostname or IP-address for the Consul agent
      agent-port     = 8500          # port for the Consul agent
      uri-scheme     = "http"        # for example: http or https
    }
  }
}
```

### Register a service

To register your service you need to create an instance of ConsulService and register it.

```
 @Inject
    public ExampleModule(Environment environment, Configuration configuration) throws UnknownHostException {
        this.environment = environment;
        this.configuration = configuration;
        this.consulConfiguration = new ConsulConfiguration(configuration);
        registerService();
    }

    private void registerService() throws UnknownHostException {
        int servicePort = Integer.parseInt(configuration.getString("http.port"));
        String hostname = InetAddress.getLocalHost().getHostAddress();
        
        //Create a ConsulService(serviceName, serviceHostName, servicePort) and register it
        ConsulService consulService = new ConsulService("call", hostname, servicePort);
        consulService.registerService(consulConfiguration.getAgentHostname(), consulConfiguration.getAgentPort());
    }
```    

#### ConsulService and health-checks

If your service exposes a healtcheck-endpoint this can be registered along with the service via the signature.

```
  public ConsulService(String serviceName, String hostname, int port, String healthEndpoint)
```

# Sample 
TBD