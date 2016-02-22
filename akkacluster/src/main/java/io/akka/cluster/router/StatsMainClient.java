package io.akka.cluster.router;

import java.util.concurrent.TimeUnit;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import io.akka.cluster.SimpleClusterListener;

import static io.akka.cluster.router.StatsMessages.StatsResult;

public class StatsMainClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String port = args.length>0? args[0]:"0";
		System.out.println("Port:"+port);
		final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+port).
				withFallback(ConfigFactory.parseString("akka.cluster.roles = [compute]")).
				withFallback(ConfigFactory.load("stats1"));
		ActorSystem system = ActorSystem.create("ClusterSystem",config);
		system.actorOf(Props.create(SimpleClusterListener.class),
		          "clusterListener");
		ActorRef worker = system.actorOf(Props.create(StatsWorker.class), "statsWorker");
		System.out.println((worker.path()));
		
		ActorRef statsService = system.actorOf(Props.create(StatsService.class), "statsService");
		System.out.println((statsService.path()));
		
		Future<Object> future = Patterns.ask(statsService, new StatsMessages.StatsJob("Hello! This is my world."), new Timeout(new FiniteDuration(5, TimeUnit.SECONDS)));
		StatsResult result = null;
		try {
			result = (StatsResult)Await.result(future, Duration.create(5, TimeUnit.SECONDS));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double meanWordLength = result.getMeanWordLength();
		System.out.println("meanWordLength::"+meanWordLength);
		

	}

}
