package iu.edu.teambash.resources;

import com.codahale.metrics.annotation.Timed;
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
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by janakbhalla on 17/09/16.
 */

@Path("/dataIngestor/{uid}/{year}/{month}/{date}/{station}")
public class DataIngestorResource extends AbstractResource {

    private static int index = 0;
    private String availableIPAddress = null;

    private static int getIndex(){
        return index;
    }

    private static void setIndex(int index){
        DataIngestorResource.index = index;
    }

    @Context
    private ResourceContext rc;

    public DataIngestorResource() {
        GetZooNode node = new GetZooNode();
        availableIPAddress = node.getNodeAddress();
    }

    private String delegate(){
        System.out.println("System starting to delegate: ");

        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(availableIPAddress+":2181", new RetryNTimes(5,1000));
        curatorFramework.start();

        ServiceDiscovery<Void> serviceDiscovery = ServiceDiscoveryBuilder.builder(Void.class).basePath("services").client(curatorFramework).build();

        try {
            serviceDiscovery.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        ServiceProvider<Void> serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName("dataIngestor").build();

        try {
            serviceProvider.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try{
            List<ServiceInstance<Void>> instances = (List<ServiceInstance<Void>>) serviceProvider.getAllInstances();
            if(instances.size() == 0) {
                System.out.println("No instances found for this service");
                return "No instances found for this service";
            }
//            System.out.println(instances);
            int currentIndex = DataIngestorResource.getIndex();
            DataIngestorResource.setIndex(currentIndex+1);

            String address = instances.get(currentIndex%instances.size()).getUriSpec().getParts().get(0).getValue();
//            System.out.println(address);
            return address;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "Done!";
    }

    @GET
    @Timed
    public Response redirect(@PathParam("uid") int uid, @PathParam("year") String year, @PathParam("month") String month, @PathParam("date") String date, @PathParam("station") String station) {

        String dataIngestorAddress = this.delegate();
        System.out.println(dataIngestorAddress);
        Response response = invokeRemoteService(1, uid, dataIngestorAddress + year + "/" + month + "/" + date + "/" + station, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, HttpMethod.GET, null);

        String url = response.readEntity(String.class);
        StormDetectionResource stormDetectionResource = rc.getResource(StormDetectionResource.class);
        return stormDetectionResource.redirect(url, uid);
    }
}
