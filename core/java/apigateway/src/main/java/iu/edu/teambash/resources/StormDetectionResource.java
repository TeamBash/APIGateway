package iu.edu.teambash.resources;

import com.codahale.metrics.annotation.Timed;
import iu.edu.teambash.GetZooNode;
import org.apache.curator.x.discovery.ServiceProvider;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by janakbhalla on 17/09/16.
 */

@Path("/stormDetection/{url : .+}/{uid}")
public class StormDetectionResource extends AbstractResource {

    private static int index = 0;
    private String availableIPAddress = null;
    @Context
    private ResourceContext rc;

    public StormDetectionResource() {
        GetZooNode node = new GetZooNode();
        availableIPAddress = node.getNodeAddress();
    }

    @GET
    @Timed
    public Response redirect(@PathParam("url") String url, @PathParam("uid") int uid) {
        ServiceProvider<Void> serviceProvider = delegate(availableIPAddress, "stormDetection");
        String stormDetection = getAddress(serviceProvider, index);
        index++;
        Response response = invokeRemoteService(2, uid, stormDetection + url, MediaType.APPLICATION_XML, MediaType.APPLICATION_XML, HttpMethod.GET, null);
        String kml = response.readEntity(String.class);
        StormClusteringResource resourceB = rc.getResource(StormClusteringResource.class);
        return resourceB.redirect(kml, uid);
    }
}
