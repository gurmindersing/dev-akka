/**
 * 
 */
package io.akka.cluster.clusterclient;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.client.ClusterClientReceptionist;

/**
 * @author gurmi
 *
 */
public class Main {
	public static void main(String[] args) {
		
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+0).withFallback(ConfigFactory.load("clusterclientworker"));
		ActorSystem system = ActorSystem.create("cluster",config);
		
		ActorRef serviceA = system.actorOf(Props.create(Service.class), "serviceA");
		
		System.out.println(serviceA.path().toString());
		ClusterClientReceptionist.get(system).registerService(serviceA);
		
		ActorRef serviceB = system.actorOf(Props.create(Service.class),"serviceB");
		ClusterClientReceptionist.get(system).registerService(serviceB);
	}

}
