package Communication;

import java.io.IOException;
import java.util.EventListener;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

import App.Messages;
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

	BoardReader reader;

	boolean fileInput;
	boolean isStop;

	public CommunicationManager(Manager manager) {
		super(manager);
		resetReader();
	}

	@Override
	public String getId() {
		return CommunicationManagerId;
	}

	@Override
	public void appStart() {

		doPortChanged(reader.getPortName());

		Thread eventThread = new Thread(this);
		eventThread.setDaemon(true);
		eventThread.start();
	}
	@Override
	public void appFinalization() {
		stop();
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

	private void resetReader(){
		reader = new BoardReader("");
	}

	private void readerDisconnect(boolean silent){

		try {
			reader.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		resetReader();
		if (!silent)
			doPortChanged(reader.getPortName());
	}

	public void run() {
		while (!isStop) {
			if (reader == null) {
				delay(50);
				continue;
			}

			Event event = null;
			try {
				event = reader.getEvent();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, Messages.Communication.ConnectionLost, Messages.Communication.ModuleName, JOptionPane.ERROR_MESSAGE);
				readerDisconnect(false);
				continue;
			}
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
	
	// Synchronous events to the board
	public void sendEvent(OutEvent event)
	{
		if (reader == null)
			return;
		
		try {
			reader.sendEvent(event);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, Messages.Communication.ConnectionLost, Messages.Communication.ModuleName, JOptionPane.ERROR_MESSAGE);
			readerDisconnect(false);
		}
	}

	@Override
	public void loadSettings(Properties preferences) {
		String portName = preferences.getProperty("Communication.Port", "");
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
		preferences.setProperty("Communication.Port", reader.getPortName());
	}

	public void stop() {
		isStop = true;
	}

	public void changePort(String portName) {

		if (fileInput)
			return;
		if (reader.getPortName() == portName)
			return;
		
		readerDisconnect(true);
		if (portName.length() != 0) {
			try {
				reader = new SerialReader(portName);
				reader.connect();
				doPortChanged(portName);
			} 
			catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						String.format(Messages.Communication.UnableToConnect, portName), Messages.Communication.ModuleName, JOptionPane.ERROR_MESSAGE);
				readerDisconnect(false);
			}
		}
	}

}
