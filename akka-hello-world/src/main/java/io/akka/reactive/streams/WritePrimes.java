/**
 * 
 */
package io.akka.reactive.streams;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ThreadLocalRandom;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

/**
 * @author gurmi
 *
 */
public class WritePrimes {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		final ActorSystem system = ActorSystem.create("sys");
		final ActorMaterializer materializer = ActorMaterializer.create(system);
		
		final int maxRandomNumberSize = 1000000;
		final Source<Integer,NotUsed> source = Source.from(new RandomIterable(maxRandomNumberSize)).filter(WritePrimes::isPrime).filter(prime->isPrime(prime+2));
		
		Sink<ByteString, CompletionStage<IOResult>> output = FileIO.toFile(new File("primes.txt"));
		 
		Sink<Integer, CompletionStage<IOResult>> slowSink = Flow.of(Integer.class).map(i->{Thread.sleep(1000);return ByteString.fromString(i.toString());}).toMat(output,Keep.right());
		Sink<Integer, CompletionStage<Done>> consoleSink = Sink.<Integer>foreach(System.out::println);
		
		 

	}
	
	private static boolean isPrime(int n) {
	    if (n <= 1)
	      return false;
	    else if (n == 2)
	      return true;
	    else {
	      for (int i = 2; i < n; i++) {
	        if (n % i == 0)
	          return false;
	      }
	      return true;
	    }
	  }
	

}

class RandomIterable implements Iterable<Integer>{
	
	private final int maxRandomNumberSize;

	RandomIterable(int maxRandomNumberSize) {
		this.maxRandomNumberSize = maxRandomNumberSize;
  	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Integer> iterator() {
		// TODO Auto-generated method stub
		return new Iterator<Integer>(){

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public Integer next() {
				// TODO Auto-generated method stub
				return ThreadLocalRandom.current().nextInt(maxRandomNumberSize);
			}
			
		};
	}
	
}
