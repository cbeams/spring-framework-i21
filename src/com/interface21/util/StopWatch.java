
package com.interface21.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Simple stop watch, allowing for timing of a number of 
 * tasks, exposing total running time and running time for each
 * named task.
 * <br>Note that this object is not designed to be threadsafe, and does not
 * use synchronization or threading. Therefore it is safe to invoke it from EJBs.
 * <br>This class is normally used to verify performance during proof-of-concepts
 * and in development, rather than as part of production applications.
 * @author  Rod Johnson
 * @since May 2, 2001
 */
public class StopWatch {
    
    //---------------------------------------------------------------------
    // Instance data
    //---------------------------------------------------------------------
    /** Start time of the current task */
    private long    st;
    
    /** Total running time */
    private long    runningTime;
    
    /** List of TaskInfo objects */
    private List  taskList = new LinkedList();
    
    /** Name of the current task */
    private String currentTask;
	
	/** Identifier of this stopwatch.
	 * Handy when we have output from multiple stop watches and need to distinguish between them.
	 */
	private String id = "";
    
    
    //---------------------------------------------------------------------
    // Constructors
    //---------------------------------------------------------------------
    /** 
     * Construct a new stop watch.
     * Does not start any task.
     */
    public StopWatch() {
    }
	
	
	/** 
     * Construct a new stop watch with the given id
     * Does not start any task.
     * @param id identifier for this stop watch. 
     * Handy when we have output from multiple stop watches and need to distinguish between them.
     */
	public StopWatch(String id) {
		 this.id = id;
    }
    
    //---------------------------------------------------------------------
    // Public methods
    //---------------------------------------------------------------------
    /** 
     * Start a named task. The results are undefined if stop() or timing
     * methods are called without invoking this method.
     * @param task name of the task to start
     */
    public void start(String task) {
        st = System.currentTimeMillis();
        this.currentTask = task;
    }
    
    /** 
     * Stop the current task. The results are undefined if timing
     * methods are called without invoking at least one pair start()/stop()
     * methods.
     */
    public void stop() {
        long lastTime = System.currentTimeMillis() - st;
        runningTime += lastTime;
        taskList.add(new TaskInfo(currentTask, lastTime));
        currentTask = "No task running";
    }
    
    /** 
     * Return the total time in milliseconds for all tasks
     * @return the total time in milliseconds for all tasks
     */
    public long getTotalTime() {
        return runningTime;
    }
    
    /**
     * Return the time taken by the last operation
     * @return the time taken by the last operation
     */
    public long getLastInterval() {
    	if (taskList.size() == 0)
    		throw new RuntimeException("No tests run: can't get last interval");
    	TaskInfo ti = (TaskInfo) taskList.get(taskList.size() - 1);
    	return ti.getTime();
    }
        
    /** 
     * Return the total time in seconds for all tasks
     * @return the total time in seconds for all tasks
     */
    public double getTotalTimeSecs() {
        return ((double) runningTime) / 1000.0;
    }
    
    /** 
     * Return the number of tasks timed
     * @return the number of tasks timed
     */
    public int getTaskCount() {
        return taskList.size();
    }
    
    /** 
     * Return an array of the data for tasks performed
     * @return an array of the data for tasks performed
     */
    public TaskInfo[] getTaskInfo() {
        return (TaskInfo[]) taskList.toArray(new TaskInfo[0]);
    }
    
    /** 
     * Return an informative string describing all tasks performed
     * @return an informative string describing all tasks performed. For
     * custom reporting, call getTaskInfo() and use the task info directly
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(id + ": StopWatch: running time (secs)=" + getTotalTimeSecs() + "; ");
        TaskInfo[] tasks = getTaskInfo();
        for (int i = 0; i < tasks.length; i++) {
            if (i > 0)
                sb.append("; ");
            sb.append("[" + tasks[i].getTaskName() + "] took " + tasks[i].getTimeSecs());
			long percent = Math.round((100.0 * tasks[i].getTimeSecs()) / getTotalTimeSecs());
			sb.append("=" + percent + "%");
        }
        return sb.toString();
    }   // toString
    
	/** 
	 * Return an informative string describing all tasks performed
     * @return an informative string describing all tasks performed. For
     * custom reporting, call getTaskInfo() and use the task info directly
     */
    public String prettyPrint() {
        StringBuffer sb = new StringBuffer(id + ": StopWatch: running time (secs)=" + getTotalTimeSecs() + "\n");
        TaskInfo[] tasks = getTaskInfo();
		sb.append("-----------------------------------------\n");
		sb.append("ms\t%\tTask name\n");
		sb.append("-----------------------------------------\n");
        for (int i = 0; i < tasks.length; i++) {
            sb.append(tasks[i].getTime() + "\t");
			long percent = Math.round((100.0 * tasks[i].getTimeSecs()) / getTotalTimeSecs());
			sb.append(percent + "%\t");
			sb.append(tasks[i].getTaskName() + "\n");
        }
        return sb.toString();
    }   // toString
    
    
    //---------------------------------------------------------------------
    // Inner classes
    //---------------------------------------------------------------------
    /** 
     * Inner class to hold data about one task 
     */
    public class TaskInfo {
        
        private String task;
        private long time;
        
        TaskInfo(String task, long time) {
            this.task = task;
            this.time = time;
        }
        
        /** Return the name of this task */
        public String getTaskName() {
            return task;
        }
        
        /** Return the time in milliseconds this task took 
         * @return the time in milliseconds this task took 
         */
        public long getTime() {
            return time;
        }
        
        /** Return the time in seconds this task took 
         * @return the time in seconds this task took 
         */
        public double getTimeSecs() {
            return ((double) time) / 1000.0;
        }
    }   // inner class TaskInfo
    
}	// class StopWatch
