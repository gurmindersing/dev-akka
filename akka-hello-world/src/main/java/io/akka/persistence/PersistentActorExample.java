/**
 * 
 */
package io.akka.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import akka.persistence.Persistence;
import akka.persistence.RecoveryCompleted;
import akka.persistence.SnapshotOffer;
import akka.persistence.UntypedPersistentActor;

/**
 * @author gurmi
 *
 */

class Cmd implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String data;
	
	public Cmd(String data){
		this.data=data;
	}
	
	public String getData(){
		return data;
	}
}

class Evt implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String data;
	
	public Evt(String data){
		this.data=data;
	}
	
	public String getData(){
		return data;
	}
}

class ExampleState implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ArrayList<String> events;
	
	public ExampleState(){
		this(new ArrayList<String>());
	}

	/**
	 * @param arrayList
	 */
	public ExampleState(ArrayList<String> events) {
		// TODO Auto-generated constructor stub
		this.events=events;
	}
	
	public ExampleState copy(){
		return new ExampleState(new ArrayList<String>(events));
	}
	
	public void update(Evt evt){
		events.add(evt.getData());
	}
	
	public int size(){
		return events.size();
	}
	
	public String toString(){
		return events.toString();
	} 	
	
}

class ExamplePersistentActor extends UntypedPersistentActor{
	
	LoggingAdapter log = Logging.getLogger(getContext().system(),this);

	/* (non-Javadoc)
	 * @see akka.persistence.PersistenceIdentity#persistenceId()
	 */
	@Override
	public String persistenceId() {
		// TODO Auto-generated method stub
		return "sample-id-1";
	}
	
	public ExampleState state = new ExampleState();
	
	public int getNumEvents(){
		return state.size();
	}
	
	

	/* (non-Javadoc)
	 * @see akka.persistence.UntypedPersistentActor#onReceiveCommand(java.lang.Object)
	 */
	@Override
	public void onReceiveCommand(Object msg) throws Exception {
		// TODO Auto-generated method stub
		
		if(msg instanceof Cmd){
			final String data = ((Cmd)msg).getData();
			final Evt evt1 = new Evt(data+ "-"+getNumEvents());
			final Evt evt2 = new Evt(data+ "-"+getNumEvents()+1);
			
			persistAll(Arrays.asList(evt1,evt2), new Procedure<Evt>(){

				@Override
				public void apply(Evt evt) throws Exception {
					// TODO Auto-generated method stub
					state.update(evt);
					if(evt.equals(evt2)){
						getContext().system().eventStream().publish(evt);
					}
				}
			});
		}else if(msg.equals("snap")){
			saveSnapshot(state.copy());
		}else if(msg.equals("print")){
			log.info("print : "+state);
//			Persistence.get(getContext().system()).de;
		}else{
			unhandled(msg);
		}
		
	}

	/* (non-Javadoc)
	 * @see akka.persistence.UntypedPersistentActor#onReceiveRecover(java.lang.Object)
	 */
	@Override
	public void onReceiveRecover(Object msg) throws Exception {
		// TODO Auto-generated method stub
		log.info("msg : "+msg.toString());
		if(msg instanceof RecoveryCompleted){
			log.info("Recovery completed");
		}else if(msg instanceof Evt){
			log.info("onreceiverecover evt : "+((Evt)msg).getData());
			state.update((Evt)msg);
		}else if(msg instanceof SnapshotOffer){
			state = (ExampleState)((SnapshotOffer)msg).snapshot();
			log.info("snapshotoffer state : "+state.toString());
		}else{
			unhandled(msg);
		}
		
	}

}

public class PersistentActorExample{
	public static void main(String[] args) throws InterruptedException {
		final ActorSystem system = ActorSystem.create("example",ConfigFactory.load("persistence"));
		final ActorRef persistentActor = system.actorOf(Props.create(ExamplePersistentActor.class), "persistentActor-4-java");
		
		persistentActor.tell(new Cmd("foo"), ActorRef.noSender());
		persistentActor.tell(new Cmd("baz"), ActorRef.noSender());
		persistentActor.tell(new Cmd("bar"), ActorRef.noSender());
		persistentActor.tell("snap", ActorRef.noSender());
		persistentActor.tell(new Cmd("buzz"), ActorRef.noSender());
		persistentActor.tell("print", ActorRef.noSender());
		
		Thread.sleep(1000);
		
		system.terminate();
		
		
	}
}







