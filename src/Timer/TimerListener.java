package Timer;

import java.util.EventListener;

public interface TimerListener extends EventListener {

	void timerChanged();
	void timerModeChanged();
	
}
