package iu.edu.teambash.resources;

import iu.edu.teambash.GetZooNode;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by janakbhalla on 24/09/16.
 */
@Path("/runForecast/{uid}")
public class RunWeatherForecastResource extends AbstractResource {

    private static int index = 0;
    private String availableIPAddress = null;

    private static int getIndex() {
        return index;
    }

    private static void setIndex(int index) {
        RunWeatherForecastResource.index = index;
    }

    public RunWeatherForecastResource() {
        GetZooNode node = new GetZooNode();
        availableIPAddress = node.getNodeAddress();
    }

    private String delegate() {

        String address = null;
        System.out.println("System starting to delegate: ");

        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(availableIPAddress + ":2181", new RetryNTimes(5, 1000));
        curatorFramework.start();

        ServiceDiscovery<Void> serviceDiscovery = ServiceDiscoveryBuilder.builder(Void.class).basePath("services").client(curatorFramework).build();

        try {
            serviceDiscovery.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        ServiceProvider<Void> serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName("runWeatherForecast").build();

        try {
            serviceProvider.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            List<ServiceInstance<Void>> instances = (List<ServiceInstance<Void>>) serviceProvider.getAllInstances();
            if (instances.size() == 0) {
                System.out.println("No instances found for this service");
                return "No instances found for this service";
            }
            int currentIndex = RunWeatherForecastResource.getIndex();
            RunWeatherForecastResource.setIndex(currentIndex + 1);

            address = instances.get(currentIndex % instances.size()).getUriSpec().getParts().get(0).getValue();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return address;
    }


    @GET
    public Response redirect(@PathParam("uid") int uid) {

        String runWeatherForecast = this.delegate();
        System.out.println(runWeatherForecast);
        return invokeRemoteService(5, uid, runWeatherForecast, MediaType.APPLICATION_XML, MediaType.APPLICATION_XML, HttpMethod.GET, null);
    }


}
