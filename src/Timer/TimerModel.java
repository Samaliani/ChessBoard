package Timer;

import java.time.Duration;

public class TimerModel {

	String id;
	String name;
	int minutes;

	public TimerModel(String name, int minutes) {
		this.name = name;
		this.minutes = minutes;
		updateId();
	}

	public String getId(){
		return id;
	}

	public String getName() {
		return name;
	}

	public Duration adjustMove(Duration time) {
		return time;
	}

	public Duration getTimeForWhite(){
		return Duration.ofSeconds(minutes * 60, 0);
	}

	public Duration getTimeForBlack() {
		return Duration.ofSeconds(minutes * 60, 0);
	}
	
	final static String Mode = "standard"; 
	
	private void updateId(){
		id = String.format("%s%d", Mode, minutes);
	}
}
