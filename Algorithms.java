import java.util.*;
import java.io.*;

public class Algorithms {
	// Array lists for job info
	private List<String> jobs;
	private Map<String, Integer> arrivalTimes;
	private Map<Integer, ArrayList<String>> arrivalTimesRev;
	private Map<String, Integer> durations;
	
	// Map for time spent and results
	private Map<String, Integer> timeSpent;
	private Map<String, String> results;
	
	// Queue for RR and list for SRT
	private Queue<String> queue;
	private List<String> list;
	
	// Queue for feedback
	private Queue<String> queueFB1;
	private Queue<String> queueFB2;
	private Queue<String> queueFB3;
	
	// HashMap for time in feedback
	private Map<String, Integer> lastTime;
	
	public Algorithms(List<String> jobs, Map<String, Integer> arrivalTimes, 
					Map<Integer, ArrayList<String>> arrivalTimesRev, Map<String, Integer> durations) {
		// Initialize job info
		this.jobs = jobs;
		this.arrivalTimes = arrivalTimes;
		this.arrivalTimesRev = arrivalTimesRev;
		this.durations = durations;
		
		// Initialize map for time spent and results
		this.timeSpent = new HashMap<String, Integer>();
		this.results = new HashMap<String, String>();
		
		// Initialize queue for RR and list for SRT
		this.queue = new LinkedList<String>();
		this.list = new ArrayList<String>();
		
		// Initialize queue for feedback
		this.queueFB1 = new LinkedList<String>();
		this.queueFB2 = new LinkedList<String>();
		this.queueFB3 = new LinkedList<String>();
		
		// Initialize hash map for feedback
		this.lastTime = new HashMap<String, Integer>();
	}
	
	private void fill() {
		for (int i=0; i<jobs.size(); i++) {
			// Job in the list
			String job = jobs.get(i);
			
			// By default, no time spent, no result, and last ran at 0
			timeSpent.put(job, 0);
			results.put(job, "");
			lastTime.put(job, 0);
		}
	}
	
	private void notStarted(int time) {
		// Job not started
		String jobStr = "";
		
		for (String s: jobs) {
			// Arrival time of each job
			int tempTime = arrivalTimes.get(s);
			
			if (time < tempTime) {
				// Update job not started yet
				jobStr = results.get(s);
				jobStr += " ";
				results.put(s, jobStr);
			}
		}
	}
	
	public void RR() {
		// Fill in data structures
		System.out.println("\nRR (Round Robin, q=1): ");
		fill();
		
		// Current process, time in process, and # of completed
		String curr = "";
		int time = 0;
		int done = 0;
		
		// Job read in and its info
		ArrayList<String> jobsArrived = new ArrayList<String>();
		String job = "";
		int jobTime = 0;
		String jobStr = "";
		
		while (done < jobs.size()) {
			// New job arrives
			if (arrivalTimesRev.containsKey(time)) {
				// Jobs arriving
				jobsArrived = arrivalTimesRev.get(time);
				
				// Add all jobs
				for (int a=0; a<jobsArrived.size(); a++) {
					// Job that arrived
					job = jobsArrived.get(a);
					
					// If empty queue, it runs, else it's in queue
					if (a == 0 && queue.size() == 0 && curr == "") {
						curr = job;
						timeSpent.put(curr, 0);		
					}
					else {
						queue.add(job);
						timeSpent.put(job, 0);	
					}
				}
			}
			// Job not started yet
			notStarted(time);
			
			// If time is after 0
			if (queue.size() > 0 && time != 0) {	
				// Not ended yet
				if (timeSpent.get(curr) < durations.get(curr)) 
					queue.add(curr);

				// Remove from queue
				curr = queue.poll();
			}
			else {
				// If it's finished
				if (timeSpent.get(curr) == durations.get(curr)) 
					curr = "";
			}
			
			// Iterate queue and update durations and output
			for (String s: queue) {	
				// Update current job output in queue
				jobStr = results.get(s);
				jobStr += " ";
				results.put(s, jobStr);
			}	

			// Process is running
			if (curr != "") {
				// Update current job output 
				jobStr = results.get(curr);
				jobStr += "X";
				results.put(curr, jobStr);
				
				// Update current job time spent
				jobTime = timeSpent.get(curr);
				++jobTime;
				timeSpent.put(curr, jobTime);
				
				// Job is completed
				if (timeSpent.get(curr) == durations.get(curr))
					++done;
			}
			
			// Increment time
			++time;
		}
		printResult();
	}
	
