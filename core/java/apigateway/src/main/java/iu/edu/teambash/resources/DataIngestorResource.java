package iu.edu.teambash.resources;

import com.codahale.metrics.annotation.Timed;
import iu.edu.teambash.StringConstants;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by janakbhalla on 17/09/16.
 */


@Path("/datainjestor/{year}/{month}/{date}/{station}")
public class DataIngestorResource {

    public DataIngestorResource() {
    }

    @GET
    @Timed
    public Response redirect(@PathParam("year") long year, @PathParam("month") long month, @PathParam("date") long date, @PathParam("station") String station){
        URI uri = UriBuilder.fromUri(StringConstants.DATA_INGESTOR + year + "/" + month + "/" + date + "/" + station).build();
        return Response.seeOther(uri).build();
    }
}