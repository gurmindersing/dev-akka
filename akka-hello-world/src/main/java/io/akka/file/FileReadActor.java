/**
 * 
 */
package io.akka.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.typesafe.config.Config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Envelope;
import akka.dispatch.PriorityGenerator;
import akka.dispatch.UnboundedPriorityMailbox;
import akka.pattern.Patterns;
import akka.routing.RoundRobinPool;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * @author gurmi
 *
 */
public class FileReadActor extends UntypedActor{
	
	
	ActorRef mapActor = getContext().actorOf(new RoundRobinPool(5).props(Props.create(MapActor.class).withDispatcher("priorityMailBox-dispatcher")), 
			"mapActor");
	ActorRef reduceActor = getContext().actorOf(new RoundRobinPool(5).props(Props.create(ReduceActor.class)),
			"reduceActor");
	Map<String,Integer> finalMap = new HashMap<String,Integer>();
	ActorRef finalSender = null;	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		File file = new File("C:/Users/gurmi/Desktop/a.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		ActorSystem system = ActorSystem.create("user");
		ActorRef fileReadActor = system.actorOf(Props.create(FileReadActor.class), "fileReadActor");
//		fileReadActor.tell(reader, ActorRef.noSender());
		Timeout timeout = new Timeout(Duration.create(50, TimeUnit.SECONDS));
		Future<Object> future = Patterns.ask(fileReadActor, reader, timeout);
		System.out.println();
		Map finalMap = (HashMap)Await.result(future, timeout.duration());
		System.out.println("final map : "+finalMap.toString());
		
	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object arg0) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("arg0 sender : "+getSender().path()+" : "+arg0);
		if(arg0 instanceof BufferedReader){
			BufferedReader reader = (BufferedReader)arg0;
			String line;
			while((line=reader.readLine())!=null){
				System.out.println("sending line to map actor :"+line);
				mapActor.tell(line, getSelf());
			}
			mapActor.tell("DISPLAY_LIST", getSelf());
//			System.out.println("get sender : "+getSender().path());
			finalSender=getSender();
			
		}else if(arg0 instanceof ArrayList){
			List list=(ArrayList)arg0;
			System.out.println("sending list to reduce actor :"+list.toArray().toString());
			reduceActor.tell(arg0, getSelf());
		}else if(arg0 instanceof HashMap){
			Map<String,Integer> subMap = (HashMap<String,Integer>)arg0;
			Set<String> subKeySet = subMap.keySet();
			System.out.println("recieved set from reduce actor :"+subKeySet.toArray().toString());
			Integer count;
			for(String key:subKeySet){
				System.out.println("key:"+key+"::"+subMap.get(key));
				Integer subCount = subMap.get(key);
				if((count=finalMap.get(key))!=null){
					count+=subCount;
					finalMap.put(key, count);
				}else{
					finalMap.put(key,count);
				}
			}
			
		}else if(arg0 instanceof String){
			System.out.println("final map to send size :: "+finalMap.size());
			System.out.println("getSender() :: "+finalSender.path());
			finalSender.tell(finalMap, getSelf());
		}
		
		
	}
	
	public static class MyPriorityMailBox extends UnboundedPriorityMailbox{

		/**
		 * @param cmp
		 */
		public MyPriorityMailBox(ActorSystem.Settings settings, Config config) {
			super(new PriorityGenerator(){

				@Override
				public int gen(Object message) {
					// TODO Auto-generated method stub
					System.out.println("In mailbox : "+message);
					if(message.equals("DISPLAY_LIST")){
						return 2;
					}else if(message.equals(PoisonPill.getInstance())){
						return 0;
					}else{
						return 1;
					}
				}
				
			});
			// TODO Auto-generated constructor stub
		}

		
		
	}

}


