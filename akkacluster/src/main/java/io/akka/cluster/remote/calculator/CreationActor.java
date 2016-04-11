/**
 * 
 */
package io.akka.cluster.remote.calculator;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * @author gurmi
 *
 */
public class CreationActor extends UntypedActor{

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
		
		if(message instanceof Op.MathOp){
			ActorRef calculator = getContext().actorOf(Props.create(CalculatorActor.class));
			calculator.tell(message, getSelf());
			
		}else if (message instanceof Op.MultiplicationResult) {
	      Op.MultiplicationResult result = (Op.MultiplicationResult) message;
	      System.out.printf("Mul result: %d * %d = %d\n", result.getN1(),
	          result.getN2(), result.getResult());
	      getContext().stop(getSender());

	    } else if (message instanceof Op.DivisionResult) {
	      Op.DivisionResult result = (Op.DivisionResult) message;
	      System.out.printf("Div result: %.0f / %d = %.2f\n", result.getN1(),
	          result.getN2(), result.getResult());
	      getContext().stop(getSender());

	    }else{
	    	unhandled(message);
	    }
		
	}

}
