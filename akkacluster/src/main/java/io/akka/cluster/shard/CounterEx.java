/**
 * 
 */
package io.akka.cluster.shard;

import akka.actor.ActorSystem;
import akka.actor.UntypedActor;

/**
 * @author gurmi
 *
 */
public class CounterEx extends UntypedActor{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ActorSystem system = ActorSystem.create("main");

	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
