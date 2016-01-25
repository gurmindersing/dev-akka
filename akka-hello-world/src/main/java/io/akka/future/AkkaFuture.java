/**
 * 
 */
package io.akka.future;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.pattern.PipeToSupport.PipeableFuture;
import akka.util.Timeout;
import io.akka.sample.AkkaActor;
import scala.concurrent.Await;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.Promise;
import scala.concurrent.duration.Duration;

/**
 * @author gurmi
 *
 */
public class AkkaFuture {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		ActorSystem system = ActorSystem.create("user");
		ActorRef actor = system.actorOf(Props.create(AkkaActor.class), "demo");
		
		Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
		Future<Object> future = Patterns.ask(actor,"Hello Future, Delighted to meet you!!!",timeout);
		try {
			String result = (String)Await.result(future, Duration.create(5, TimeUnit.SECONDS));
			System.out.println("future result : "+result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PipeableFuture<Object> pipeableFuture = Patterns.pipe(future, system.dispatcher()).to(actor);
//		pipeableFuture.
		futureCall(system);
//		system.shutdown();
		
	}
	
	public static void futureCall(ActorSystem system) throws InterruptedException{
		
		final ExecutionContext ec = system.dispatcher();
		Future<String> f1 = Futures.future(new Callable<String>(){

			@Override
			public String call() throws Exception {
				// TODO Auto-generated method stub
				Thread.sleep(100);
				System.out.println("call Thread : "+Thread.currentThread().getId());
				return "Hello"+"World";
			}
			
		}, ec);
//		Thread.sleep(100);
		f1.onSuccess(new PrintResult<String>(), ec);
		System.out.println("main Thread : "+Thread.currentThread().getId());
		/*Future<Integer> f2 = f1.map(new Mapper<String,Integer>(){
//			System.out.println("In map method");
			public Integer apply(String s){
				System.out.println("map Thread : "+Thread.currentThread().getId());
				return s.length();
			}
		}, ec);*/
		
		Future<Integer> f2 = f1.flatMap(new Mapper<String,Future<Integer>>(){
//			System.out.println("In map method");
			public Future<Integer> apply(String s){
				System.out.println("map Thread : "+Thread.currentThread().getId());
				return Futures.future(new Callable<Integer>(){

					@Override
					public Integer call() throws Exception {
						// TODO Auto-generated method stub
						return s.length();
					}
					
				},ec);
			}
		}, ec);
		
		f2.onSuccess(new PrintResult<Integer>(), ec);
		Promise<String> promise = Futures.promise();
		Future<String> f3 = promise.future();
//		f3.
		
	}
	
	public final static  class PrintResult<T> extends OnSuccess<T>{

		/* (non-Javadoc)
		 * @see akka.dispatch.OnSuccess#onSuccess(java.lang.Object)
		 */
		@Override
		public void onSuccess(T result) throws Throwable {
			// TODO Auto-generated method stub
			System.out.println(result);
			
		}
		
	}

}


