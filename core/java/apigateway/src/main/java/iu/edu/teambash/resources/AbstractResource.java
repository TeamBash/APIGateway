package iu.edu.teambash.resources;

import iu.edu.teambash.StringConstants;
import iu.edu.teambash.ZooKeeperServerUpException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by janakbhalla on 25/09/16.
 */
public class AbstractResource {

    protected Response invokeRemoteService(int id, int uid, String url, String request, String accept, String method, Entity em) {
        int logId = Integer.valueOf(invokeService(StringConstants.REGISTRY + "startLog/" + uid + "/" + id, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, HttpMethod.POST, null).readEntity(String.class));
        Response response = invokeService(url, request, accept, method, em);
        invokeService(StringConstants.REGISTRY + "endLog/" + logId, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, HttpMethod.POST, null);
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new WebApplicationException(url, response.getStatus());
        }
        return response;
    }

    protected Response invokeService(String url, String request, String accept, String method, Entity em) {
        Client client = new JerseyClientBuilder().build();
        return client.target(url).request(request).accept(accept).method(method, em);
    }

    protected ServiceProvider<Void> delegate(String availableIPAddress, String serviceName) {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(availableIPAddress + ":2181", new RetryNTimes(5, 1000));
        curatorFramework.start();

        ServiceDiscovery<Void> serviceDiscovery = ServiceDiscoveryBuilder.builder(Void.class).basePath("services").client(curatorFramework).build();

        try {
            serviceDiscovery.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServiceProvider<Void> serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName(serviceName).build();

        try {
            serviceProvider.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serviceProvider;
    }

    protected String getAddress(ServiceProvider<Void> serviceProvider, int currentIndex){
        String address = null;
        try {
            List<ServiceInstance<Void>> instances = (List<ServiceInstance<Void>>) serviceProvider.getAllInstances();
            if (instances.size() == 0) {
                throw new ZooKeeperServerUpException(StringConstants.NO_INSTANCES_EXCEPTION);
            }
            address = instances.get(currentIndex % instances.size()).getUriSpec().getParts().get(0).getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }
}
