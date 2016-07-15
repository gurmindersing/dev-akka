package io.akka.cluster.factorial;

public class FactorialResult {
	
	Integer result;

	public FactorialResult(Integer factorial) {
		// TODO Auto-generated constructor stub
		this.result=factorial;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

}
