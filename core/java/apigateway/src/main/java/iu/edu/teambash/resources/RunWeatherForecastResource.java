package iu.edu.teambash.resources;

import iu.edu.teambash.GetZooNode;
import iu.edu.teambash.JobEntity;
import org.apache.curator.x.discovery.ServiceProvider;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Created by janakbhalla on 24/09/16.
 */
@Path("/runForecast/{uid}")
public class RunWeatherForecastResource extends AbstractResource {

    @Context
    private ResourceContext rc;

    private static int index = 0;
    private String availableIPAddress = null;

    public RunWeatherForecastResource() {
        GetZooNode node = new GetZooNode();
        availableIPAddress = node.getNodeAddress();
    }

    @GET
    public Response redirect(@PathParam("uid") int uid) {
        ServiceProvider<Void> serviceProvider = delegate(availableIPAddress, "runWeatherForecast");
        String runWeatherForecast = getAddress(serviceProvider, index);
        index++;
        System.out.println(runWeatherForecast);
        String jobName = "team_bash_" + System.currentTimeMillis();
        Response response = invokeRemoteService(5, uid, runWeatherForecast, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, HttpMethod.POST, Entity.entity(jobName, MediaType.TEXT_PLAIN));
        JobsRegistryResource jobsRegistryResource = rc.getResource(JobsRegistryResource.class);
        jobsRegistryResource.createJob(new JobEntity(uid, jobName));
        return response;
    }


}
