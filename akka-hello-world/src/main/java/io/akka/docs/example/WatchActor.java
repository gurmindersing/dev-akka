/**
 * 
 */
package io.akka.docs.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

/**
 * @author Gurminder
 *
 */
public class WatchActor extends UntypedActor{
	
	final ActorRef child = this.getContext().actorOf(Props.empty(), "child");
	{
		this.getContext().watch(child);
	}
	
	ActorRef lastSender = getContext().system().deadLetters();
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ActorSystem system = ActorSystem.create("akka");
//		ActorRef parent = system.actorOf(, name)

	}



	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		
		if(message.equals("kills")){
			getContext().stop(child);
			lastSender = getSender();
			
		}else if(message instanceof Terminated){
			
			final Terminated t = (Terminated)message;
			if(t.getActor()== child){
				lastSender.tell("finished",getSelf());
			}
			
		}else{
			unhandled(message);
		}
		
	}

}
