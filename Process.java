public class Process{ 
	//instance variables
	int pid;
	int burst_time;
	int arrival_time;
	int priority;
	int finishTime;
	int responceTime;
	int runTime;

	/**Constructor for Process*/
	public Process(int pid, int burst_time, int arrival_time, int priority){
		this.pid = pid;
		this.burst_time = burst_time;
		runTime = burst_time; //burst_time will decrement as the program runs.  Need a variable to keep track of the original total run time
		this.arrival_time = arrival_time;
		this.priority = priority;
		responceTime = -1; //flag to show that it has not yet been set
	}


}