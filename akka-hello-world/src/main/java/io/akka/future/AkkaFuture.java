/**
 * 
 */
package io.akka.future;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Filter;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.dispatch.OnSuccess;
import akka.japi.Function;
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
//		Promise<String> promise = Futures.promise();
//		Future<String> theFuture = promise.future();
//		theFuture.
		
//		system.shutdown();
		
	}
	
	public static void futureCall(ActorSystem system) throws InterruptedException{
		
		final ExecutionContext ec = system.dispatcher();
		Future<String> f1 = Futures.future(new Callable<String>(){

			public String call(){
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("call Thread : "+Thread.currentThread().getId());
				return "Hello"+"World";
			}
			
		}, ec);
		Thread.sleep(1000);
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
			public Future<Integer> apply(final String s){
				System.out.println("flat map Thread : "+Thread.currentThread().getId());
				return Futures.future(new Callable<Integer>(){

					public Integer call() {
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
		
		Future<Integer> future3 = Futures.successful(4);
		Future<Integer> successfulFilter  = future3.filter(Filter.filterOf(new Function<Integer,Boolean>(){

			public Boolean apply(Integer t) {
				// TODO Auto-generated method stub
				System.out.println(t);
				return t%2 == 0;
			}
			
		}), ec);
		
		Future<Integer> failedFilter  = future3.filter(Filter.filterOf(new Function<Integer,Boolean>(){

			public Boolean apply(Integer t) {
				// TODO Auto-generated method stub
				System.out.println(t);
				return t%2 == 0;
			}
			
		}), ec);
		System.out.println(failedFilter.isCompleted());
		Future<Integer> failedFilter2  = failedFilter.filter(Filter.filterOf(new Function<Integer,Boolean>(){

			public Boolean apply(Integer t) {
				// TODO Auto-generated method stub
				System.out.println(t);
				return t%2 == 0;
			}
			
		}), ec);
		
		Future<Integer> ff1 = Futures.successful(4);
		Future<Integer> ff2 = Futures.successful(5);
		Future<Integer> ff3 = Futures.successful(6);
		
		List<Future<Integer>> list = new ArrayList<Future<Integer>>();
//		Iterator<Future<Integer>> iterator = list.iterator();
		list.add(ff1);list.add(ff2);list.add(ff3);
		Iterable<Future<Integer>> iterable = list;
		Future<Iterable<Integer>> futureListOfInts = Futures.sequence(list, ec);
//		futureListOfInts.map(arg0, arg1)
		
		
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


