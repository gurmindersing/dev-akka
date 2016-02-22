/**
 * 
 */
package io.akka.cluster.router;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import akka.actor.Address;
import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.ReachabilityEvent;
import akka.cluster.ClusterEvent.ReachableMember;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * @author gurmi
 *
 */
public class StatsSimpleOneClient extends UntypedActor{
	Cluster cluster = Cluster.get(getContext().system());
	private String servicePath;
	final Cancellable tickTask;
	final Set<Address> nodes = new HashSet<Address>();
	
	public StatsSimpleOneClient(String servicePath){
		this.servicePath = servicePath;
		FiniteDuration interval = Duration.create(2, TimeUnit.SECONDS);
		tickTask = getContext().system().scheduler().schedule(interval, interval, getSelf(), 
				"tick", getContext().dispatcher(),null);
		
	}
	
	public void preStart(){
		cluster.subscribe(getSelf(), MemberEvent.class,ReachabilityEvent.class);
	}
	
	public void postStop(){
		cluster.unsubscribe(getSelf());
		tickTask.cancel();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		
		if (message instanceof CurrentClusterState) {
		      CurrentClusterState state = (CurrentClusterState) message;
		      nodes.clear();
		      for (Member member : state.getMembers()) {
		        if (member.hasRole("compute") && member.status().equals(MemberStatus.up())) {
		          nodes.add(member.address());
		        }
		      }

		    } else if (message instanceof MemberUp) {
		      MemberUp mUp = (MemberUp) message;
		      if (mUp.member().hasRole("compute"))
		        nodes.add(mUp.member().address());

		    } else if (message instanceof MemberEvent) {
		      MemberEvent other = (MemberEvent) message;
		      nodes.remove(other.member().address());

		    } else if (message instanceof UnreachableMember) {
		      UnreachableMember unreachable = (UnreachableMember) message;
		      nodes.remove(unreachable.member().address());

		    } else if (message instanceof ReachableMember) {
		      ReachableMember reachable = (ReachableMember) message;
		      if (reachable.member().hasRole("compute"))
		        nodes.add(reachable.member().address());

		    } else {
		      unhandled(message);
		    }
		
	}

}
