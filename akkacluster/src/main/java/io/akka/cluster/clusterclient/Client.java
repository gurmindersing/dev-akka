/**
 * 
 */
package io.akka.cluster.clusterclient;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorPath;
import akka.actor.ActorPaths;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientSettings;

/**
 * @author gurmi
 *
 */
public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+2551).withFallback(ConfigFactory.load("clusterclient"));
		ActorSystem system = ActorSystem.create("client", config);
		
		final ActorRef c = system.actorOf(ClusterClient.props(ClusterClientSettings.create(system).withInitialContacts(initialContacts())), "client");
		System.out.println("c path : "+c.path().toString());
		c.tell(new ClusterClient.Send("/user/serviceA", "hello",true), ActorRef.noSender());
		c.tell(new ClusterClient.SendToAll("/user/serviceB", "hi"), ActorRef.noSender());
		
	}
	
	static Set<ActorPath> initialContacts(){
		return new HashSet<ActorPath>(Arrays.asList(ActorPaths.fromString("akka.tcp://cluster@127.0.0.1:2551/service/receptionist"),
				ActorPaths.fromString("akka.tcp://cluster@127.0.0.1:2552/service/receptionist")));
	}

}
