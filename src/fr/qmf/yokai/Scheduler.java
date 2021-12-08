package fr.qmf.yokai;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class Scheduler implements Tickable {

	private long tickCount;
	
	private Random random = new Random();
	private Queue<Runnable> scheduledTasks = new ArrayDeque<>();
	private Map<Long, ScheduledTask> repeatingTasks = new HashMap<>();
	
	public void scheduleTask(Runnable runnable) {
		this.scheduledTasks.add(runnable);
	}
	
	public long scheduleRepeatingTask(Runnable runnable, long delay, long period) {
		long id = random.nextLong();
		repeatingTasks.put(id, new ScheduledTask(runnable, delay, period));
		return id;
	}
	
	public void removeRepeatingTask(long id) {
		this.repeatingTasks.remove(id);
	}
	
	@Override
	public void tick() {
		Runnable scheduled = scheduledTasks.poll();
		while(scheduled != null) {
			scheduled.run();
			scheduled = scheduledTasks.poll();
		}
		
		repeatingTasks.values().forEach(s -> {
			if(s.delay > 0) s.delay--;
			if(s.delay == 0 && tickCount % s.period == 0) {
				s.runnable.run();
			}
		});
		
		tickCount++;
	}
	
	public static class ScheduledTask{
		private Runnable runnable;
		private long delay, period;
		public ScheduledTask(Runnable runnable, long delay, long period) {
			this.runnable = runnable;
			this.delay = delay;
			this.period = period;
		}
	}

}
