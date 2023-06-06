import java.io.*;
import java.util.*;

public class Project3 {
	public static void main(String[] args) {
		// Store each job along with their arrival times and durations
		List<String> jobs = new ArrayList<String>();
		Map<String, Integer> arrivalTimes = new HashMap<String, Integer>();
		Map<Integer, ArrayList<String>> arrivalTimesRev = new HashMap<Integer, ArrayList<String>>();
		Map<String, Integer> durations = new HashMap<String, Integer>();
		
		//Read as long as no IOException
		try {
			// Iterate all lines
			String filename = "jobs.txt";
			Scanner fileRead = new Scanner(new File(filename));
			String line = "";
			
			while (fileRead.hasNextLine()) {
				// Read line and get arrival time and duration of each job
				line = fileRead.nextLine();
				String[] elems = line.split("\t");
				
				// Get job, and arrival time and duration
				String job = elems[0];
				int arrivalTime = Integer.parseInt(elems[1]);
				int duration = Integer.parseInt(elems[2]);
				
				// Arrived jobs list exists
				if (arrivalTimesRev.containsKey(arrivalTime)) {
					ArrayList<String> jobsArrive = arrivalTimesRev.get(arrivalTime);
					jobsArrive.add(job);
					arrivalTimesRev.put(arrivalTime, jobsArrive);
				}
				// List doesn't exist yet
				else {
					ArrayList<String> jobsArrive = new ArrayList<String>();
					jobsArrive.add(job);
					arrivalTimesRev.put(arrivalTime, jobsArrive);
				}
				
				// Add job and its times
				jobs.add(job);
				arrivalTimes.put(job, arrivalTime);
				durations.put(job, duration);
			}
			
			// Did we get the input command?
			if (args.length == 1) {
				// Object for scheduling algorithms
				Algorithms algos = new Algorithms(jobs, arrivalTimes, arrivalTimesRev, durations);
				
				// Get command from input parameter
				String cmd = args[0];
				cmd = cmd.toUpperCase();
			
				// Round Robin
				if (cmd.equals("RR")) 
					algos.RR();
				
				// Shortest Remaining Time
				else if (cmd.equals("SRT")) 
					algos.SRT();
				
				// Feedback (q = 1)
				else if (cmd.equals("FB")) 
					algos.FB();
				
				// Do all 3
				else if (cmd.equals("ALL")) {
					algos.RR();
					algos.SRT();
					algos.FB();
				}
			}
		}
		catch (Exception e) {
		}
	}
}