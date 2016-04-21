/**
 * 
 */
package io.akka.reactive;

import java.io.File;
import java.math.BigInteger;
import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
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
public class ReactiveActor extends UntypedActor{
	
//	static LoggingAdapter log =  Logging.getLogger(getContext().system(), this);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final ActorSystem system = ActorSystem.create("QuickStart");
		final Materializer materializer = ActorMaterializer.create(system);
		
		final Source<Integer,NotUsed> source = Source.range(1, 100);
//		source.runForeach(i->System.out.println(i), materializer);
		System.out.println("system started");
		final Source<BigInteger,NotUsed> factorials = source.scan(BigInteger.ONE, (acc,next)-> acc.multiply(BigInteger.valueOf(next)));
		
		final CompletionStage<IOResult> result = factorials.map(num->ByteString.fromString(num.toString()+"\n"))
				.runWith(FileIO.toFile(new File("factorials.txt")), materializer);
		
		factorials.map(BigInteger::toString).runWith(linkSink("factorials.txt"), materializer);
		
		System.out.println("completed");

	}
	
	public static Sink<String,CompletionStage<IOResult>> linkSink(String filename){
		
		return Flow.of(String.class).map(s->ByteString.fromString(s.toString()+"\n")).
				toMat(FileIO.toFile(new File(filename)), Keep.right());
		
	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	

}
