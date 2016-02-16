package io.akka.future;

import java.util.concurrent.Callable;

public class RandomPause implements Callable<Long>{
	
	private Long millisPause;
	
	public RandomPause(){
		millisPause = Math.round(Math.random()*3000) + 1000;
		System.out.println(this.toString()+ " will pause for "+ millisPause);
	}

	public Long call() throws Exception {
		// TODO Auto-generated method stub
		Thread.sleep(millisPause);
		System.out.println(this.toString()+" was paused for "+millisPause);
		return millisPause;
	}

}
