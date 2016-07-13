/**
 * 
 */
package io.akka.reactive.streams;

import java.util.Arrays;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;

/**
 * @author gurmi
 *
 */
public class BasicTransformation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		final ActorSystem system = ActorSystem.create("sys");
		final ActorMaterializer materializer = ActorMaterializer.create(system);
		final String text =
			      "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
			      "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
			      "when an unknown printer took a galley of type and scrambled it to make a type " +
			      "specimen book.";

		Source.from(Arrays.asList(text.split("\\s"))).filter(s->s.length()>3).map(e->e.toUpperCase()).runForeach(System.out::println, materializer).
			handle((done,failure)->{
				system.terminate();
				return NotUsed.getInstance();
			});

	}

}
