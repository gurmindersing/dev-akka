/**
 * 
 */
package io.akka.cluster.remote.calculator;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.duration.Duration;

/**
 * @author gurmi
 *
 */
public class LookupApplication {
	
		
	public static void main(String[] args) {
		startRemoteWorkerSystem();
		startRemoteLookupSystem("akka.tcp://CalculatorWorkerSystem@127.0.0.1:2552");
	}
	
	public static void startRemoteWorkerSystem(){
		ActorSystem.create("CalculatorWorkerSystem", ConfigFactory.load("calculator"));
	}
	
	public static void startRemoteLookupSystem(String path){
		ActorSystem system = ActorSystem.create("RemoteLookupSystem", ConfigFactory.load("remotelookup"));
		
		final ActorRef actor = system.actorOf(Props.create(LookupActor.class,path), "lookupActor");
		System.out.println("Started lookup system");
		
		final Random r = new Random();
		system.scheduler().schedule(Duration.create(1, TimeUnit.SECONDS), Duration.create(1, TimeUnit.SECONDS), new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				System.out.println("in run method");
				if(r.nextInt(100)%2==0){
					System.out.println("in if block");
					actor.tell(new Op.Add(r.nextInt(100), r.nextInt(100)), null);
					
				}else{
					System.out.println("in else block");
					actor.tell(new Op.Subtract(r.nextInt(100), r.nextInt(100)), null);
				}
				
			}
			
		}, system.dispatcher());
		
	}

}
