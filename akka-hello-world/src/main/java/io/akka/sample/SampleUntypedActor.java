/**
 * 
 */
package io.akka.sample;

import akka.actor.UntypedActor;

/**
 * @author Gurminder Singh
 *
 */
public class SampleUntypedActor extends UntypedActor{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof String){
//			getContext().
		}
	}

}
