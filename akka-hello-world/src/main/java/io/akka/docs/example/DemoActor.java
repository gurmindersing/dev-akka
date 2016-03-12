/**
 * 
 */
package io.akka.docs.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.japi.Creator;
import static akka.pattern.Patterns.ask;

/**
 * @author Gurminder
 *
 */
public class DemoActor extends UntypedActor{
	
	final ActorRef child = this.getContext().actorOf(Props.empty(), "child");
	{
		this.getContext().watch(child);
	}
	
	final ActorRef lastSender = getContext().system().deadLetters();
	
	
	/**
	 * @param args
	 * @throws TimeoutException 
	 */
	public static void main2(String[] args) throws TimeoutException {
		// TODO Auto-generated method stub
		
		ActorSystem system = ActorSystem.create("akka");
		ActorRef demoActor = null;//system.actorOf(DemoActor.props(123));
		
		Inbox inbox  = Inbox.create(system);
		inbox.send(demoActor, "Hello Akka!!");
		
		inbox.watch(demoActor);
		for(int i=0;i<10;i++){
			demoActor.tell(Integer.parseInt("100"), ActorRef.noSender());
		}
		demoActor.tell(PoisonPill.getInstance(), ActorRef.noSender());
		String message = (String)inbox.receive(Duration.create(5, TimeUnit.SECONDS));
		System.out.println("From inbox:"+inbox.receive(Duration.create(5, TimeUnit.SECONDS)));
		
		System.out.println("Inbox: "+message);
		
//		System.exit(0);
		

	}
	
	public static void main(String args[]){
		ActorSystem system= ActorSystem.create("akka");
		ActorRef demo1 = system.actorOf(Props.create(DemoActor.class), "demo1");
		ActorRef demo2 = system.actorOf(Props.create(DemoActor.class), "demo2");
		Inbox inbox= Inbox.create(system);
		inbox.watch(demo1);
//		demo1.tell(PoisonPill.getInstance(), ActorRef.noSender());
//		System.out.println(inbox.receive(Duration.create(1,TimeUnit.SECONDS)));
		
		final ArrayList<Future<Object>> futures = new ArrayList<Future<Object>>();
		futures.add(ask(demo1,Integer.valueOf("100"),1000));
		futures.add(ask(demo2,Integer.valueOf("200"),1000));
		
		System.out.println("in main mat");
		
		/*
		final Future<Iterable<Object>> aggregate = Futures.sequence(futures, system.dispatcher());
		final Future<Result> transformed = aggregate.map(new Mapper<Iterable<Object>,Result>(){
			public Result apply(Iterable<Object> coll){
				final Iterator<Object> it = coll.iterator();
				final String x = (String)it.next();
				final String y = (String)it.next();
				return new Result(x,s);
			}
		},system.dispatcher());*/
	}
	
	/*
	public static Props props(final int magicNumber){
		/*return Props.create(new Creator<DemoActor>(){

			public DemoActor create() throws Exception {
				// TODO Auto-generated method stub
				return new DemoActor(magicNumber);
			}
			
		});*/
		/*
		return Props.create(DemoActor.class,()->new DemoActor(magicNumber));
		
		
	}*/
	
	/*public static Props props(final int magicNumber) {
	    return Props.create(new Creator<DemoActor>() {
	      private static final long serialVersionUID = 1L;
	 
	      @Override
	      public DemoActor create() throws Exception {
	        return new DemoActor(magicNumber);
	      }
	    });
	  }*/
	
	final int magicNumber;
	
	public DemoActor(int magicNumber){
		this.magicNumber = magicNumber;
	}
	
	public DemoActor(){
//		this.magicNumber = magicNumber;
		this.magicNumber=5;
	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof String){
			System.out.println(message);
//			getSender().tell(message, getSelf());
			ActorSelection select = getContext().actorSelection("/akka/user/child");
			System.out.println("select "+select.anchorPath().toSerializationFormat());
			getContext().stop(child);
			System.out.println("child killed");
			System.out.println("select "+select.anchorPath().toSerializationFormat());
			select.anchor().tell("Hello", getSelf());
			System.out.println("sent message to killed child!");
			child.tell("hello", getSelf());
			child.tell(new Integer("123"), getSelf());
			
		}else if(message instanceof Integer){
			Thread.sleep(1000);
			System.out.println("sleep:"+message);
		}else if(message instanceof Terminated){
			System.out.println("terminated");
		}
			
			else{
			unhandled(message);
		}
		
	}
	
	

}

class A extends UntypedActor{
final ActorRef child =
getContext().actorOf(Props.create(MyUntypedActor.class), "myChild");

/* (non-Javadoc)
 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
 */
@Override
public void onReceive(Object message) throws Exception {
	// TODO Auto-generated method stub
	if(message instanceof String){
		System.out.println("child: "+message);
		getSender().tell(message, getSelf());
	}else if(message instanceof Terminated){
		System.out.println("child:terminated");
	}else{
		unhandled(message);
	}
	
}
}

