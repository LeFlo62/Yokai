package fr.qmf.yokai;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class Scheduler implements Tickable {

	private long tickCount;
	
	private Random random = new Random();
	private Map<Long, ScheduledTask> scheduledTasks = new HashMap<>();
	private Map<Long, ScheduledTask> repeatingTasks = new HashMap<>();
	
	/**
	 * Schedules the defined {@code runnable} to be executed after {@code delay} ticks
	 * 
	 * @param runnable The runnable to run
	 * @param delay The delay in ticks before executing first the runnable
	 * @return the scheduled task id.
	 */
	public long scheduleTask(Runnable runnable, long delay) {
		long id = random.nextLong();
		this.scheduledTasks.put(id, new ScheduledTask(runnable, delay, 0));
		return id;
	}
	
	/**
	 * Schedules the defined {@code runnable} to be executed after
	 * {@code delay} ticks and every {@code period} ticks.
	 * 
	 * @param runnable The runnable to run
	 * @param delay The delay in ticks before executing first the runnable
	 * @param period The period in ticks to wait between loops
	 * @return the scheduled task id.
	 */
	public long scheduleRepeatingTask(Runnable runnable, long delay, long period) {
		long id = random.nextLong();
		repeatingTasks.put(id, new ScheduledTask(runnable, delay, period));
		return id;
	}
	
	/**
	 * Removes the scheduled task associated with the given {@code id} from the scheduled
	 * tasks. 
	 * 
	 * @param id The task id to be removed.
	 */
	public void removeScheduledTask(long id) {
		this.scheduledTasks.remove(id);
	}
	
	/**
	 * Removes the scheduled task associated with the given {@code id} from the scheduled
	 * repeating tasks. 
	 * 
	 * @param id The task id to be removed.
	 */
	public void removeRepeatingTask(long id) {
		this.repeatingTasks.remove(id);
	}
	
	@Override
	public void tick() {
		Iterator<Entry<Long, ScheduledTask>> scheduled = scheduledTasks.entrySet().iterator();
		while(scheduled.hasNext()) {
			ScheduledTask task = scheduled.next().getValue();
			if(task.delay == 0) {
				scheduled.remove();
				task.runnable.run();
			}
			task.delay--;
		}
		
		repeatingTasks.values().forEach(s -> {
			if(s.delay > 0) s.delay--;
			if(s.delay == 0 && tickCount % s.period == 0) {
				s.runnable.run();
			}
		});
		
		tickCount++;
	}
	
	public static class ScheduledTask{ //Records from Java 15 may be useful here, but the lower Java version, the better for consumer. Although untrue for security.
		private Runnable runnable;
		private long delay, period;
		public ScheduledTask(Runnable runnable, long delay, long period) {
			this.runnable = runnable;
			this.delay = delay;
			this.period = period;
		}
	}

}
