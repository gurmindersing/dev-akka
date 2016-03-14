/**
 * 
 */
package io.akka.persistence;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import akka.persistence.UntypedPersistentActor;

/**
 * @author gurmi
 *
 */
public class MyPersistentActor extends UntypedPersistentActor{

	

	/* (non-Javadoc)
	 * @see akka.persistence.PersistenceIdentity#persistenceId()
	 */
	@Override
	public String persistenceId() {
		// TODO Auto-generated method stub
		return "some-persistent-id";
	}

	/* (non-Javadoc)
	 * @see akka.persistence.UntypedPersistentActor#onReceiveCommand(java.lang.Object)
	 */
	@Override
	public void onReceiveCommand(Object msg) throws Exception {
		// TODO Auto-generated method stub
		sender().tell(msg, self());
		/*persistAsync(String.format("evt-%s-1", msg), new Procedure<String>(){

			@Override
			public void apply(String event) throws Exception {
				// TODO Auto-generated method stub
				sender().tell(event, self());
				
			}			
		});
		
		persistAsync(String.format("evt-%s-2", msg),new Procedure<String>(){

			@Override
			public void apply(String event) throws Exception {
				// TODO Auto-generated method stub
//				Thread.sleep(3000);
				sender().tell(event, self());
				
			}
			
		});
		
		deferAsync(String.format("evt-%s-3", msg),new Procedure<String>(){

			@Override
			public void apply(String event) throws Exception {
				// TODO Auto-generated method stub
//				Thread.sleep(3000);
				sender().tell(event, self());
				
			}
			
		});
		*/
		
		final Procedure<String> replyToSender = new Procedure<String>(){

			@Override
			public void apply(String msg) throws Exception {
				// TODO Auto-generated method stub
				sender().tell(msg, self());
				
			}
			
		};
		final Procedure<String> outer1Callback = new Procedure<String>(){

			@Override
			public void apply(String msg) throws Exception {
				// TODO Auto-generated method stub
				sender().tell(msg, self());
				persistAsync(String.format("%s-inner-1", msg),replyToSender);
				
			}
			
		};
		
		final Procedure<String> outer2Callback = new Procedure<String>(){

			@Override
			public void apply(String msg) throws Exception {
				// TODO Auto-generated method stub
				sender().tell(msg, self());
				persistAsync(String.format("%s-inner-2", msg),outer1Callback);
				
			}
			
		};
		
		
		persistAsync(String.format("%s-outer-1", msg),outer2Callback);
		persistAsync(String.format("%s-outer-1", msg),outer2Callback);
	}

	/* (non-Javadoc)
	 * @see akka.persistence.UntypedPersistentActor#onReceiveRecover(java.lang.Object)
	 */
	@Override
	public void onReceiveRecover(Object msg) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ActorSystem system = ActorSystem.create("system",ConfigFactory.load("persistence"));
		ActorRef myactor = system.actorOf(Props.create(MyPersistentActor.class), "myactor");
		ActorRef actorMsg = system.actorOf(Props.create(ActorMsg.class), "actorMsg");
		myactor.tell("a", actorMsg);
		myactor.tell("b", actorMsg);
		
//		system.terminate();

	}	

}

class ActorMsg extends UntypedActor{
	
	LoggingAdapter log = Logging.getLogger(context().system(), this);

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		log.info("message recv :"+ message);
		
		
	}
	
}
