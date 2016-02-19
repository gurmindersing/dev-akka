package io.akka.cluster.router;

import java.util.concurrent.TimeUnit;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import static io.akka.cluster.router.StatsMessages.StatsResult;

public class StatsMainClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ActorSystem system = ActorSystem.create("system");
		ActorRef statsService = system.actorOf(Props.create(StatsService.class), "statsService");
		
		Future<Object> future = Patterns.ask(statsService, "Hello! This is my world.", new Timeout(new FiniteDuration(5, TimeUnit.SECONDS)));
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
