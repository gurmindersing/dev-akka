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
public class Subscriber extends UntypedActor{
	
	LoggingAdapter log = Logging.getLogger(getContext().system(),this);
	
	public Subscriber(){
		ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
		mediator.tell(new DistributedPubSubMediator.Subscribe("content",getSelf()),getSelf());
	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof String){
			log.info("Got {}",message);
		}else if(message instanceof DistributedPubSubMediator.SubscribeAck){
			log.info("Subscribing");
		}else{
			log.info(message.toString());
			unhandled(message);
		}
		
	}
	public static void main(String[] args) {
		if (args.length == 0)
		      startup(new String[] { "2551", "2552", "0" });
		
		
	}
	
		/**
	 * @param strings
	 */
	private static void startup(String[] ports) {
		// TODO Auto-generated method stub
		
		for(String port:ports){
		Config config = ConfigFactory.parseString(
		          "akka.remote.netty.tcp.port=" + port).withFallback(ConfigFactory.parseString("akka.cluster.roles=[sub]"))
				.withFallback(ConfigFactory.load());

        // Create an Akka system
        ActorSystem system = ActorSystem.create("ClusterSystem", config);
        system.actorOf(Props.create(Subscriber.class), "subscriber1");
        system.actorOf(Props.create(Subscriber.class), "subscriber2");
        system.actorOf(Props.create(Subscriber.class), "subscriber3");
		}
		      
		      
	}

}