	public void SRT() {
		// Fill in data structures
		System.out.println("\nSRT (Shortest Remaining time): ");
		fill();
		
		// Current process, time in process, and # of completed
		String curr = "";
		int time = 0;
		int done = 0;
		
		// Job read in and its info
		ArrayList<String> jobsArrived = new ArrayList<String>();
		String job = "";
		int jobTime = 0;
		String jobStr = "";
		
		while (done < jobs.size()) {
			// New job arrives
			if (arrivalTimesRev.containsKey(time)) {
				// Jobs arriving
				jobsArrived = arrivalTimesRev.get(time);
				
				// Add all jobs
				for (int a=0; a<jobsArrived.size(); a++) {
					// Job that arrived
					job = jobsArrived.get(a);
				
					// If empty queue and non is running, it's current
					if (a == 0 && list.size() == 0 && curr == "") {
						curr = job;
						timeSpent.put(curr, 0);
					}
					else {
						list.add(job);
						timeSpent.put(job, 0);
					}
				}
			}
			// Job not started yet
			notStarted(time);
			
			if (list.size() > 0) {
				// Remaining time and index of first in list
				String srtStr = list.get(0);
				int srt = durations.get(srtStr) - timeSpent.get(srtStr);
				int srtInd = 0;
				
				for (int i=0; i<list.size(); i++) {
					// Evaluate remaining time
					String tempStr = list.get(i);
					int tempSrt = durations.get(tempStr) - timeSpent.get(tempStr);

					// Get info of one with shorter remaining time
					if (tempSrt < srt) {
						srtStr = tempStr;
						srt = tempSrt;
						srtInd = i;
					}
				}
				
				// Maybe a shorter remaining time
				if (curr != srtStr) {
					// Process wasn't completed
					if (curr != "") {
						// Current remaining time
						int currTime = durations.get(curr) - timeSpent.get(curr);
						
						// Array has shortest remaining time
						if (srt < currTime) {
							list.add(curr);
							list.remove(srtInd);
							curr = srtStr;
						}
					}		
					// Process was completed, so remove shortest remaining time process and update
					else {
						list.remove(srtInd);	
						curr = srtStr;
					}
				}
			}
			// Iterate queue and update durations and output
			for (String s: list) {	
				// Update current job output in list
				jobStr = results.get(s);
				jobStr += " ";
				results.put(s, jobStr);
			}	
	
			// If process is running
			if (curr != "") {
				// Update current job output 
				jobStr = results.get(curr);
				jobStr += "X";
				results.put(curr, jobStr);
				
				// Update current job time spent
				jobTime = timeSpent.get(curr);
				++jobTime;
				timeSpent.put(curr, jobTime);
				
				// Job is completed
				if (timeSpent.get(curr) == durations.get(curr)) {
					curr = "";
					++done;
				}
			}
			// Increment time
			++time;
		}
		printResult();
	}
	
