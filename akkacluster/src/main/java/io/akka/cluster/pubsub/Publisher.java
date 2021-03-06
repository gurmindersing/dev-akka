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
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		startup(new String[]{"2551"});
		
	}

	/**
	 * @param strings
	 * @throws InterruptedException 
	 */
	private static void startup(String[] strings) throws InterruptedException {
		// TODO Auto-generated method stub
		String port = strings[0];
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+port)
				.withFallback(ConfigFactory.parseString("akka.cluster.roles=[sub]"))
				.withFallback(ConfigFactory.load());
		
		ActorSystem system = ActorSystem.create("ClusterSystem", config);
		
		ActorRef cluster = system.actorOf(Props.create(SimpleClusterListener.class), "cluster");
		ActorRef publisher = system.actorOf(Props.create(Publisher.class), "publisher");
		Thread.sleep(5000);
		for(int i=0;i<100;i++){
			publisher.tell("Publish this!", null);
		}
	
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
