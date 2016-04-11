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
public class CreationApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		startRemoteWorkerSystem();
		startRemoteCreationSystem();

	}
	
	public static void startRemoteWorkerSystem(){
		ActorSystem.create("CalculatorWorkerSystem", ConfigFactory.load("calculator"));
		System.out.println("started CalculatorWorkerSystem");
	}
	
	public static void startRemoteCreationSystem(){
		final ActorSystem system = ActorSystem.create("CreationSystem", ConfigFactory.load("remotecreation"));
		final ActorRef actor = system.actorOf(Props.create(CreationActor.class), "creationActor");
		
		System.out.println("Started creation system");
		
		final Random r = new Random();
		system.scheduler().schedule(Duration.create(1, TimeUnit.SECONDS), Duration.create(1, TimeUnit.SECONDS), new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				if(r.nextInt(100)%2==0){
					actor.tell(new Op.Multiply(r.nextInt(100), r.nextInt(100)), null);
					
				}else{
					actor.tell(new Op.Divide(r.nextInt(100), r.nextInt(100)), null);
				}
				
			}
			
		}, system.dispatcher());
		
		
		
	}

}
