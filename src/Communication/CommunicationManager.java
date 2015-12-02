package Communication;

import java.io.IOException;
import java.util.EventListener;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

import Communication.Readers.BoardReader;
import Communication.Readers.FileBoardReader;
import Communication.Readers.SerialReader;
import Core.Manager;
import Core.Component;
import Core.EventManager;
import Core.EventProvider;
import Core.SettingSubscriber;

public class CommunicationManager extends Component implements Runnable,
		SettingSubscriber, EventProvider {

	public static final String CommunicationManagerId = "communication";

	String portName;
	BoardReader reader;

	boolean fileInput;
	boolean isStop;

	public CommunicationManager(Manager manager) {
		super(manager);
	}

	@Override
	public String getId() {
		return CommunicationManagerId;
	}

	@Override
	public void appStart() {

		Thread eventThread = new Thread(this);
		eventThread.setDaemon(true);
		eventThread.start();
	}
	
	@Override
	public boolean isSupportedListener(EventListener listener) {
		return (listener instanceof CommunicationListener);
	}	

	private void doProcessEvent(Event event) {
		EventManager eventManager = (EventManager)getManager().getComponent(EventManager.EventManagerId);
		List<EventListener> listeners = eventManager.getListeners(getId());
		for (EventListener listener : listeners)
			((CommunicationListener)listener).processEvent(event);		
	}

	private void doPortChanged(String portName) {
		EventManager eventManager = (EventManager)getManager().getComponent(EventManager.EventManagerId);
		List<EventListener> listeners = eventManager.getListeners(getId());
		for (EventListener listener : listeners)
			((CommunicationListener)listener).portChanged(portName);		
	}

	private void delay(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (!isStop) {
			if (reader == null) {
				delay(50);
				continue;
			}

			Event event = reader.getEvent();
			if (event != null)
				doProcessEvent(event);
			else
				delay(50);
		}

		try {
			reader.disconnect();
		} catch (IOException e) {
		}
	}

	@Override
	public void loadSettings(Properties preferences) {
		portName = preferences.getProperty("Communication.Port");
		if (portName.equals("FILE")) {
			fileInput = true;
			String fileName = preferences.getProperty("Communication.File");
			reader = new FileBoardReader(fileName);
			try {
				reader.connect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else 
			changePort(portName);
	}

	@Override
	public void saveSettings(Properties preferences) {
		preferences.setProperty("Communication.Port", portName);
	}

	public void stop() {
		isStop = true;
	}

	public void changePort(String portName) {

		if (fileInput)
			return;
		if (this.portName == portName)
			return;
		
		if (reader != null) {
			try {
				reader.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			reader = null;
		}

		if (portName.length() != 0) {
			try {
				reader = new SerialReader(portName);
				reader.connect();
				doPortChanged(portName);
			} 
			catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						String.format("Unable to connect to %s.", portName),
						"Connection", JOptionPane.ERROR_MESSAGE);
				reader = new BoardReader(portName);
			}
		}
	}

}
