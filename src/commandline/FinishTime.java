package commandline;

import java.util.ArrayList;
import java.util.Date;

/**
 * The type Finish time.
 */
public class FinishTime {
	
	//Variable History Size for smoother Changes after each Cycle
    private int HistorySize = 5;
    //Some pretty straight forward Variables witch should be self-explanatory
    private long RemainingCycles;
    private ArrayList<Long> TimeStamps = new ArrayList<>();
    private long startTime;
    private long timePerCycle;

    /**
     * Instantiates a new Finish time.
     *
     * @param totalcycles the totalcycles
     * @param historySize the history size
     */
//Create Object
    public FinishTime(long totalcycles,int historySize){
        this.RemainingCycles = totalcycles;
        this.timePerCycle = 500;
        this.HistorySize = historySize;
    }

    /**
     * Start.
     */
//Start Time with first TimeStamp
    void start(){
        if (TimeStamps.size() == 0){
        	startTime = new Date().getTime();
            for (int i = 0; i < HistorySize;i++) {
                TimeStamps.add(new Date().getTime() - timePerCycle * (HistorySize - i));
            }
        }
    }

    /**
     * Add check point.
     */
//Adding CheckPoints after each Calculation for better results over time
    void addCheckPoint(){
        if (TimeStamps.size() > 0){
        	if (TimeStamps.size() >= HistorySize) {
        		TimeStamps.remove(0);
        	}
        	TimeStamps.add(new Date().getTime());
            RemainingCycles--;
            long temp = 0;
            for (int i = HistorySize - 1; i > 1;i--){
                temp += TimeStamps.get(i) - TimeStamps.get(i-1);
            }
            timePerCycle = temp / HistorySize;
        }
    }

    /**
     * Get estimated finish time long.
     *
     * @return the long
     */
//Get back the estimated finish Time for all Calculations in Total in milliseconds
    long getEstimatedFinishTime(){
        return new Date().getTime() + (timePerCycle * RemainingCycles);
    }

    /**
     * Get estimated needed time long.
     *
     * @return the long
     */
//Get back the estimated needed time for all Calculations in Total in milliseconds
    long getEstimatedNeededTime(){
        return timePerCycle * RemainingCycles;
    }

    /**
     * Format string.
     *
     * @param time the time
     * @return the string
     */
//Retrun the Formatted time like 00:00:00
	static String format(long time){
    	time /= 1000;
    	long sec = 0,min = 0,hour = 0;
    	while(time >= 3600){
    	    hour++;
    	    time-=3600;
	    }
	    while(time >= 60){
		    min++;
		    time-=60;
	    }
	    sec = time;
	    return "" + (hour < 10 ? (hour != 0 ? ("0" + hour):"00"):hour) + ":" + (min < 10 ? (min != 0 ? ("0" + min):"00"):min) + ":" + (sec < 10 ? (sec != 0 ? ("0" + sec):"00"):sec);
    }
    
    
    //GETTER AND SETTER


    /**
     * Gets remaining cycles.
     *
     * @return the remaining cycles
     */
    public long getRemainingCycles() {
		return RemainingCycles;
	}

    /**
     * Gets start time.
     *
     * @return the start time
     */
    public long getStartTime() {
		return startTime;
	}

    /**
     * Gets time per cycle.
     *
     * @return the time per cycle
     */
    public long getTimePerCycle() {
		return timePerCycle;
	}
}
