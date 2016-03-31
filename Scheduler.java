import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

class Scheduler{
	public static void main(String args[]){
		ArrayList<Process> waitingQ = new ArrayList<Process>(); //Processes that havent arrived
		ArrayList<Process> readyQ = new ArrayList<Process>(); //Processes that are ready to run
		Process[] running = new Process[1]; //Process that is currently running
		ArrayList<Process> doneQ = new ArrayList<Process>(); //need this to do analytics

		int time = 0;
		int numProcesses = waitingQ.size();

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

	static void calcAnalytics(ArrayList<Process> doneQ){
		int totalWaitTime=0;
		int totalResponceTime =0;
		int totalWeightedWaitTime =0;
		int totalWeightedResponceTime =0;
		int totalPriority =0;
		
		for(int i=0; i<doneQ.size(); i++){
			int waitTime = doneQ.get(i).finishTime - doneQ.get(i).arrival_time - doneQ.get(i).runTime;  //time finish - time start = runtime + waittime
			totalWaitTime = totalWaitTime + waitTime;
			totalWeightedWaitTime = totalWeightedWaitTime + (waitTime * doneQ.get(i).priority);
			totalResponceTime = totalResponceTime + doneQ.get(i).responceTime;
			totalWeightedResponceTime = totalWeightedResponceTime + (doneQ.get(i).responceTime * doneQ.get(i).priority);
			totalPriority = totalPriority + doneQ.get(i).priority;
		}

		double avgWaitTime = totalWaitTime/doneQ.size();
		double avgResponceTime = totalResponceTime/doneQ.size();
		double avgWeightedWaitTime = totalWeightedWaitTime/totalPriority;
		double avgWeightedResponceTime = totalWeightedResponceTime/totalPriority;

		System.out.println("The average waiting time is: " + avgWaitTime);
		System.out.println("The average weighted waiting time is: " + avgWeightedWaitTime);
		System.out.println("The average responce time is: " + avgResponceTime);
		System.out.println("The average weighted responce time is: " + avgWeightedResponceTime);
	}

	void fcfs(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ){
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
				if(running[0].responceTime == -1) //if responce time hasnt been set yet (first time running)
					running[0].responceTime = time;
				readyQ.remove(0);//remove that element from readyQ
			}

			//else check if process in runnning is done
			else if(running[0].burst_time == 0){
				//in this case whatever was running just finished
				running[0].finishTime = time; //set the finish time of the process to now
				//if there is something left to run
				if(readyQ.get(0)!=null){
					doneQ.add(running[0]); //process finished, add to doneQ
					running[0]= readyQ.get(0);
				}
				//there is nothing more to run right now
				else{ 
					doneQ.add(running[0]); //process finished, add to doneQ
					running[0]=null;
				}
			}

			//if something is still running
			else{ 
				running[0].burst_time--; //take one more second off time remaining
			}

			if(running[0]!=null){
				System.out.println("Currently running process: "+ running[0].pid +".  Time: "+time);
			}

			time++; //increment time
			
		}
		calcAnalytics(doneQ);
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