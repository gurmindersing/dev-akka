/**
 * 
 */
package io.akka.spring;

import javax.inject.Named;

/**
 * @author Gurminder
 *
 */
@Named("countingService")
public class CountingService {
	
	public int increment(int count){
		return count + 1;
	}

}
