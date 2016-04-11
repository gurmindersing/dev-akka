/**
 * 
 */
package io.akka.cluster.remote.calculator;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.ReceiveTimeout;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.japi.Procedure;
import scala.concurrent.duration.Duration;

/**
 * @author gurmi
 *
 */
public class LookupActor extends UntypedActor{
	
	private final String path;
	private ActorRef calculator = null;
	
	public LookupActor(String path){
		this.path=path;
		sendIndentifyRequest();
		
	}
	
	private void sendIndentifyRequest(){
		getContext().actorSelection(path).tell(new Identify(path), getSelf());
		getContext().system().scheduler().scheduleOnce(Duration.create(3, TimeUnit.SECONDS), getSelf(),ReceiveTimeout.getInstance(),
				getContext().dispatcher(),getSelf());
	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof ActorIdentity){
			calculator = ((ActorIdentity) message).getRef();
			System.out.println("calculator found : "+calculator);
			if(calculator==null){
				System.out.println("remote actor not available : "+path);
			}else{
				getContext().watch(calculator);
				getContext().become(active);
			}
			
		}else{
			System.out.println("Not yet ready!");
		}
		
	}
	
	Procedure<Object> active = new Procedure<Object>(){
		public void apply(Object message){
			if(message instanceof Op.MathOp){
				System.out.println("to calc");
				calculator.tell(message, getSelf());
			}else if (message instanceof Op.AddResult) {
		        Op.AddResult result = (Op.AddResult) message;
		        System.out.printf("Add result: %d + %d = %d\n", result.getN1(),
		            result.getN2(), result.getResult());

		      } else if (message instanceof Op.SubtractResult) {
		        Op.SubtractResult result = (Op.SubtractResult) message;
		        System.out.printf("Sub result: %d - %d = %d\n", result.getN1(),
		            result.getN2(), result.getResult());

		      } else if(message instanceof Terminated){
		    	  sendIndentifyRequest();
		    	  getContext().unbecome();
		      }else{
		    	  unhandled(message);
		      }
		}
	};

}
