/**
 * 
 */
package io.akka.sample;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Procedure;

/**
 * @author Gurminder
 *
 */
public class HotSwapActor extends UntypedActor{
	
	Procedure<Object> angry = new Procedure<Object>(){
		public void apply(Object message){
			if(message.equals("bar")){
				System.out.println("in angry method");
				getSender().tell("I am already angry",getSelf());
			}else if(message.equals("foo")){
				getSender().tell("becoming happy",getSelf());
				getContext().become(happy);
			}
		}
	};
	
	Procedure<Object> happy = new Procedure<Object>(){
		public void apply(Object message){
			if(message.equals("bar")){
				getSender().tell("I am happy now",getSelf());
			}else if(message.equals("foo")){
				getSender().tell("becoming angry",getSelf());
				getContext().become(angry);
			}
		}
	};
	
	public void preStart(){
		System.out.println("starting pre");
	}
	
	
	/**
	 * @param args
	 * @throws TimeoutException 
	 */
	public static void main(String[] args) throws TimeoutException {
		// TODO Auto-generated method stub
		System.out.println("hello");
		ActorSystem system = ActorSystem.create("user");
		ActorRef actorRef = system.actorOf(Props.create(HotSwapActor.class), "hotSwap");
		Inbox inbox = Inbox.create(system);
		inbox.send(actorRef, "bar");
//		System.out.println(inbox.receive(Duration.create(1, TimeUnit.SECONDS)));
//		inbox = Inbox.create(system);
		inbox.send(actorRef, "foo");
		System.out.println(inbox.receive(Duration.create(1, TimeUnit.SECONDS)));
//		actorRef.tell("bar", ());
		inbox.send(actorRef, "foo");
		System.out.println(inbox.receive(Duration.create(1, TimeUnit.SECONDS)));

	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message.equals("bar")){
			System.out.println("in on recieve method");
			getContext().become(angry);
		}else if(message.equals("foo")){
			getContext().become(happy);
		}else{
			unhandled(message);
		}
	}

	

}
