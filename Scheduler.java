import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

class Scheduler{
	public static void main(String args[]){
		ArrayList<Process> waitingQ = new ArrayList<Process>(); //Processes that havent arrived
		ArrayList<Process> readyQ = new ArrayList<Process>(); //Processes that are ready to run
		Process running = null; //Process that is currently running
		ArrayList<Process> doneQ = new ArrayList<Process>(); //need this to do analytics

		int time = 0;

		//determine what file to read from
		File input = new File(args[1]);
		//read from file
		readFromFile(input, waitingQ);
		//detemine what algorithm to use
		String algorithm = args[0];
		switch(algorithm){
			case "fcfs":
				fcfs(time, readyQ, waitingQ, doneQ, running);
				break;
			case "sjf":
				sjf(time, readyQ, waitingQ, doneQ, running);
				break;
			case "srtf":
				srtf(time, readyQ, waitingQ, doneQ, running);
				break;
			case "nonpreprior":
				nonpreprior(time, readyQ, waitingQ, doneQ, running);
				break;
			case "preprior":
				preprior(time, readyQ, waitingQ, doneQ, running);
				break;
			case "rr":
				rr(time, readyQ, waitingQ, doneQ, running);
				break;
			case "custom":
				custom(time, readyQ, waitingQ, doneQ, running);
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
		double totalWaitTime=0;
		double totalresponseTime =0;
		double totalWeightedWaitTime =0;
		double totalWeightedresponseTime =0;
		double totalPriority =0;
		
		for(int i=0; i<doneQ.size(); i++){
			double waitTime = doneQ.get(i).finishTime - doneQ.get(i).arrival_time - doneQ.get(i).runTime;  //time finish - time start = runtime + waittime
			totalWaitTime = totalWaitTime + waitTime;
			totalWeightedWaitTime = totalWeightedWaitTime + (waitTime * doneQ.get(i).priority);
			totalresponseTime = totalresponseTime + doneQ.get(i).responseTime - doneQ.get(i).arrival_time;
			totalWeightedresponseTime = totalWeightedresponseTime + ((doneQ.get(i).responseTime - doneQ.get(i).arrival_time)* doneQ.get(i).priority);
			totalPriority = totalPriority + doneQ.get(i).priority;
		}

		double avgWaitTime = totalWaitTime/doneQ.size();
		double avgresponseTime = totalresponseTime/doneQ.size();
		double avgWeightedWaitTime = totalWeightedWaitTime/totalPriority;
		double avgWeightedresponseTime = totalWeightedresponseTime/totalPriority;

		System.out.println("The average waiting time is: " + avgWaitTime);
		System.out.println("The average weighted waiting time is: " + avgWeightedWaitTime);
		System.out.println("The average response time is: " + avgresponseTime);
		System.out.println("The average weighted response time is: " + avgWeightedresponseTime);
	}

	static void fcfs(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ, ArrayList<Process> doneQ, Process running){
		//main execution loop for fcfs
		
		while(waitingQ.size() != 0 || readyQ.size() != 0 || running!=null){ //loop until there is nothing in waiting, ready, or running
			//for all elements of waitingQ check if arrivial time matches current time if so add to readyQ
			int i=0;
			while(i<waitingQ.size()){
				if(waitingQ.get(i).arrival_time == time){
					readyQ.add(waitingQ.get(i));
					waitingQ.remove(i);
					
				}
				else{
					i++;
				}
			}

			//check if running is empty
			if(running==null){
				//if so add the first element of readyQ to running;
				running = readyQ.get(0);
				if(running.responseTime == -1) //if response time hasnt been set yet (first time running)
					running.responseTime = time;
				readyQ.remove(0);//remove that element from readyQ
			}

			//else check if process in runnning is done
			else if(running.burst_time == 0){
				//in this case whatever was running just finished
				running.finishTime = time; //set the finish time of the process to now
				//if there is something left to run
				if(!readyQ.isEmpty()){
					doneQ.add(running); //process finished, add to doneQ
					running= readyQ.get(0);
					if(running.responseTime == -1) //if response time hasnt been set yet (first time running)
						running.responseTime = time;
					readyQ.remove(0);//remove that element from readyQ
				}
				//there is nothing more to run right now
				else{ 
					doneQ.add(running); //process finished, add to doneQ
					running=null;
				}
			}

			//if something is still running
			if(running!=null){ 
				running.burst_time--; //take one more second off time remaining
				System.out.println("Currently running process: "+ running.pid +".  Time: "+time);
			}

			time++; //increment time
			
		}
		calcAnalytics(doneQ);
	}

	static void sjf(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ, ArrayList<Process> doneQ, Process running){
		//main execution loop for sjf
		while(waitingQ.size() != 0 || readyQ.size() != 0 || running!=null){ //loop until there is nothing in waiting, ready, or running
			
			//for all elements of waitingQ check if arrivial time matches current time if so add to readyQ
			int i=0;
			while(i<waitingQ.size()){
				if(waitingQ.get(i).arrival_time == time){
					readyQ.add(waitingQ.get(i));
					waitingQ.remove(i);
					
				}
				else{
					i++;
				}
			}

			//if nothing in running, add shortest job in ready
			if(running==null){
				
				//get job with shortest time
				int shortestTime = readyQ.get(0).burst_time;
				int index = 0;
				for(int j =0; j<readyQ.size(); j++){
					if(readyQ.get(j).burst_time < shortestTime){
						shortestTime = readyQ.get(j).burst_time;
						index = j;
					}
				}
				running=readyQ.get(index);
				if(running.responseTime == -1) //if response time hasnt been set yet (first time running)
					running.responseTime = time;
				readyQ.remove(index); //remove that element from readyQ
			}

			//else check if process in runnning is done
			else if(running.burst_time == 0){
				//in this case whatever was running just finished
				running.finishTime = time; //set the finish time of the process to now
				//if there is something left to run
				if(!readyQ.isEmpty()){
					doneQ.add(running); //process finished, add to doneQ
					
					//add shortest job
					int shortestTime = readyQ.get(0).burst_time;
					int index = 0;
					for(int j =0; j<readyQ.size(); j++){
						if(readyQ.get(j).burst_time < shortestTime){
							shortestTime = readyQ.get(j).burst_time;
							index = j;
						}
					}
					running=readyQ.get(index);
					if(running.responseTime == -1) //if response time hasnt been set yet (first time running)
						running.responseTime = time;
					readyQ.remove(index);//remove that element from readyQ
				}

				//there is nothing more to run right now
				else{ 
					doneQ.add(running); //process finished, add to doneQ
					running=null;
				}
			}

			//if something is running
			if(running!=null){
				running.burst_time--;
				//print status on whats running
				System.out.println("Currently running process: "+ running.pid +".  Time: "+time);
			}

			time++;
		}

		calcAnalytics(doneQ);
	}

	static void srtf(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ, ArrayList<Process> doneQ, Process running){
		//main execution loop for srtf
		while(waitingQ.size() != 0 || readyQ.size() != 0 || running!=null){ //loop until there is nothing in waiting, ready, or running
			boolean flag = false;
			//for all elements of waitingQ check if arrivial time matches current time if so add to readyQ
			int j=0;
			while(j<waitingQ.size()){
				if(waitingQ.get(j).arrival_time == time){
					readyQ.add(waitingQ.get(j));
					if(running != null){ //if there is a new job with shorter time remaining than what is in running
						if(waitingQ.get(j).burst_time < running.burst_time){
							flag = true; //trigger replacement flag
						}
					}
					waitingQ.remove(j);
				}
				else{
					j++;
				}
			}

			//if nothing in running, add shortest job in ready
			if(running==null){
				
				//get job with shortest time
				int shortestTime = readyQ.get(0).burst_time;
				int index = 0;
				for(int i =0; i<readyQ.size(); i++){
					if(readyQ.get(i).burst_time < shortestTime){
						shortestTime = readyQ.get(i).burst_time;
						index = i;
					}
				}
				running=readyQ.get(index);
				if(running.responseTime == -1) //if response time hasnt been set yet (first time running)
					running.responseTime = time;
				readyQ.remove(index); //remove that element from readyQ
			}

			//if flag is true need to replace what's in running with new, shorter job
			else if(flag){
				int shortestTime = running.burst_time;
				int index =0;
				//get job with shortest time
				for(int i=0; i<readyQ.size(); i++){
					if(readyQ.get(i).burst_time < shortestTime){
						shortestTime = readyQ.get(i).burst_time;
						index =i;
					}
				}
				readyQ.add(running); //put running back into readyQ
				running = readyQ.get(index); //add new shortest job to running
				if(running.responseTime == -1) //if response time hasnt been set yet (first time running)
					running.responseTime = time;
				readyQ.remove(index); //remove that element from readyQ
			}

			//else check if process in runnning is done
			else if(running.burst_time == 0){
				//in this case whatever was running just finished
				running.finishTime = time; //set the finish time of the process to now
				//if there is something left to run
				if(!readyQ.isEmpty()){
					doneQ.add(running); //process finished, add to doneQ
					
					//add shortest job
					int shortestTime = readyQ.get(0).burst_time;
					int index = 0;
					for(int i =0; i<readyQ.size(); i++){
						if(readyQ.get(i).burst_time < shortestTime){
							shortestTime = readyQ.get(i).burst_time;
							index = i;
						}
					}
					running=readyQ.get(index);
					if(running.responseTime == -1) //if response time hasnt been set yet (first time running)
						running.responseTime = time;
					readyQ.remove(index);//remove that element from readyQ
				}

				//there is nothing more to run right now
				else{ 
					doneQ.add(running); //process finished, add to doneQ
					running=null;
				}
			}

			//if something is running
			if(running!=null){
				running.burst_time--;
				//print status on whats running
				System.out.println("Currently running process: "+ running.pid +".  Time: "+time);
			}

			time++;
		}

		calcAnalytics(doneQ);
	}

	static void nonpreprior(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ, ArrayList<Process> doneQ, Process running){

	}

	static void preprior(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ, ArrayList<Process> doneQ, Process running){

	}

	static void rr(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ, ArrayList<Process> doneQ, Process running){
		//main execution loop for fcfs
		int quanta = 2;
		int runStartTime =0;
		
		while(waitingQ.size() != 0 || readyQ.size() != 0 || running!=null){ //loop until there is nothing in waiting, ready, or running
			
			//for all elements of waitingQ check if arrivial time matches current time if so add to readyQ
			int i=0;
			while(i<waitingQ.size()){
				if(waitingQ.get(i).arrival_time == time){
					readyQ.add(waitingQ.get(i));
					waitingQ.remove(i);
					
				}
				else{
					i++;
				}
			}

			//check if running is empty
			if(running==null){
				//if so add the first element of readyQ to running;
				running = readyQ.get(0);
				runStartTime =time;
				if(running.responseTime == -1) //if response time hasnt been set yet (first time running)
					running.responseTime = time;
				readyQ.remove(0);//remove that element from readyQ
			}

			//else check if process is done
			else if(running.burst_time == 0){
				//in this case whatever was running just finished
				running.finishTime = time; //set the finish time of the process to now
				//if there is something left to run
				if(!readyQ.isEmpty()){
					doneQ.add(running); //process finished, add to doneQ
					running= readyQ.get(0);
					runStartTime =time;
					if(running.responseTime == -1) //if response time hasnt been set yet (first time running)
						running.responseTime = time;
					readyQ.remove(0);//remove that element from readyQ
				}
				//there is nothing more to run right now
				else{ 
					doneQ.add(running); //process finished, add to doneQ
					running=null;
				}
			}

			//if quanta is over
			else if((time - runStartTime)%quanta==0){
				readyQ.add(running);
				running = readyQ.get(0); //swap with next member in readyQ;
				runStartTime =time;
				if(running.responseTime==-1)
					running.responseTime=time;
				readyQ.remove(0);
			}

			//if something is still running
			if(running!=null){ 
				running.burst_time--; //take one more second off time remaining
				System.out.println("Currently running process: "+ running.pid +".  Time: "+time);
			}

			time++; //increment time
			
		}
		calcAnalytics(doneQ);

	}

	static void custom(int time, ArrayList<Process> readyQ, ArrayList<Process> waitingQ, ArrayList<Process> doneQ, Process running){
		//main execution loop for fcfs
		int runningQuanta = 0;
		int runStartTime =0;
		
		while(waitingQ.size() != 0 || readyQ.size() != 0 || running!=null){ //loop until there is nothing in waiting, ready, or running
			
			//for all elements of waitingQ check if arrivial time matches current time if so add to readyQ
			int i=0;
			while(i<waitingQ.size()){
				if(waitingQ.get(i).arrival_time == time){
					readyQ.add(waitingQ.get(i));
					waitingQ.remove(i);
					
				}
				else{
					i++;
				}
			}

			//check if running is empty
			if(running==null){
				//if so add the first element of readyQ to running;
				running = readyQ.get(0);
				runningQuanta = running.priority;
				runStartTime = time;
				if(running.responseTime == -1) //if response time hasnt been set yet (first time running)
					running.responseTime = time;
				readyQ.remove(0);//remove that element from readyQ
			}

			//else check if process is done
			else if(running.burst_time == 0){
				//in this case whatever was running just finished
				running.finishTime = time; //set the finish time of the process to now
				//if there is something left to run
				if(!readyQ.isEmpty()){
					doneQ.add(running); //process finished, add to doneQ
					running= readyQ.get(0);
					runningQuanta = running.priority;
					runStartTime=time;
					if(running.responseTime == -1) //if response time hasnt been set yet (first time running)
						running.responseTime = time;
					readyQ.remove(0);//remove that element from readyQ
				}
				//there is nothing more to run right now
				else{ 
					doneQ.add(running); //process finished, add to doneQ
					running=null;
				}
			}

			//if quanta is over
			else if((time - runStartTime)%runningQuanta==0){
				readyQ.add(running);
				running = readyQ.get(0); //swap with next member in readyQ;
				runningQuanta = running.priority;
				runStartTime=time;
				if(running.responseTime==-1)
					running.responseTime=time;
				readyQ.remove(0);
			}

			//if something is still running
			if(running!=null){ 
				running.burst_time--; //take one more second off time remaining
				System.out.println("Currently running process: "+ running.pid +".  Time: "+time);
			}

			time++; //increment time
			
		}
		calcAnalytics(doneQ);

	}
}