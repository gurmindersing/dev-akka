package io.akka.cluster.sample;

import io.akka.cluster.sample.TransformationMessages.TransformationJob;
import io.akka.cluster.sample.TransformationMessages.JobFailed;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class TransformationFrontend extends UntypedActor{
	
	List<ActorRef> backend = new ArrayList<ActorRef>();
	int jobCounter=0;

	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if((message instanceof TransformationJob) && backend.isEmpty()){
			TransformationJob job = (TransformationJob)message;
			getSender().tell(new JobFailed("Service not available", job),getSender());
			
		}else if (message instanceof TransformationJob){
			TransformationJob job = (TransformationJob)message;
			jobCounter++;
			backend.get(jobCounter % backend.size()).forward(job, getContext());
		}else {
			unhandled(message);
		}
		
	}
	
	

}
