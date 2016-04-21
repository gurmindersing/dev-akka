package io.akka.sample;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorContext;
import akka.actor.ActorIdentity;
import akka.actor.ActorLogging;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Identify;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.actor.TypedActor;
import akka.actor.TypedActorExtension;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.CircuitBreaker;

public class AkkaActor extends UntypedActor{
	{
//		final ActorRef myChild = getContext().actorOf(Props.create(AkkaActor.class),"childActor");
	}
	LoggingAdapter log =  Logging.getLogger(getContext().system(), this);
	CircuitBreaker breaker;

	public static void main2(String[] args) {
		// TODO Auto-generated method stub
		
		ActorSystem system = ActorSystem.create("user");
//		ActorRef actor = system.actorOf(Props.create(AkkaActor.class), "main");
		ActorRef actorA = system.actorOf(Props.create(AkkaActor.class), "actorA");
		ActorRef actorB = system.actorOf(Props.create(AkkaActor.class), "actorB");
		
		final ArrayList<Future<Object>> futures = new ArrayList<Future<Object>>();
		System.out.println("ask(actorA,'request1',5000)"+ask(actorA,"request1",5000));
		Future<Object> obj = ask(actorA,"request2",5000);
//		obj.onSuccess(arg0, arg1);
		futures.add(ask(actorA,"request3",5000));
		futures.add(ask(actorB,"another request",5000));
		
		final Future<Iterable<Object>> aggregate = Futures.sequence(futures, system.dispatcher());
		ask(actorA,PoisonPill.getInstance(),5000);
		ask(actorB,PoisonPill.getInstance(),5000);
		ask(actorA,"PoisonPill.getInstance()",5000);
		ask(actorB,"PoisonPill.getInstance()",5000);
		
		TypedActorExtension extension = TypedActor.get(system);
		
		/*
		final Future<Result> transformed = aggregate.map(new Mapper<Iterable<Object>,Result>(){
			public Result apply(Iterable<Object>coll){
					final Iterator<Object> it = coll.iterator();
					final String x = (String)it.next();
					final String s = (String)it.next();
					return new Result(x,s);
			}
			
		}, system.dispatcher());*/
		
//		pipe(transformed, system.dispatcher()).to;
//		actor.tell(new Identify("identify"), actor);
//		System.out.println(AkkaActor.myChild.getContext());
//		system.shutdown();
		

	}
	
	public static void main(String args[]) throws InterruptedException{
		ActorSystem system = ActorSystem.create("user");
		ActorRef actorA = system.actorOf(Props.create(Manager.class), "actorA");
		ask(actorA,"job",2000);
//		ask(actorA,"shutdown",2000);
		
		
		system.scheduler().schedule(Duration.Zero(), Duration.create(50, TimeUnit.MILLISECONDS), 
				actorA,"Tick", system.dispatcher(),null);
		Thread.sleep(500);
		System.out.println("before ");
//		ask(actorA,system,2000);
	}
	
	@Override
	  public void preStart() {
	    log.info("Starting");
	  }
	
	@Override
	  public void postStop() {
	    log.info("Stopped");
	  }

	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
//		System.out.println(message);
		if (message instanceof ActorIdentity) {
			System.out.println(message);
		      ActorIdentity identity = (ActorIdentity) message;
//		      if (identity.correlationId().equals(identifyId)) {
		        ActorRef ref = identity.getRef();
//		        if (ref == null)
		        System.out.println("sending");
		        log.info("sending...");
//		        System.out.println(getSender().isTerminated());
		        getSender().tell("hello", getSelf());
//		          getContext().stop(getSelf());
		        /*else {
		          another = ref;
		          getContext().watch(another);
		          probe.tell(ref, getSelf());
		        }*/
//		      }
		 }else if(message instanceof String){
//			System.out.println("message : "+message);
//			 getContext().setReceiveTimeout(Duration.create(1,TimeUnit.SECONDS));
			log.info("message : "+message);
//			Thread.sleep(5000);
			getSender().tell("retruned:"+message, getSelf());
//			throw new Exception();
			this.breaker=new CircuitBreaker(getContext().dispatcher(), getContext().system().scheduler(), 5, 
					Duration.create(10, TimeUnit.SECONDS), Duration.create(1, TimeUnit.MINUTES));
			
		}else if (message instanceof ReceiveTimeout) {
		      // To turn it off
			log.info("timeout catch");
//		      getContext().setReceiveTimeout(Duration.Undefined());
		    }else if(message instanceof ActorRef){
			log.info("actorref :" +message);
		}else{
			unhandled(message);
		}
		
	}

}
