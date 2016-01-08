/**
 * 
 */
package io.akka.file;

import java.util.ArrayList;
import java.util.StringTokenizer;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * @author gurmi
 *
 */
public class MapActor extends UntypedActor{

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object arg0) throws Exception {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		if(arg0 instanceof String){
			String message = (String)arg0;
			StringTokenizer st = new StringTokenizer(message);
			while(st.hasMoreTokens()){
				list.add(st.nextToken());
			}
		}else{
			unhandled(arg0);
		}
		
		getSender().tell(list, ActorRef.noSender());
		
	}

}
