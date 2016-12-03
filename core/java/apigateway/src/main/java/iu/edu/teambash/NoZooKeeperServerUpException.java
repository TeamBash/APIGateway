package iu.edu.teambash;

/**
 * Created by Raghuveer Raavi on 11/26/2016.
 */
public class NoZooKeeperServerUpException extends Exception {

    public NoZooKeeperServerUpException(){
        this.toString();
    }

    @Override
    public String toString(){
        return "No ZooKeeper Servers are available at the moment. Try again";
    }
}
