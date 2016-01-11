/**
 * 
 */
package io.akka.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * @author gurmi
 *
 */
public class ReduceActor extends UntypedActor{

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object arg0) throws Exception {
		// TODO Auto-generated method stub
		Map<String,Integer> map = new HashMap<String,Integer>();
		NavigableMap<String, Integer> reducedMap = new ConcurrentSkipListMap<String, Integer>();
		if(arg0 instanceof ArrayList){
			ArrayList<String> list = (ArrayList<String>)arg0;
			/*list.forEach((temp)->{
				Set<String> keys = map.keySet();
				if(keys.contains(temp)){
				Integer count = map.get(temp);
				count++;
				map.put(temp,count);
				}else{
					map.put(temp, 1);
				}
			});*/
			Set<String> keys = map.keySet();
			Iterator<String> it = list.iterator();
			while(it.hasNext()){
				String word = it.next();
				if(keys.contains(word)){
					Integer wordCtr = map.get(word);
					wordCtr++;
					map.put(word, wordCtr);
				}else{
					map.put(word, 1);
				}
			}
			
			System.out.println("Send submap to master");
			getSender().tell(map, ActorRef.noSender());
		}else{
			unhandled(arg0);
		}
		
		
		
	}

}
