package io.akka.cluster.sample;

import io.akka.cluster.sample.TransformationMessages.TransformationJob;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.protobuf.msg.ClusterMessages.MemberStatus;
import akka.cluster.Member;

public class TransformationBackend extends UntypedActor{
	
	Cluster cluster = Cluster.get(getContext().system());
	
	public void preStart(){
		cluster.subscribe(getSelf(), MemberUp.class);
	}
	
	public void postStop(){
		cluster.unsubscribe(getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof TransformationJob){
			TransformationJob job = (TransformationJob)message;
			getSender().tell(new TransformationMessages.TransformationResult(job.getText()), getSelf());
		}else if(message instanceof CurrentClusterState){
			CurrentClusterState state = (CurrentClusterState)message;
			for(Member member:state.getMembers()){
				if(member.status().equals(MemberStatus.Up)){
					register(member);
				}
			}
		}else if(message instanceof MemberUp){
			MemberUp mUp = (MemberUp)message;
			register(mUp.member());
		}else{
			unhandled(message);
		}
		
		
	}
	
	public void register(Member member){
		if(member.hasRole("frontend")){
			getContext().actorSelection(member.address()+"");
		}
	}

}
