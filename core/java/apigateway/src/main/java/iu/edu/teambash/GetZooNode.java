package iu.edu.teambash;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Raghuveer Raavi on 11/26/2016.
 */
public class GetZooNode {
    private static String availableIPAddress = null;
    public GetZooNode(){
        List<String> ipAddresses = new LinkedList<>();
        ipAddresses.add("ec2-52-15-48-215.us-east-2.compute.amazonaws.com");
        ipAddresses.add("ec2-52-15-170-115.us-east-2.compute.amazonaws.com");
        ipAddresses.add("ec2-52-52-172-203.us-west-1.compute.amazonaws.com");
        //URI uri = null;
        //String response = null;
        int count = 0;
        for(String ip : ipAddresses){
            count++;
            try{
                if(InetAddress.getByName(ip).isReachable(3000)) {
                    availableIPAddress = ip;
                    break;
                }
            }
            catch (UnknownHostException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(count == ipAddresses.size() && availableIPAddress == null){
            try{
                throw new NoZooKeeperServerUpException();
            } catch (NoZooKeeperServerUpException e) {
                e.printStackTrace();
            }
        }
    }

    public String getNodeAddress(){
        return availableIPAddress;
    }
}
