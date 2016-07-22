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
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientSettings;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * @author gurmi
 *
 */
public class ClusterClientMain {
	
//	LoggingAdapter log = Logging.getLogger(getContext().system(),this);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+2552).withFallback(ConfigFactory.load("clusterclient"));
		ActorSystem system = ActorSystem.create("client", config);
		
		ActorRef c = system.actorOf(ClusterClient.props(ClusterClientSettings.create(system)),"client");
		c.tell(new ClusterClient.Send("/user/serviceA","hello",true), ActorRef.noSender());
		c.tell(new ClusterClient.SendToAll("/user/serviceB","hello"), ActorRef.noSender());
		
	}
	
	/*static Set<ActorPath> initialContacts(){
		return new HashSet<ActorPath>(Arrays.asList(ActorPaths.fromString("akka.tcp://cluster@127.0.0.1:2551/service/receptionist"),
				ActorPaths.fromString("akka.tcp://cluster@127.0.0.1:2552/service/receptionist")));
	}*/

}
