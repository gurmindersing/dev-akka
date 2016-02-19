package io.akka.cluster.sample;

public class TransformationApp {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		TransformationBackendMain.main(new String[]{"2551"});
		TransformationBackendMain.main(new String[]{"2552"});
		TransformationBackendMain.main(new String[]{"6000"});
	    TransformationFrontendMain.main(new String[]{"6001"});

	}

}
