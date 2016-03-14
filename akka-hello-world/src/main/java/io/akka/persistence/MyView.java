/**
 * 
 */
package io.akka.persistence;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.UntypedPersistentView;
import akka.persistence.Update;

/**
 * @author gurmi
 *
 */
public class MyView extends UntypedPersistentView{
	
	LoggingAdapter log = Logging.getLogger(context().system(), this);
	
	public String persistenceId(){
		return "some-persistent-id";
	}

	
	/* (non-Javadoc)
	 * @see akka.persistence.PersistentView#viewId()
	 */
	@Override
	public String viewId() {
		// TODO Auto-generated method stub
		return "my-stable-persistence-view-id";
	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(isPersistent()){
			log.info(message.toString());
		}else if(message instanceof String){
			log.info(message.toString());
		}else{
			log.info(message.toString());
			unhandled(message);
		}
		
	}
	
	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("system", ConfigFactory.load("persistence"));
		ActorRef view = system.actorOf(Props.create(MyPersistentActor.class), "view");
		view.tell(Update.create(true), null);
	}

}
