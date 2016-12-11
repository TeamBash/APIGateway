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

@Path("/dataIngestor/{uid}/{year}/{month}/{date}/{station}")
public class DataIngestorResource extends AbstractResource {

    private static int index = 0;
    private String availableIPAddress = null;
    @Context
    private ResourceContext rc;

    public DataIngestorResource() {
        GetZooNode node = new GetZooNode();
        availableIPAddress = node.getNodeAddress();
    }

    @GET
    @Timed
    public Response redirect(@PathParam("uid") int uid, @PathParam("year") String year, @PathParam("month") String month, @PathParam("date") String date, @PathParam("station") String station) {

        ServiceProvider<Void> serviceProvider = delegate(availableIPAddress, "dataIngestor");
        String dataIngestorAddress = getAddress(serviceProvider, index);
        index++;
        Response response = invokeRemoteService(1, uid, dataIngestorAddress + year + "/" + month + "/" + date + "/" + station, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, HttpMethod.GET, null);

        String url = response.readEntity(String.class);
        StormDetectionResource stormDetectionResource = rc.getResource(StormDetectionResource.class);
        return stormDetectionResource.redirect(url, uid);
    }
}
