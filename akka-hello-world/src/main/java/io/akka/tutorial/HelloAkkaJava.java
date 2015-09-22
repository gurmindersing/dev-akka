/**
 * 
 */
package io.akka.tutorial;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * @author Gurminder
 *
 */
public class HelloAkkaJava {

	public static class Greet implements Serializable{}
	public static class WhoToGreet implements Serializable{
		public final String who;
		public WhoToGreet(String who){
			this.who=who;
		}
	}
	
	public static class Greeting implements Serializable{
		public final String message;
		public Greeting(String message){
			this.message=message;
		}
		
	}
	
	//above are messages to be passed to Actors
	
	//now define actors
	
	public static class Greeter extends UntypedActor{

		String greeting="";
		public void onReceive(Object message) throws Exception {
			// TODO Auto-generated method stub
			if(message instanceof WhoToGreet){
				greeting = "hello, " + ((WhoToGreet)message).who;
			}
			
			else if(message instanceof Greet){
				getSender().tell(new Greeting(greeting), getSelf());
			}
			
		}	
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final ActorSystem system = ActorSystem.create("helloakka");
		final ActorRef greeter = system.actorOf(Props.create(Greeter.class), "greeter");
		
		final Inbox inbox = Inbox.create(system);
		
		greeter.tell(new WhoToGreet("akka"), ActorRef.noSender());
		ActorPath greeterPath = greeter.path();
		ActorPath greeterPath2 = null;
		System.out.println(greeterPath.root());
		
		inbox.send(greeter,new Greet());
		
		Greeting greeting1 = (Greeting)inbox.receive(Duration.create(5, TimeUnit.SECONDS));
		
		System.out.println("Greeting: "+ greeting1.message);
		
		greeter.tell(new WhoToGreet("typesafe"), ActorRef.noSender());
		inbox.send(greeter, new Greet());
		Greeting greeting2 = (Greeting)inbox.receive(Duration.create(5, TimeUnit.SECONDS));
		System.out.println("Greeting: "+ greeting2.message);
		
		ActorRef greetPrinter = system.actorOf(Props.create(GreetPrimer.class));
//		greetPrinter.tell(new WhoToGreet("akkau"), ActorRef.noSender());
		
//		system.scheduler().schedule(Duration.Zero(),Duration.create(1, TimeUnit.SECONDS),greeter,new Greet(),system.dispatcher(),greetPrinter);
		
		system.actorSelection(greeterPath2).tell(new WhoToGreet("typo"), greeter);
		inbox.send(greeter, new Greet());
		greeting1 = (Greeting)inbox.receive(Duration.create(5, TimeUnit.SECONDS));
		
		System.out.println("Greeting: "+ greeting1.message);
		
		
		
		
	}
	
	public static class GreetPrimer extends UntypedActor{

		/* (non-Javadoc)
		 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
		 */
		@Override
		public void onReceive(Object message) throws Exception {
			// TODO Auto-generated method stub
			if(message instanceof Greeting)
				System.out.println(((Greeting)message).message);
			else{
				System.out.println("message: "+message);
			}
		}
		
	}

}











