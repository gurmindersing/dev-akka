package io.akka.cluster.factorial;

import java.util.concurrent.Callable;




import scala.concurrent.Future;
import akka.actor.UntypedActor;
import akka.dispatch.Mapper;
import static akka.dispatch.Futures.future;
import static akka.pattern.Patterns.pipe;

public class FactorialBackend extends UntypedActor{

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof Integer){
			final Integer n = (Integer)message;
			Future<Integer> f = future(new Callable<Integer>(){
				public Integer call() throws Exception {
					// TODO Auto-generated method stub
					return factorial(n);
				}
				
			},getContext().dispatcher());
			
			Future<FactorialResult> result = f.map(new Mapper<Integer,FactorialResult>(){
				public FactorialResult apply(Integer factorial){
					return new FactorialResult(factorial);
				}
			},getContext().dispatcher());
			
			f.flatMap(new Mapper<Integer,Future<Future<FactorialResult>>>(){
				@Override
				public Future<Future<FactorialResult>> apply(final Integer factorial) {
					// TODO Auto-generated method stub
					Future<FactorialResult> f = future(new Callable<FactorialResult>(){

						public FactorialResult call() throws Exception {
							// TODO Auto-generated method stub
							return new FactorialResult(factorial);
						}
						
					},getContext().dispatcher());
					
					return f;
				}
			}, getContext().dispatcher());
			
			pipe(result,getContext().dispatcher()).to(getSender());
		}
		
		
		
	}
	
	Integer factorial(int n){
		Integer acc = new Integer(1);
		for(int i=1;i<=n;i++){
			acc=acc*n;
		}
		return acc;
	}

}