	public void FB() {
		// Fill in data structures
		System.out.println("\nFB (Feedback, q=1, 3 queues): ");
		fill();
		
		// Queue taken from and number of active
		int takenFrom = 0;
		int numActive = 0;
		
		// Info about removal
		String rem = "";
		
		// Current process, time in process, and # of completed
		String curr = "";
		int time = 0;
		int done = 0;
		
		// Job read in and its info
		ArrayList<String> jobsArrived = new ArrayList<String>();
		String job = "";
		int jobTime = 0;
		String jobStr = "";

		while (done < jobs.size()) {
			// New job arrives
			if (arrivalTimesRev.containsKey(time)) {
				// Jobs arriving
				jobsArrived = arrivalTimesRev.get(time);
				
				// Add all jobs
				for (int a=0; a<jobsArrived.size(); a++) {
					// Job that arrived
					job = jobsArrived.get(a);
					
					// If only one process active in queue 1, demote it to queue 2
					if (a == 0 && queueFB1.size() == 1 && numActive == 1) {
						rem = queueFB1.poll();
						queueFB2.add(rem);
					}

					// One more process is in queue 1, and runs
					++numActive;
					queueFB1.add(job);
					curr = queueFB1.peek();
				}
			}
			// Take current process from queue 1 if it has something
			if (queueFB1.size() > 0) {
				curr = queueFB1.peek();
				takenFrom = 1;
			}
			// Take current process from queue 2 if it has something
			else if (queueFB2.size() > 0) {
				curr = queueFB2.peek();
				takenFrom = 2;
			}
			// Take current process from queue 3 if it has something
			else if (queueFB3.size() > 0) {
				curr = queueFB3.peek();
				takenFrom = 3;
			}
			// Nothing in there
			else 
				curr = "";
			// Job not started yet
			notStarted(time);
			
			// Iterate queue 1 and update durations and output
			for (String s: queueFB1) {	
				// Omit current process since it's in queue and running
				if (s != curr) {
					// Update current job output in queue
					jobStr = results.get(s);
					jobStr += " ";
					results.put(s, jobStr);
				}
			}	
			// Iterate queue 2 and update durations and output
			for (String s: queueFB2) {	
				// Omit current process since it's in queue and running
				if (s != curr) {
					// Update current job output in queue
					jobStr = results.get(s);
					jobStr += " ";
					results.put(s, jobStr);
				}
			}	
			// Iterate queue 3 and update durations and output
			for (String s: queueFB3) {	
				// Omit current process since it's in queue and running
				if (s != curr) {
					// Update current job output in queue
					jobStr = results.get(s);
					jobStr += " ";
					results.put(s, jobStr);
				}
			}	
			
			// Process is running
			if (curr != "") {
				// Update current job output 
				jobStr = results.get(curr);
				jobStr += "X";
				results.put(curr, jobStr);
				
				// Update current job time spent
				jobTime = timeSpent.get(curr);
				++jobTime;
				timeSpent.put(curr, jobTime);
				
				// Current time is most recent time
				lastTime.put(curr, time);
				
				// If process finished, remove, else move to lower queue
				if (timeSpent.get(curr) == durations.get(curr)) {
					// If taken from queue 1, take out of queue 1
					if (takenFrom == 1)
						queueFB1.remove();
					
					// If taken from queue 2, take out of queue 2
					else if (takenFrom == 2)
						queueFB2.remove();
					
					// If taken from queue 3, take out of queue 3
					else if (takenFrom == 3)
						queueFB3.remove();
					
					// One process done, one less active process
					++done;
					--numActive;
				}
				else {
					// Remove to lower queue if more than one active process exists
					if (numActive > 1) {
						// If taken from queue 1, take out of queue 1 and add to queue 2
						if (takenFrom == 1) {
							queueFB1.remove();
							queueFB2.add(curr);
						}
						// If taken from queue 2, take out of queue 1 and add to queue 3
						else if (takenFrom == 2) {
							queueFB2.remove();
							queueFB3.add(curr);
						}
						// If taken from queue 3, take out of queue 3 and add to queue 3
						else if (takenFrom == 3) {
							queueFB3.remove();
							queueFB3.add(curr);
						}
					}
				}
			}
			//Increment time
			++time;
		}
		printResult();
	}
	
	public void printResult() {
		for (int i=0; i<jobs.size(); i++) {
			// Get each process's result
			String job = jobs.get(i);
			String result = results.get(job);
			
			// Print result
			System.out.println(job + " " + result);
		}
		// Reset data structures
		reset();
	}
	
	public void reset() {
		// Reset map for time spent and result
		timeSpent = new HashMap<String, Integer>();
		results = new HashMap<String, String>();
		
		// Reset queue for RR and list for SRT
		queue = new LinkedList<String>();
		list = new ArrayList<String>();
		
		// Reset queue for feedback
		queueFB1 = new LinkedList<String>();
		queueFB2 = new LinkedList<String>();
		queueFB3 = new LinkedList<String>();
		
		// Reset hashmap for feedback
		lastTime = new HashMap<String, Integer>();
	}
}