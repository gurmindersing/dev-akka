package io.akka.cluster.router;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberUp;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;
import io.akka.cluster.router.StatsMessages.StatsJob;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;

public class StatsService extends UntypedActor{
	
	ActorRef workerRouter = getContext().actorOf(FromConfig.getInstance().props(Props.create(StatsWorker.class)), "workerRouter");
	Cluster cluster = Cluster.get(getContext().system());
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	public void preStart(){
		System.out.println("in pre start");
		cluster.subscribe(getSelf(), MemberUp.class);
	}
	
	public void postStop(){
		System.out.println("in post stop");
		cluster.unsubscribe(getSelf());
	}
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
					workerRouter.tell(new ConsistentHashableEnvelope(word, word), aggregator);
				}
				
			}
		}else if(message instanceof CurrentClusterState){
			CurrentClusterState state = (CurrentClusterState)message;
			log.info("current cluster state : "+state.getLeader()+"memebers size : "+state.getMembers()+state.getAllRoles());
		}else{
			System.out.println("in else block : "+message);
			unhandled(message);
		}
		
	}

}
