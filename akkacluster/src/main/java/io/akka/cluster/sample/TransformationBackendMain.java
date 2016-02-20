package io.akka.cluster.sample;

import io.akka.cluster.SimpleClusterListener;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;

import java.util.concurrent.TimeUnit;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TransformationBackendMain {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		final String port = args.length > 0? args[0]:"0";
		final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+port).
				withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
				withFallback(ConfigFactory.load());
		
		final ActorSystem system = ActorSystem.create("ClusterSystem",config);
		system.actorOf(Props.create(SimpleClusterListener.class),"backsimpleclusterlistener");
		ActorRef listener =system.actorOf(Props.create(SimpleClusterListener.class),"simpleclusterlistener");
		System.out.println("listener back end:"+listener.path().toStringWithoutAddress());
		system.actorOf(Props.create(TransformationBackend.class), "backend");
		
		Cluster.get(system);
		/*
		Cluster.get(system).registerOnMemberRemoved(new Runnable(){
			public void run() {
				
			final Runnable exit = new Runnable() {
			      public void run() {
			    	  System.out.println("exit clean jvm");
//			        System.exit(0);
			      }
			    };
			system.registerOnTermination(exit);
			system.terminate();
			
			new Thread(){
				public void run(){
					try {
						Await.result(system.whenTerminated(), Duration.create(10, TimeUnit.SECONDS));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
//						System.exit(-1);
						System.out.println("awaited 10 seconds");
						
						
					}
				}
			}.start();
				
			}
			
		});*/
		
		
//		Thread.sleep(5000);
//		System.out.println("stopping cluster");
		Cluster cluster = Cluster.get(system);
		System.out.println("stopping cluster address::"+cluster.selfAddress().toString());
		cluster.leave(cluster.selfAddress());

	}

}
