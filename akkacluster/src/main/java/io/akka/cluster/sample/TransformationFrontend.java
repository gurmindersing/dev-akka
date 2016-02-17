package io.akka.cluster.sample;

import io.akka.cluster.sample.TransformationMessages.TransformationJob;
import io.akka.cluster.sample.TransformationMessages.JobFailed;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

public class TransformationFrontend extends UntypedActor{
	
	List<ActorRef> backends = new ArrayList<ActorRef>();
	int jobCounter=0;

	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if((message instanceof TransformationJob) && backends.isEmpty()){
			TransformationJob job = (TransformationJob)message;
			getSender().tell(new JobFailed("Service not available", job),getSender());
			
		}else if (message instanceof TransformationJob){
			TransformationJob job = (TransformationJob)message;
			jobCounter++;
			backends.get(jobCounter % backends.size()).forward(job, getContext());
		}else if(message.equals(TransformationMessages.BACKEND_REGISTRATION)){
			getContext().watch(getSender());
			backends.add(getSender());
		}else if(message instanceof Terminated){
			backends.remove(((Terminated)message).getActor());
		}else {
			unhandled(message);
		}
		
	}
	
	

}
