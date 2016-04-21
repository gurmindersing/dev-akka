/**
 * 
 */
package io.akka.sample;

import static akka.pattern.Patterns.gracefulStop;
import java.util.concurrent.TimeUnit;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.japi.Procedure;

/**
 * @author Gurminder
 *
 */
public class Manager extends UntypedActor {
	  
	  public static final String SHUTDOWN = "shutdown";
	  
	  ActorRef worker = getContext().watch(getContext().actorOf(
	      Props.create(AkkaActor.class), "worker"));
	  
	  public void onReceive(Object message) {
	    if (message.equals("job")) {
	    	System.out.println("getSelf:"+getSelf());
	      worker.tell("crunch", getSelf());
//	      getContext().become(shuttingDown);
	    } else if (message.equals(SHUTDOWN)) {
	      worker.tell(PoisonPill.getInstance(), getSelf());
	      System.out.println("getContext().sender():"+getContext().sender());
	      getContext().become(shuttingDown);
	    }else if(message instanceof ActorSystem){
	    	System.out.println(" shut down system");
	    	((ActorSystem)message).shutdown();
	    }else{
	    	System.out.println("tick:");
	    }
	  }
	  
	  Procedure<Object> shuttingDown = new Procedure<Object>() {
	    @Override
	    public void apply(Object message) {
	    	System.out.println("shuttingdown() method message: "+message);
	      if (message.equals("job")) {
	    	  System.out.println("getSender: "+getSender().path().toSerializationFormat());
	        getSender().tell("service unavailable, shutting down", getSelf());
	      } else if (message instanceof Terminated) {
	        getContext().stop(getSelf());
	      }
	    }
	  };
	  
	  public void preStart(){
		  System.out.println("starting manager");
	  }
	  
	  
	  public static void main(String args[]){
			ActorSystem system = ActorSystem.create("user");
			ActorRef actorA = system.actorOf(Props.create(Manager.class), "actorA");
			
			Future<Boolean> stopped = gracefulStop(actorA,Duration.create(5,TimeUnit.SECONDS),Manager.SHUTDOWN);
			try {
				
				System.out.println(Await.result(stopped, Duration.create(6, TimeUnit.SECONDS)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("is terminated : "+actorA.isTerminated());
			stopped = gracefulStop(actorA,Duration.create(5,TimeUnit.SECONDS),Manager.SHUTDOWN);
			try {
				
				System.out.println(Await.result(stopped, Duration.create(6, TimeUnit.SECONDS)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			/*
			Inbox inbox = Inbox.create(system);
			inbox.send(actorA, "job");
			System.out.println(inbox.receive(Duration.create(2000, TimeUnit.MILLISECONDS)));
			System.out.println(ask(actorA,"job",2000).value().toString());
			System.out.println(ask(actorA,"shutdown",2000));
			System.out.println(ask(actorA,"job",2000));*/
		}
	}
