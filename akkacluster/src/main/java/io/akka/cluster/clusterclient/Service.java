/**
 * 
 */
package io.akka.cluster.clusterclient;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * @author gurmi
 *
 */
public class Service extends UntypedActor{
	
	LoggingAdapter log = Logging.getLogger(getContext().system(),this);

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof String){
			log.info(message.toString());
		}else{
			unhandled(message);
		}
		
	}

}
