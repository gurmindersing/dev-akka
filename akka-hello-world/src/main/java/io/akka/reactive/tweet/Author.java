/**
 * 
 */
package io.akka.reactive.tweet;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.FlowShape;
import akka.stream.Materializer;
import akka.stream.SinkShape;
import akka.stream.UniformFanOutShape;
import akka.stream.javadsl.Broadcast;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Source;
import akka.stream.scaladsl.Sink;
import scala.Function1;
import scala.runtime.BoxedUnit;

/**
 * @author gurmi
 *
 */
public class Author {
	public static final Hashtag AKKA = new Hashtag("#akka");
	
	public final String handle;
	
	public Author(String handle){
		this.handle=handle;
	}
	
	public static void main(String[] args) {
		
		final Hashtag AKKA = new Hashtag("#akka");
		
		final ActorSystem system = ActorSystem.create("reactive-tweets");
		final Materializer mat = ActorMaterializer.create(system);
		
		Source<Tweet,NotUsed> tweets=Source.from(Arrays.asList(new Tweet(new Author("akka"),new Date().getTime(), "hello #akka world")
				,new Tweet(new Author("akka2"),new Date().getTime(), "hello world2 #akka")));
//		tweets.
		
		
		Source<Author,NotUsed> authors = tweets.filter(t->{
			System.out.println("t.hashtags().contains(AKKA):"+t.hashtags());return t.hashtags().contains(AKKA);}).map(t->{System.out.println("author : "+t.author);
			return t.author;});
		authors.runForeach(a->System.out.println(a), mat);
		
		readNumbers(mat);
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void readNumbers(Materializer mat){
		Source<Integer, NotUsed> source = Source.from(Arrays.asList(1,2,3,4,5,6,7,8,9,10))
				.via(Flow.of(Integer.class).map(elem->elem*2));
		
		akka.stream.javadsl.Sink<Integer, CompletionStage<Integer>> sink = akka.stream.javadsl.Sink.<Integer,Integer>fold(0, (aggr,next)->aggr+next);
		
		RunnableGraph<CompletionStage<Integer>> runnable = source.toMat(sink, Keep.right());
		source.runWith(sink, mat).thenAccept(x->System.out.println(x));;
		CompletionStage<Integer> result = runnable.run(mat);
		result.thenAccept(x->System.out.println(x));	
		
		Source.maybe();
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void graph(Source<Tweet,NotUsed> tweets,Materializer materializer){
		Sink<Author,NotUsed> writeAuthors=null;;
		Sink<Hashtag,NotUsed> writeHashtags=null;
		
		RunnableGraph.fromGraph(GraphDSL.create(b->{
			final UniformFanOutShape<Tweet,Tweet> bcast = b.add(Broadcast.create(2));
			FlowShape<Tweet,Hashtag> toTags = 
					b.add(Flow.of(Tweet.class).mapConcat(t->new ArrayList<Hashtag>(t.hashtags())));
			FlowShape<Tweet,Author> toAuthor =
					b.add(Flow.of(Tweet.class).map(t->t.author));
			
			final SinkShape<Author> authors = b.add(writeAuthors);
			final SinkShape<Hashtag> hashtags = b.add(writeHashtags);
			
			b.from(b.add(tweets)).viaFanOut(bcast).via(toAuthor).to(authors);
			b.from(bcast).via(toTags).to(hashtags);
			
			return ClosedShape.getInstance();
		})).run(materializer);
	}
	
}

class Hashtag{
	public final String name;
	
	public Hashtag(String name){
		this.name=name;
	}
}
class Tweet{
	public final Author author;
	public final long timestamp;
	public final String body;
	
	Tweet(Author author,long timestamp, String body){
		this.author=author;
		this.timestamp=timestamp;
		this.body=body;
	}
	
	public Set<Hashtag> hashtags(){
		Set<Hashtag> set = Arrays.asList(body.split(" ")).stream().filter(a->{System.out.println(a); return a.startsWith("#");})
				.map(a->{System.out.println(a);return new Hashtag(a);})
				.collect(Collectors.toSet());
				
		System.out.println("set : "+set.size());
		/*Stream.of("a1", "a2", "a3")
	    .map(s -> s.substring(1))
	    .mapToInt(Integer::parseInt)
	    .max()
	    .ifPresent(System.out::println);  // 3
*/		return set;
		
	}
	
	
}


