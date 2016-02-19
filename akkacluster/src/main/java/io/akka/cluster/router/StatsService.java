package io.akka.cluster.router;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.FromConfig;
import io.akka.cluster.router.StatsMessages.StatsJob;;

public class StatsService extends UntypedActor{
	
	ActorRef workerRouter = getContext().actorOf(FromConfig.getInstance().props(Props.create(StatsWorker.class)), "workerRouter");

	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof StatsJob){
			StatsJob job = (StatsJob)message;
			if(job.getText().equals("")){
				unhandled(message);
			}else{
				final String[] words = job.getText().split(" ");
				final ActorRef replyTo = getSender();
				
				ActorRef aggregator = getContext().actorOf(Props.create(StatsAggregator.class, words.length,replyTo));
				
				for(String word:words){
					workerRouter.tell(word, aggregator);
				}
				
			}
		}else{
			unhandled(message);
		}
		
	}

}
