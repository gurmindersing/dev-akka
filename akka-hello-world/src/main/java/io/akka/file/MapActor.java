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
			System.out.println("In map actor :: "+message);
			if(message.equals("DISPLAY_LIST")){
				getSender().tell("DISPLAY_LIST", ActorRef.noSender());
			}else{
				StringTokenizer st = new StringTokenizer(message);
				while(st.hasMoreTokens()){
					list.add(st.nextToken());
				}
				getSender().tell(list, ActorRef.noSender());
			}
		}else{
			unhandled(arg0);
		}
		
		
		
	}

}
