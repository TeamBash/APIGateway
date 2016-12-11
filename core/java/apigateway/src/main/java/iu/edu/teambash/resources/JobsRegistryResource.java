package iu.edu.teambash.resources;

import iu.edu.teambash.JobEntity;
import iu.edu.teambash.StringConstants;

import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by janakbhalla on 11/12/16.
 */


@Path("/jobs/")
public class JobsRegistryResource extends AbstractResource{

    @POST
    @Path("/createJob")
    public Response createJob(JobEntity jobEntity) {
        return invokeService(StringConstants.REGISTRY + "jobs/createJob", MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON, HttpMethod.POST, Entity.entity(jobEntity, MediaType.APPLICATION_JSON));
    }

    @GET
    @Path("/getDetails/{uid}")
    public Response getJobNames(@PathParam("uid") int uid) {
        return invokeService(StringConstants.REGISTRY + "jobs/getDetails/" + uid, MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON, HttpMethod.GET, null);
    }
}
