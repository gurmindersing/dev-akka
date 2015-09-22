/**
 * 
 */
package io.akka.docs.example;

import java.security.Identity;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Identify;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

/**
 * @author Gurminder
 *
 */
public class Follower extends UntypedActor{
	
	final String identityId = "1";
	{
		ActorSelection selection = getContext().actorSelection("/user/another");
		selection.tell(new Identify(identityId), getSelf());
	}
	
	ActorRef another;
	
	final ActorRef probe;
	public Follower(ActorRef probe){
		this.probe=probe;
	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof ActorIdentity){
			ActorIdentity identity = (ActorIdentity)message;
			if(identity.correlationId().equals(identityId)){
				ActorRef ref = identity.getRef();
				if(ref == null)
					getContext().stop(getSelf());
				else{
					another = ref;
					getContext().watch(another);
					probe.tell(ref, getSelf());
				}
			}
			
		}else if(message instanceof Terminated){
			final Terminated t = (Terminated)message;
			if(t.getActor().equals(another)){
				getContext().stop(getSelf());
			}
		}else{
			unhandled(message);
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ActorSystem system = ActorSystem.create("user");
		system.actorOf(Props.create(Fo, args), name)

	}

}
