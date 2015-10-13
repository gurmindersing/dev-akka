/**
 * 
 */
package io.akka.sample;

import java.util.concurrent.TimeUnit;

import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Option;
import static akka.pattern.Patterns.ask;

/**
 * @author Gurminder
 *
 */
public class Child extends UntypedActor{
	
	int state = 0;

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object o) throws Exception {
		// TODO Auto-generated method stub
		if(o instanceof Exception){
			throw (Exception)o;
		}else if(o instanceof Integer){
			state = (Integer)o;
		}else if(o.equals("get")){
			getSender().tell(state, getSelf());
		}else{
			unhandled(o);
		}
		
	}
	/*
	public void preRestart(Throwable t,Option<Object> option){
//		super();
		System.out.println("before restarting");
		
	}*/
	
	@Override
	public void preRestart(Throwable cause, Option<Object> msg) {
	   // do not kill all children, which is the default here
	}

	
	public static void main(String args[]) throws Exception{
		ActorSystem system = ActorSystem.create("user");
		Props superprops = Props.create(Supervisor.class);
		ActorRef supervisor = system.actorOf(superprops, "supervisor");
//		ActorRef child = (ActorRef)Await.result(ask(supervisor,Props.create(Child.class)),5000),5000);
		ActorRef child = (ActorRef)Await.result(ask(supervisor,Props.create(Child.class), 5000), Duration.create(5000,TimeUnit.MILLISECONDS));
//		child.tell(new Exception(), ActorRef.noSender());
		child.tell(23, ActorRef.noSender());
		child.tell("gets", ActorRef.noSender());
		child.tell(23, ActorRef.noSender());
		child.tell(new NullPointerException(), ActorRef.noSender());
		System.out.println(Await.result(ask(child, "get", 5000), Duration.create(5000,TimeUnit.MILLISECONDS)));
//		child.tell(new IllegalArgumentException(), ActorRef.noSender());
		
		//supervisor used to create child elemenets

		
	}
	
	

}
