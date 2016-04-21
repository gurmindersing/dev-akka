/**
 * 
 */
package io.akka.cluster.shard;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.cluster.sharding.ShardRegion;
import akka.japi.Option;
import akka.japi.Procedure;
import akka.pattern.Patterns;
import akka.persistence.UntypedPersistentActor;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * @author gurmi
 *
 */
public class Counter extends UntypedPersistentActor {
	 
	  public static enum CounterOp {
	    INCREMENT, DECREMENT
	  }
	 
	  public static class Get {
	    final public long counterId;
	 
	    public Get(long counterId) {
	      this.counterId = counterId;
	    }
	  }
	 
	  public static class EntityEnvelope {
	    final public long id;
	    final public Object payload;
	 
	    public EntityEnvelope(long id, Object payload) {
	      this.id = id;
	      this.payload = payload;
	    }
	  }
	 
	  public static class CounterChanged {
	    final public int delta;
	 
	    public CounterChanged(int delta) {
	      this.delta = delta;
	    }
	  }
	 
	  int count = 0;
	 
	  // getSelf().path().name() is the entity identifier (utf-8 URL-encoded)
//	  @Override
	  public String persistenceId() {
	    return "Counter-" + getSelf().path().name();
	  }
	 
	  @Override
	  public void preStart() throws Exception {
	    super.preStart();
	    context().setReceiveTimeout(Duration.create(120, TimeUnit.SECONDS));
	  }
	 
	  void updateState(CounterChanged event) {
	    count += event.delta;
	  }
	 
	  @Override
	  public void onReceiveRecover(Object msg) {
	    if (msg instanceof CounterChanged)
	      updateState((CounterChanged) msg);
	    else
	      unhandled(msg);
	  }
	 
	  @Override
	  public void onReceiveCommand(Object msg) {
	    if (msg instanceof Get)
	      getSender().tell(count, getSelf());
	 
	    else if (msg == CounterOp.INCREMENT)
	      persist(new CounterChanged(+1), new Procedure<CounterChanged>() {
	        public void apply(CounterChanged evt) {
	          updateState(evt);
	        }
	      });
	 
	    else if (msg == CounterOp.DECREMENT)
	      persist(new CounterChanged(-1), new Procedure<CounterChanged>() {
	        public void apply(CounterChanged evt) {
	          updateState(evt);
	        }
	      });
	 
	    else if (msg.equals(ReceiveTimeout.getInstance()))
	      getContext().parent().tell(
	          new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
	 
	    else
	      unhandled(msg);
	  }
	  
	  
	  
	  public static void main(String[] args) {
		  
		ActorSystem system = ActorSystem.create("user");
		Option<String> roleOption = Option.none();
		ClusterShardingSettings settings = ClusterShardingSettings.create(system);
		ActorRef startedCounterRegion = ClusterSharding.get(system).start("Counter",Props.create(Counter.class),settings,messageExtractor);
		
		ActorRef counterRegion = ClusterSharding.get(system).shardRegion("Counter");
		scala.concurrent.Future<Object> future = Patterns.ask(counterRegion, new Counter.Get(123), new Timeout(new FiniteDuration(5, TimeUnit.SECONDS)));
		try {
			Await.result(future, Duration.create(5, TimeUnit.SECONDS));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		counterRegion.tell(new Counter.Get(123), ActorRef.noSender());
		counterRegion.tell(new Counter.EntityEnvelope(123, Counter.CounterOp.INCREMENT),ActorRef.noSender() );
		scala.concurrent.Future<Object> future2 = Patterns.ask(counterRegion, new Counter.Get(123), new Timeout(new FiniteDuration(5, TimeUnit.SECONDS)));
		try {
			Await.result(future2, Duration.create(5, TimeUnit.SECONDS));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static ShardRegion.MessageExtractor messageExtractor = new ShardRegion.MessageExtractor() {
		
		public String shardId(Object message) {
			int numOfShards = 100;
			if(message instanceof Counter.EntityEnvelope){
				long id = ((Counter.EntityEnvelope)message).id;
				return String.valueOf(id%numOfShards);
			}else if(message instanceof Counter.Get){
				long id = ((Counter.Get)message).counterId;
				return String.valueOf(id%numOfShards);
			}else
				
			return null;
		}
		
		public String entityId(Object message) {
			// TODO Auto-generated method stub
			
			if(message instanceof Counter.EntityEnvelope)
				return String.valueOf(((Counter.EntityEnvelope)message).id);
			else if(message instanceof Counter.Get)
				return String.valueOf(((Counter.Get)message).counterId);
			else
				return null;
		}
		
		public Object entityMessage(Object message){
			if(message instanceof Counter.EntityEnvelope)
				return ((Counter.EntityEnvelope)message).payload;
			else
				return message;
		}
	};
	
	
}
