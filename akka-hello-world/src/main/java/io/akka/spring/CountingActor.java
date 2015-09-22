package io.akka.spring;

import javax.inject.Named;

import org.springframework.context.annotation.Scope;

import akka.actor.UntypedActor;

@Named("CountingActor")
@Scope("prototype")
public class CountingActor extends UntypedActor{
	
	public static class Get{}
	
	public static class Count{}
	
	final CountingService countingService;
	
	public CountingActor(CountingService countingService){
		this.countingService=countingService;
	}
	
	private int count = 0;

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof Count){
			count = countingService.increment(count);
		}else if(message instanceof Get){
			getSender().tell(count,getSelf());
		}else{
			unhandled(message);
		}
		
	}

}
