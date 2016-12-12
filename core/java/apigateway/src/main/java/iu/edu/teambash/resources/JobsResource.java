package iu.edu.teambash.resources;

import iu.edu.teambash.GetZooNode;
import org.apache.curator.x.discovery.ServiceProvider;

import javax.ws.rs.*;
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

@Path("/jobsapi")
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
    @Path("/getJobDetails/{uid}")
    public Response getJobDetails(@PathParam("uid") int uid){
        JobsRegistryResource jobsRegistryResource = rc.getResource(JobsRegistryResource.class);
        List<String> jobNames = jobsRegistryResource.getJobNames(uid).readEntity(new GenericType<List<String>>(){});

        ServiceProvider<Void> serviceProvider = delegate(availableIPAddress, "getJobDetails");
        String getJobDetailsAddr = getAddress(serviceProvider, index);
        index++;

        return invokeService(getJobDetailsAddr, MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON, HttpMethod.POST, Entity.entity(jobNames, MediaType.APPLICATION_JSON));

    }

    @GET
    @Path("/restartJob/{uid}/{jobName}")
    public Response restartJob(@PathParam("jobName") String jobName, @PathParam("uid") int uid){
        ServiceProvider<Void> serviceProvider = delegate(availableIPAddress, "runWeatherForecast");
        String runWeatherForecast = getAddress(serviceProvider, index);
        index++;
        System.out.println(runWeatherForecast);
        return invokeRemoteService(5, uid, runWeatherForecast, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, HttpMethod.POST, Entity.entity(jobName, MediaType.TEXT_PLAIN));
    }

}
