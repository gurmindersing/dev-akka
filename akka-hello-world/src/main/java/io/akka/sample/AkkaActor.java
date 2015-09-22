package io.akka.sample;

import java.util.ArrayList;
import java.util.Iterator;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;
import scala.concurrent.Future;
import akka.actor.ActorContext;
import akka.actor.ActorIdentity;
import akka.actor.ActorLogging;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Identify;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class AkkaActor extends UntypedActor{
	{
		final ActorRef myChild = getContext().actorOf(Props.create(AkkaActor.class),"childActor");
	}
	LoggingAdapter log =  Logging.getLogger(getContext().system(), this);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ActorSystem system = ActorSystem.create("user");
//		ActorRef actor = system.actorOf(Props.create(AkkaActor.class), "main");
		ActorRef actorA = system.actorOf(Props.create(AkkaActor.class), "actorA");
//		ActorRef actorB = system.actorOf(Props.create(AkkaActor.class), "actorB");
		
		final ArrayList<Future<Object>> futures = new ArrayList<Future<Object>>();
//		Future<Object> obj = ask(actorA,"request",5000);
//		futures.add(ask(actorA,"request",5000));
//		futures.add(ask(actorB,"another request",5000));
		
		final Future<Iterable<Object>> aggregate = Futures.sequence(futures, system.dispatcher());
		/*
		final Future<Result> transformed = aggregate.map(new Mapper<Iterable<Object>,Result>(){
			public Result apply(Iterable<Object>coll){
					final Iterator<Object> it = coll.iterator();
					final String x = (String)it.next();
					final String s = (String)it.next();
					return new Result(x,s);
			}
			
		}, system.dispatcher());
		
		pipe(transformed, system.dispatcher()).to;*/
//		actor.tell(new Identify("identify"), actor);
//		System.out.println(AkkaActor.myChild.getContext());
		system.shutdown();
		

	}
	
	@Override
	  public void preStart() {
	    log.info("Starting");
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
			log.info("message : "+message);
			throw new Exception();
		}else if(message instanceof ActorRef){
			log.info("actorref :" +message);
		}else{
			unhandled(message);
		}
		
	}

}
