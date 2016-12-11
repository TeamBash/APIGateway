package iu.edu.teambash.resources;

import com.codahale.metrics.annotation.Timed;
import iu.edu.teambash.GetZooNode;
import org.apache.curator.x.discovery.ServiceProvider;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by janakbhalla on 22/09/16.
 */
@Path("/forecastTrigger/{uid}")
public class ForecastTriggerResource extends AbstractResource {

    private static int index = 0;
    private String availableIPAddress = null;
    @Context
    private ResourceContext rc;

    public ForecastTriggerResource() {
        GetZooNode node = new GetZooNode();
        availableIPAddress = node.getNodeAddress();
    }


    @POST
    @Timed
    public Response redirect(String cluster, @PathParam("uid") int uid) {
        ServiceProvider<Void> serviceProvider = delegate(availableIPAddress, "forecastTrigger");
        String forecastTriggerAddress = getAddress(serviceProvider, index);
        index++;
        System.out.println(forecastTriggerAddress);
        Response response = invokeRemoteService(4, uid, forecastTriggerAddress, MediaType.TEXT_HTML, MediaType.TEXT_HTML, HttpMethod.POST, Entity.entity(cluster, MediaType.APPLICATION_XML));
        Boolean forecastTrigger = Boolean.valueOf(response.readEntity(String.class));
        if (!forecastTrigger)
            return Response.ok(forecastTrigger).build();
        RunWeatherForecastResource runWeatherForecastResource = rc.getResource(RunWeatherForecastResource.class);
        return runWeatherForecastResource.redirect(uid);
    }
}
