/**
 * 
 */
package io.akka.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * @author gurmi
 *
 */
public class FileReadActor extends UntypedActor{
	
	
	ActorRef mapActor = getContext().actorOf(Props.create(MapActor.class), "mapActor");
	ActorRef reduceActor = getContext().actorOf(Props.create(ReduceActor.class),"reduceActor");
	Map<String,Integer> finalMap = new HashMap<String,Integer>();
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		File file = new File("C:/Users/gurmi/Desktop/mongoProc.log");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		ActorSystem system = ActorSystem.create("user");
		ActorRef fileReadActor = system.actorOf(Props.create(FileReadActor.class), "fileReadActor");
//		fileReadActor.tell(reader, ActorRef.noSender());
		fileReadActor. ask   tell(reader, ActorRef.noSender());
	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object arg0) throws Exception {
		// TODO Auto-generated method stub
		if(arg0 instanceof BufferedReader){
			BufferedReader reader = (BufferedReader)arg0;
			String line;
			while((line=reader. readLine())!=null){
				mapActor.tell(line, getSender());
			}
		}else if(arg0 instanceof ArrayList){
			reduceActor.tell(arg0, getSender());
		}else if(arg0 instanceof HashMap){
			Map<String,Integer> subMap = (HashMap<String,Integer>)arg0;
			Set<String> subKeySet = subMap.keySet();
			Integer count;
			for(String key:subKeySet){
				int subCount = subMap.get(key);
				if((count=finalMap.get(key))!=null){
					count+=subCount;
					finalMap.put(key, count);
				}else{
					finalMap.put(key,count);
				}
			}
			
			getSelf().tell(finalMap, ActorRef.noSender());
		}
		
		
	}

}
