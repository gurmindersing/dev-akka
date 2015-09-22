/**
 * 
 */
package io.akka.docs.example;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * @author Gurminder
 *
 */
public class MyUntypedActor extends UntypedActor{
	
	LoggingAdapter log = Logging.getLogger(getContext().system(),this);
	
	public void onReceive(Object message) throws Exception{
		
		if(message instanceof String){
			log.info("Recieved String message: "+message);
			getSender().tell(message, getSelf());
		}else{
			unhandled(message);
		}
		
		
		
	}
	
	public static void main(String args[]){
		ActorSystem system = ActorSystem.create("akka");
		ActorRef untypedActor = system.actorOf(Props.create(MyUntypedActor.class));
		Inbox inbox = Inbox.create(system);
		inbox.send(untypedActor, new StringBuilder("Hello World!"));
		String message = (String)inbox.receive(Duration.create(5,TimeUnit.SECONDS));
		System.out.println(message);
	}

	

}
