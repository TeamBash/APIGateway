package iu.edu.teambash.resources;

import iu.edu.teambash.GetZooNode;
import org.apache.curator.x.discovery.ServiceProvider;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by janakbhalla on 11/12/16.
 */
@Path("/getJobDetails/{uid}")
public class JobsResource extends AbstractResource {

    private static int index = 0;
    private String availableIPAddress = null;
    @Context
    private ResourceContext rc;

    public JobsResource() {
        GetZooNode node = new GetZooNode();
        availableIPAddress = node.getNodeAddress();
    }


    @GET
    public Response getJobDetails(@PathParam("uid") int uid){
        JobsRegistryResource jobsRegistryResource = rc.getResource(JobsRegistryResource.class);
        List<String> jobNames = jobsRegistryResource.getJobNames(uid).readEntity(new GenericType<List<String>>(){});

        ServiceProvider<Void> serviceProvider = delegate(availableIPAddress, "getJobDetails");
        String getJobDetailsAddr = getAddress(serviceProvider, index);
        index++;

        return invokeService(getJobDetailsAddr, MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON, HttpMethod.POST, Entity.entity(jobNames, MediaType.APPLICATION_JSON));

    }

}
