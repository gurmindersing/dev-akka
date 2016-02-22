/**
 * 
 */
package io.akka.cluster.router;

import java.util.concurrent.TimeUnit;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import io.akka.cluster.router.StatsMessages.StatsResult;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * @author gurmi
 *
 */
public class StatsSimpleOneMasterClientMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ActorSystem system = ActorSystem.create("ClusterSystem", ConfigFactory.load("stats2"));
//		system.actorOf(Props.create(), name);
//		system.actorOf(Props.create(StatsMainClient.class), "/user/statsServiceProxy");
		
		Future<Object> future = Patterns.ask(new ActorSelection("")new StatsMessages.StatsJob("Hello! This is my world."), new Timeout(new FiniteDuration(5, TimeUnit.SECONDS)));
		StatsResult result = null;
	}
	
	public static void start(){
		FiniteDuration interval = Duration.create(2, TimeUnit.SECONDS);
		tcikTask
	}

}
