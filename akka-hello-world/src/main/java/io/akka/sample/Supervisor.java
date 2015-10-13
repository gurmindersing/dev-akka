/**
 * 
 */
package io.akka.sample;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;
import static akka.actor.SupervisorStrategy.stop;
import scala.concurrent.duration.Duration;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.japi.Function;

/**
 * @author Gurminder
 *
 */
public class Supervisor extends UntypedActor {
//    new OneForOneStrategy());
	private static SupervisorStrategy strategy = new OneForOneStrategy(3,
		      Duration.create("5 seconds"), new Function<Throwable, Directive>() {
		      @Override
		      public Directive apply(Throwable t) {
		    	  System.out.println(t);
		    	  if (t instanceof ArithmeticException) {
		    		  System.out.println("resume");
		              return resume();
		            } else if (t instanceof NullPointerException) {
		            	System.out.println("restart");
		              return restart();
		            } else if (t instanceof IllegalArgumentException) {
		            	System.out.println("stop");
		              return stop();
		            } else {
		            	System.out.println("escalate()");
		              return escalate();
		            }
		      }
		    });
    @Override
    public SupervisorStrategy supervisorStrategy() {
      return strategy;
    }
   
   
    public void onReceive(Object o) {
      if (o instanceof Props) {
        getSender().tell(getContext().actorOf((Props) o), getSelf());
      } else {
        unhandled(o);
      }
    }
  }


