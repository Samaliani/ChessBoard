package Communication;

import java.util.EventListener;

public interface CommunicationListener extends EventListener {

	void processEvent(Event event);
	public void portChanged(String portName);

}
