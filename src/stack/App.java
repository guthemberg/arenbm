/**
 * 
 */
package stack;

import java.util.ArrayList;
import java.util.List;

import peersim.core.Node;
import peersim.core.Protocol;
import stat.Generator;

/**
 * @author guthemberg
 *
 */
public class App implements Protocol {
	Node[] servers=null;
	int transport;
	static public int msg_id_gen=0;
	long bwd;
	long ts;
	long computed_messages;
	long downloaded;
	long tx;
//	long duration;
	/**
	 * 
	 */
	public App(String prefix) {
		this.computed_messages=0L;
	}
	
	public Node[] getSources(int size){
		Node[] sources=new Node[size];
		//create all options vector
		List<Integer> options = new ArrayList<Integer>();
		for (int i = 0; i < this.servers.length; i++) {
			options.add(new Integer(i));
		}
		int i=0;
		int index=0;
		//selections
		do {
			index = Generator.getRandomIndex(options.size());
			sources[i]=this.servers[index];
			options.remove(index);
			i++;
		} while (i<size);
		return sources;
	}
    public Object clone() {
        App local_storage = null;
        try {
        	local_storage = (App) super.clone();
        } catch (CloneNotSupportedException e) {
        	System.err.println("Unexpected error in:"+this.toString());
        } 
    	return local_storage;
    }
    
    public int[] getChunksDistribution(int n_sources, int n_chunks){
    	int partition = n_chunks/n_sources;
    	int[] distribution = new int[n_sources];
    	int remaining_chunks=n_chunks;
    	for (int i = 0; i < distribution.length; i++) {
    		if(i==(distribution.length-1))
    			distribution[i]=remaining_chunks;
    		else
    			distribution[i]=partition;
    		remaining_chunks-=partition;
		}
    	return distribution;
    }
    
    public void fetchData(Node dst){
    	//get sources
    	Node[] sources = this.getSources(1);
    	//int n_chunks = this.getRandomIndex(this.max_chunks-this.min_chunks)+this.min_chunks;
    	long length = Generator.nextLong();
    	//random delay for this fetch, this is such a scheduled task
    	Node src=null;
    	Transport src_net = null;
    	Message msg=null;
    	for (int i = 0; i < sources.length; i++) {
			src=sources[i];
			src_net = (Transport)(src).getProtocol(this.transport);
			//for (int j = 0; j < nc; j++) {
				msg=new Message(src,dst, App.msg_id_gen, 
						length);
				src_net.send(src, dst, this.transport, msg);
				//msg_index++;
			//}
		}
		App.msg_id_gen++; 
    }
    
    public void receiveData(Message msg){
		this.computed_messages++;
		this.downloaded+=msg.getLength();
//		this.duration+=(CommonState.getTime()-msg.getStart());
		this.fetchData(msg.getDst());
    }
    
    public void bootstrap(
    		long bwd, long ts,int transport,
    		Node[] list){
		this.bwd=bwd;
		this.ts=ts;
		this.transport=transport;
    	this.servers=list;
		this.computed_messages=0L;
		this.downloaded=0L;
		this.tx=0L;
//		this.duration=0L;
    }
    	
	public long getComputedMessages(){
		return this.computed_messages;
	}
	public void resetComputedMessages(){
		this.computed_messages=0L;
	}
	public long getDownloaded(){
		return this.downloaded;
	}
	public void resetDownloaded(){
		this.downloaded=0L;
	}
	public void addTx(long tx){
		this.tx+=tx;
	}
	public long getTx(){
		return this.tx;
	}
//	public long getDuration(){
//		return this.duration;
//	}
//	public void resetDuration(){
//		this.duration=0L;
//	}
	//public int getMsgSize(){
	//	return this.messages.;
	//}
}
