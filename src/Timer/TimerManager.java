package Timer;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import Chess.Game;
import Chess.Piece;
import Core.Component;
import Core.EventManager;
import Core.EventProvider;
import Core.Manager;
import Core.SettingSubscriber;
import Game.GameEventListener;

public class TimerManager extends Component implements GameEventListener, SettingSubscriber, EventProvider {

	public static final String TimerManagerId = "timer";

	int activeMode;
	List<TimerModel> modes;

	Piece.Color turnColor;

	Timer timer;
	TimerModel mode;
	Duration white;
	Duration black;

	Instant lastEvent;

	public TimerManager(Manager manager) {
		super(manager);

		modes = new ArrayList<TimerModel>();
		modes.add(new TimerModel(App.Messages.Timers.Blitz5Mode, 5));
		modes.add(new TimerModel(App.Messages.Timers.Blitz10Mode, 10));
		modes.add(new TimerModel(App.Messages.Timers.Rapid30Mode, 30));
	}

	@Override
	public String getId() {
		return TimerManagerId;
	}

	@Override
	public void appStart() {
	}

	@Override
	public void beforeGame(Game game) {

		TimerModel mode = modes.get(activeMode);

		white = mode.getTimeForWhite();
		black = mode.getTimeForBlack();
		raiseTimerChangedEvent();
	}

	@Override
	public void startGame(Game game) {

		timer = new Timer(TimerManagerId, true);

		mode = modes.get(activeMode);

		white = mode.getTimeForWhite();
		black = mode.getTimeForBlack();

		lastEvent = Instant.now();
		turnColor = game.getTurnColor();

		raiseTimerChangedEvent();
		runTimer();
	}

	@Override
	public void makeMove(Game game) {

		synchronized (this) {
			cancelTimer();
		}
		adjustTime();
		setActiveTime(mode.adjustMove(getActiveTime()));

		turnColor = turnColor.inverse();
		runTimer();
	}

	@Override
	public void endGame(Game game) {
		mode = null;
		timer.cancel();
		timer = null;
	}

	@Override
	public boolean isSupportedListener(EventListener listener) {
		return (listener instanceof TimerListener);
	}

	private Duration getActiveTime() {
		if (turnColor == Piece.Color.White)
			return white;
		else
			return black;
	}

	private void setActiveTime(Duration value) {

		if (value.isNegative() || value.isZero())
			value = Duration.ofSeconds(0);

		if (turnColor == Piece.Color.White)
			white = value;
		else
			black = value;
		raiseTimerChangedEvent();
	}

	private List<EventListener> getTimerListeners() {
		EventManager eventManager = (EventManager) getManager().getComponent(EventManager.EventManagerId);
		return eventManager.getListeners(getId());
	}

	private void raiseTimerChangedEvent() {
		for (EventListener listener : getTimerListeners())
			((TimerListener) listener).timerChanged();
	}

	private void raiseTimerModeChangedEvent() {
		for (EventListener listener : getTimerListeners())
			((TimerListener) listener).timerModeChanged();
	}

	private void adjustTime() {

		synchronized (this) {
			Instant newEvent = Instant.now();
			Duration period = Duration.between(lastEvent, newEvent);
			lastEvent = newEvent;

			setActiveTime(getActiveTime().minus(period));
		}
	}

	private void runTimer() {

		timer.schedule(new TimerTask() {
			public void run() {
				adjustTime();
			}
		}, getActiveTime().toMillis() % 1000, 1000);
	}

	private void cancelTimer() {
		timer.purge();
	}

	@Override
	public void loadSettings(Properties preferences) {
		String modeId = preferences.getProperty("Timer.Mode", "standard5");
		TimerModel mode = getTimerById(modeId);
		if (mode != null)
			activeMode = modes.indexOf(mode);
		else
			activeMode = 0;

		updateMode();
	}

	@Override
	public void saveSettings(Properties preferences) {
		preferences.setProperty("Timer.Mode", modes.get(activeMode).id);
	}

	public int getModeCount() {
		return modes.size();
	}

	public String getModeName(int index) {
		return modes.get(index).getName();
	}

	public int getActiveMode() {
		return activeMode;
	}

	public void setActiveMode(int index) {
		if (activeMode == index)
			return;
		activeMode = index;
		updateMode();
		raiseTimerModeChangedEvent();
	}

	public Duration getWhiteTime() {
		return white;
	}

	public Duration getBlackTime() {
		return black;
	}

	private void updateMode() {
		TimerModel mode = modes.get(activeMode);

		white = mode.getTimeForWhite();
		black = mode.getTimeForBlack();
	}

	private TimerModel getTimerById(String id) {
		for (TimerModel mode : modes)
			if (mode.id.equals(id))
				return mode;
		return modes.get(0);
	}
}
