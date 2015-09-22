package io.akka.sample;

import java.util.ArrayList;

import scala.concurrent.Future;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Futures;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	// TODO Auto-generated method stub
		
    			ActorSystem system = ActorSystem.create("user");
//    			ActorRef actor = system.actorOf(Props.create(AkkaActor.class), "main");
    			ActorRef actorA = system.actorOf(Props.create(AkkaActor.class), "actorA");
//    			ActorRef actorB = system.actorOf(Props.create(AkkaActor.class), "actorB");
    			
    			final ArrayList<Future<Object>> futures = new ArrayList<Future<Object>>();
//    			Future<Object> obj = ask(actorA,"request",5000);
//    			futures.add(ask(actorA,"request",5000));
//    			futures.add(ask(actorB,"another request",5000));
    			
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
//    			actor.tell(new Identify("identify"), actor);
//    			System.out.println(AkkaActor.myChild.getContext());
    			system.shutdown();
    			
    }
}
