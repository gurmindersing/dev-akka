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

public class PublisherNoSeed extends UntypedActor{

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		startup(new String[]{"0"});

	}

	private static void startup(String[] strings) {
		// TODO Auto-generated method stub
		
		String port = strings[0];
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+port).withFallback(ConfigFactory.parseString("akka.cluster.roles=[sub]"))
				.withFallback(ConfigFactory.load());
		ActorSystem system = ActorSystem.create("ClusterSystem",config);
		system.actorOf(Props.create(SimpleClusterListener.class), "cluster");
		ActorRef publisher = system.actorOf(Props.create(PublisherNoSeed.class),"publisher");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<100;i++){
			publisher.tell("Publish this no seed!", null);
		}
		
	}

	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof String){
			String in = (String)message;
			log.info("publishing message no seed : "+message);
			mediator.tell(new DistributedPubSubMediator.Publish("content", in.toUpperCase()), getSelf());
		}else{
			unhandled(message);
		}
		
	}

}
