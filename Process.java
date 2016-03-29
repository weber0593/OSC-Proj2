public class Process{ 
	//instance variables
	private int pid;
	private int burst_time;
	private int arrival_time;
	private int priority;

	/**Constructor for Process*/
	public Process(int pid, int burst_time, int arrival_time, int priority){
		this.pid = pid;
		this.burst_time = burst_time;
		this.arrival_time = arrival_time;
		this.priority = priority;
	}


}