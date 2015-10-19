/**
 * 
 */
package io.akka.sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.OneForOneStrategy;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.FromConfig;
import akka.routing.RoundRobinPool;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import scala.concurrent.duration.Duration;

/**
 * @author Gurminder
 *
 */

final class Work implements Serializable{
	public final String payload;
	
	public Work(String payload){
		this.payload = payload;
	}
}

class Worker extends UntypedActor{

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof Work){
			System.out.println(getContext().toString()+"Routed message: "+message);
			getSelf().tell(PoisonPill.getInstance(), getSelf());
		}
		
	}
	
	public void preStart(){
		System.out.println("starting worker!!");
	}
	
}
class Master extends UntypedActor{
	
	ActorRef r;
	Router router;
	{
		List<Routee> routees = new ArrayList<Routee>();
		for(int i=0;i<5;i++){
			ActorRef r = getContext().actorOf(Props.create(Worker.class));
			getContext().watch(r);
			routees.add(new ActorRefRoutee(r));
		}
		
		router = new Router(new RoundRobinRoutingLogic(),routees);
		
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ActorSystem system = ActorSystem.create("user");
//		ActorRef master = system.actorOf(Props.create(Master.class), "master");
		/*master.tell(new Work("1"), ActorRef.noSender());
		master.tell(new Work("1"), ActorRef.noSender());
		master.tell(new Work("1"), ActorRef.noSender());
		master.tell(new Work("1"), ActorRef.noSender());
		master.tell(new Work("1"), ActorRef.noSender());*/
//		master.tell(PoisonPill.getInstance(), ActorRef.noSender());
//		master.tell(PoisonPill.getInstance(), ActorRef.noSender());
//		master.tell(PoisonPill.getInstance(), ActorRef.noSender());
//		master.tell(PoisonPill.getInstance(), ActorRef.noSender());
//		master.tell(new Work("1"), ActorRef.noSender());
		ActorRef router = system.actorOf(FromConfig.getInstance().props(Props.create(Worker.class)),"router1");
		SupervisorStrategy supervisor = new OneForOneStrategy(5,Duration.create(5, TimeUnit.SECONDS),
				Collections.<Class<? extends Throwable>>singletonList(Exception.class));
		ActorRef r = system.actorOf(new RoundRobinPool(5).withSupervisorStrategy(supervisor).props(Props.create(Worker.class)), 
			    "router2");
        r.tell(new Work("2"), ActorRef.noSender());
        r.tell(new Exception(), ActorRef.noSender());
	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("in master : "+message);
		if(message instanceof Work){
			router.route(message, getSender());
		}else if(message instanceof Terminated){
			router = router.removeRoutee(((Terminated)message).actor());
			System.out.println("terminated actor:"+getSender());
			ActorRef ref = getContext().actorOf(Props.create(Worker.class));
			getContext().watch(ref);
			router.addRoutee(new ActorRefRoutee(ref));
		}
		
	}

}
