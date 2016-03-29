import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

class Scheduler{
	public static void main(String args[]){
		ArrayList<Process> waitingQ = new ArrayList<Process>();
		ArrayList<Process> readyQ = new ArrayList<Process>();
		int time = 0;

		//determine what file to read from
		File input = new File(args[1]);
		//read from file
		readFromFile(input, waitingQ);
		//detemine what algorithm to use
		String algorithm = args[0];
		switch(algorithm){
			case "fcfs":
				fcfs(time, readyQ, waitingQ);
				break;
			case "sjf":
				sjf(time, readyQ, waitingQ);
				break;
			case "srtf":
				srtf(time, readyQ, waitingQ);
				break;
			case "nonpreprior":
				nonpreprior(time, readyQ, waitingQ);
				break;
			case "preprior":
				preprior(time, readyQ, waitingQ);
				break;
			case "rr":
				rr(time, readyQ, waitingQ);
				break;
			case "custom":
				custom(time, readyQ, waitingQ);
				break;
			default:
				System.out.println("Invalid algorithm type");
		}
		


	}

	/**method to read all processes from the input file and add them to the waitingQ*/
	static void readFromFile(File f, ArrayList<Process> waitingQ){
		int pid, burst_time, arrival_time, priority;
		try{
			Scanner read = new Scanner(f);
			read.useDelimiter(",|\\n");

			while(read.hasNext()){
				pid = read.nextInt();
				burst_time = read.nextInt();
				arrival_time = read.nextInt();
				priority = read.nextInt();
				Process p = new Process(pid, burst_time, arrival_time, priority);
				waitingQ.add(p);
			}

			read.close();
		}
		catch(FileNotFoundException e){
			System.out.println("File not found");
		}
	}

	void fcfs(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ){
		Process running[] = new Process(1); 
		int numProcesses = waitingQ.size;
		int totalWaitTime = 0;
		Process[] running = new Process[1]; 
		//main execution loop for fcfs
		while(waitingQ.size() != 0 && readyQ.size() != 0 && running[0]!=null){ //loop until there is nothing in waiting, ready, or running
			//for all elements of waitingQ check if arrivial time matches current time if so add to readyQ
			for(int i=0; i<numProcesses; i++){
				if(waitingQ(i).arrival_time == time){
					readyQ.add(waitingQ(i));
				}
			}
			//check if running is empty
			if(running[0]==null){
				//if so add the first element of readyQ to running;
				running[0] = readyQ.get(0);
				readyQ.remove(0);//remove that element from readyQ
			}
			//else check if runningQ[0].burst_time == 0
			else if(running[0].burst_time == 0){
				//in this case whatever was running just finished
				//if there is something left to run
				if(readyQ.get(0)!=null){
					running[0]= readyQ.get(0);
				}
				else{ //there is nothing more to run right now
					running[0]=null;
				}
			}
			else{ //something is still running
				running[0].burst_time--; //take one more second off time remaining
			}
			if(running[0]!=null){
				System.out.println("Currently running process "+ running[0].pid +".  Time: "+time);
			}
			time++;
			totalWaitTime = totalWaitTime + readyQ.size(); //gain one sec for each process waiting per second
		}
		double avgWait = totalWaitTime/numProcesses;
		//need stuff for calculating weighted wait time (responce time will be the same as wait time)  Print them here
	}

	void sjf(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ){

	}

	void srtf(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ){

	}

	void nonpreprior(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ){

	}

	void preprior(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ){

	}

	void rr(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ){

	}

	void custom(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ){

	}
}