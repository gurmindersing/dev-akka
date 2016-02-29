package io.akka.cluster.router;

import java.util.HashMap;
import java.util.Map;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;


public class StatsWorker extends UntypedActor{
	Map<String,Integer> cache = new HashMap<String,Integer>();
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof String){
			String word = (String)message;
			Integer length = cache.get(word);
			if(length==null){
				length=word.length();
				cache.put(word, length);
			}
			log.info("sending from stats worker : "+getSelf().path().address().toString());
			getSender().tell(length, getSelf());
		}else{
			unhandled(message);
		}
	}

}
