package io.akka.cluster.sample;

import java.io.Serializable;

public interface TransformationMessages {
	
	public static class TransformationJob implements Serializable{
		public String text;
		
		public TransformationJob(String text){
			this.text=text;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
		
		
	}
	
	public static class TransformationResult implements Serializable{
		
		public String result;
		
		public TransformationResult(String result){
			this.result=result;
		}

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}
		
		@Override
	    public String toString() {
	      return "TransformationResult(" + result + ")";
	    }
	}
	
	public static class JobFailed implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final String reason;
		private final TransformationJob job;
		
		public JobFailed(String reason, TransformationJob job) {
		      this.reason = reason;
		      this.job = job;
		}
		
		public String getReason() {
			return reason;
		}
		public TransformationJob getJob() {
			return job;
		}
		
		@Override
	    public String toString() {
	      return "JobFailed(" + reason + ")";
	    }
		
		
	}
	
	 public static final String BACKEND_REGISTRATION = "BackendRegistration";
	
	

}
