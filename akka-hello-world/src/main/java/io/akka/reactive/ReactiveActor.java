/**
 * 
 */
package io.akka.reactive;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.FlowShape;
import akka.stream.Fusing;
import akka.stream.Fusing.FusedGraph;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.Outlet;
import akka.stream.ThrottleMode;
import akka.stream.UniformFanInShape;
import akka.stream.UniformFanOutShape;
import akka.stream.javadsl.Broadcast;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Merge;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import scala.concurrent.duration.Duration;

/**
 * @author gurmi
 *
 */
public class ReactiveActor extends UntypedActor{
	
//	static LoggingAdapter log =  Logging.getLogger(getContext().system(), this);
	
	public static void main(String[] args) {
		final ActorSystem system = ActorSystem.create("QuickStart");
		final Materializer materializer = ActorMaterializer.create(system);
		final Source<Integer,NotUsed> source = Source.from(Arrays.asList(1,2,3,4,5,6));
		Source<Integer,NotUsed> source2 = source.map(x->2);
		final Sink<Integer,CompletionStage<Integer>> sink = Sink.<Integer,Integer>fold(0, (aggr,next)->aggr+next);
		CompletionStage<Integer> sum = source.runWith(sink, materializer);
		CompletionStage<Integer> sum2 = source2.runWith(sink, materializer);
		RunnableGraph<CompletionStage<Integer>> runnable = source.toMat(sink, Keep.right());
		
		Flow<Integer,Integer,NotUsed> flow = Flow.of(Integer.class).map(x->x*2).filter(x->x>500);
		FusedGraph<FlowShape<Integer, Integer>, NotUsed> fused = Fusing.aggressive(flow);
//		Source.fromIterator(()->Stream.iterate(0,x->x+1).iterator()).via(fused).take(1000);
		
		RunnableGraph<CompletionStage<Integer>> source3 = Source.range(1,3).map(x->x+1).async().map(x->x*2).toMat(sink,Keep.right());
		
		try {
			System.out.println(sum.toCompletableFuture().get());
			System.out.println(sum2.toCompletableFuture().get());
			source3.run(materializer);
			runnable.run(materializer);
			runnable.run(materializer);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		createGraph2();
		
		system.terminate();
	}
		

	/**
	 * @param args
	 */
	public static void main2(String[] args) {
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
		
		factorials.zipWith(Source.range(0, 99), (num,idx)->String.format("%d!=%s",idx, num))
			.throttle(1, Duration.create(1, TimeUnit.SECONDS),1, ThrottleMode.shaping())
			.runForeach(s->System.out.println(s), materializer);
		
		System.out.println("completed");
		
		

	}
	
	public static void createGraph2(){
		final Source<Integer, NotUsed> in = Source.from(Arrays.asList(1, 2, 3, 4, 5));
		final Sink<List<String>, CompletionStage<List<String>>> sink = Sink.head();
		final Flow<Integer, Integer, NotUsed> f1 = Flow.of(Integer.class).map(elem -> elem + 10);
		final Flow<Integer, Integer, NotUsed> f2 = Flow.of(Integer.class).map(elem -> elem + 20);
		final Flow<Integer, String, NotUsed> f3 = Flow.of(Integer.class).map(elem -> elem.toString());
		final Flow<Integer, Integer, NotUsed> f4 = Flow.of(Integer.class).map(elem -> elem + 30);
		 
		final RunnableGraph<CompletionStage<List<String>>> result =
		  RunnableGraph.fromGraph(
		    GraphDSL     // create() function binds sink, out which is sink's out port and builder DSL
		      .create(   // we need to reference out's shape in the builder DSL below (in to() function)
		        sink,                // previously created sink (Sink)
		        (builder, out) -> {  // variables: builder (GraphDSL.Builder) and out (SinkShape)
		          final UniformFanOutShape<Integer, Integer> bcast = builder.add(Broadcast.create(2));
		          final UniformFanInShape<Integer, Integer> merge = builder.add(Merge.create(2));
		 
		          final Outlet<Integer> source = builder.add(in).out();
		          builder.from(source).via(builder.add(f1))
		            .viaFanOut(bcast).via(builder.add(f2)).viaFanIn(merge)
		            .via(builder.add(f3.grouped(1000))).to(out);  // to() expects a SinkShape
		          builder.from(bcast).via(builder.add(f4)).toFanIn(merge);
		          return ClosedShape.getInstance();
		        }));
	}
	
	
	public void createGraph(){
		final Sink<Integer,CompletionStage<Integer>> topHeadSink = Sink.head();
		final Sink<Integer,CompletionStage<Integer>> bottomHeadSink = Sink.head();
		
		final Flow<Integer,Integer,NotUsed> sharedDoubler = Flow.of(Integer.class).map(elem->elem*2);
		
		final RunnableGraph<Pair<CompletionStage<Integer>,CompletionStage<Integer>>> g =
				RunnableGraph.fromGraph(GraphDSL.create(topHeadSink,bottomHeadSink,Keep.both(),
						(b,top,bottom)->{final UniformFanOutShape<Integer, Integer> bcast =
				          b.add(Broadcast.create(2));
						 
				        b.from(b.add(Source.single(1))).viaFanOut(bcast)
				          .via(b.add(sharedDoubler)).to(top);
				        b.from(bcast).via(b.add(sharedDoubler)).to(bottom);
				        return ClosedShape.getInstance();
				        }));
				
		
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
