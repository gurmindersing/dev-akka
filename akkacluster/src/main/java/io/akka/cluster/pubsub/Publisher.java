/**
 * 
 */
package io.akka.cluster.pubsub;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * @author gurmi
 *
 */
public class Publisher extends UntypedActor{
	
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		startup(new String[]{"0","2551","2552"});
		
	}

	/**
	 * @param strings
	 */
	private static void startup(String[] strings) {
		// TODO Auto-generated method stub
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+0)
				.withFallback(ConfigFactory.parseString("akka.cluster.roles=[sub]"))
				.withFallback(ConfigFactory.load());
		
		ActorSystem system = ActorSystem.create("ClusterSystem", config);
		ActorRef publisher = system.actorOf(Props.create(Publisher.class), "publisher");
		publisher.tell("Publish this!", publisher);
	
	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof String){
			String in = (String)message;
			log.info("publishing message : "+message);
			mediator.tell(new DistributedPubSubMediator.Publish("content", in.toUpperCase()), getSelf());
		}else{
			unhandled(message);
		}
		
	}

}
