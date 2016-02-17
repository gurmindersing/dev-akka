package io.akka.cluster.sample;

import io.akka.cluster.SimpleClusterListener;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TransformationFrontendMain {
	
	public static void main(String args[]){
		final String port = args.length>0?args[0]:"0";
		final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+port).
				withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]")).
				withFallback(ConfigFactory.load());
		
		ActorSystem system = ActorSystem.create("ClusterSystem", config);
		ActorRef listener =system.actorOf(Props.create(SimpleClusterListener.class),"frontsimpleclusterlistener");
		System.out.println("listener front end:"+listener.path().toStringWithoutAddress());
		final ActorRef frontend = system.actorOf(Props.create(TransformationFrontend.class), "frontend");
		final FiniteDuration interval = Duration.create(2, TimeUnit.SECONDS);
		final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
		final AtomicInteger counter = new AtomicInteger();
		final ExecutionContext ec = system.dispatcher();
		system.scheduler().schedule(interval, interval, new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				Patterns.ask(frontend, new TransformationMessages.TransformationJob("hello-"+counter.incrementAndGet()), timeout).
					onSuccess(new OnSuccess<Object>(){

						@Override
						public void onSuccess(Object result) throws Throwable {
							// TODO Auto-generated method stub
							System.out.println(result);
							
						}
						
					}, ec);
			}
			
		}, ec);
		
	}

}
