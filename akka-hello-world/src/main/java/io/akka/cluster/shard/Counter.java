package io.akka.cluster.shard;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.PoisonPill;
import akka.actor.ReceiveTimeout;
import akka.japi.Procedure;
import akka.persistence.UntypedPersistentActor;

public class Counter extends UntypedPersistentActor{
	
	public static enum CounterOp{
		INCREMENT, DECREMENT
	}
	
    public static class Get{
    	final public long counterId;
    	
    	public Get(long counterId){
    		this.counterId=counterId;
    	}
    }
    
    public static class EntityEnvelope{
    	final public long id;
    	final public Object payload;
    	
    	public EntityEnvelope(long id, Object payload){
    		this.id= id;
    		this.payload=payload;
    	}
    }
    
    public static class CounterChanged{
    	final public int delta;
    	
    	public CounterChanged(int delta){
    		this.delta=delta;
    	}
    }
    
    int count = 0;
    
    @Override
	public String persistenceId() {
		// TODO Auto-generated method stub
		return "Counter-"+getSelf().path().name();
	}

    public void preStart() throws Exception{
    	super.preStart();
    	context().setReceiveTimeout(Duration.create(120,TimeUnit.SECONDS));
    }
    
    void updateState(CounterChanged event){
    	count+=event.delta;
    	
    }
    
    
    
	@Override
	public void onReceiveCommand(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof Get){
			getSender().tell(message, getSelf());
		}else if(message==CounterOp.INCREMENT)
			persist(new CounterChanged(+1), new Procedure<CounterChanged>(){

				@Override
				public void apply(CounterChanged evt) throws Exception {
					// TODO Auto-generated method stub
					updateState(evt);
				}
				
			});
		else if(message==CounterOp.DECREMENT)
			persist(new CounterChanged(-1), new Procedure<CounterChanged>(){

				@Override
				public void apply(CounterChanged evt) throws Exception {
					// TODO Auto-generated method stub
					updateState(evt);
				}
				
			});
		else if(message.equals(ReceiveTimeout.getInstance()))
			getContext().parent().tell(new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
		
	}

	@Override
	public void onReceiveRecover(Object msg) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
