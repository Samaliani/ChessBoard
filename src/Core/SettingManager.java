package Core;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

public class SettingManager extends Component implements ManagerExtender {

	static String SettingManagerId = "settings";

	String fileName;
	String description;
	Properties preferences = new Properties();
	List<SettingSubscriber> subscribers = new ArrayList<SettingSubscriber>();

	public SettingManager(Manager manager, String fileName, String description) {
		super(manager);
		this.fileName = fileName;
		this.description = description;
	}

	public String getId() {
		return SettingManagerId;
	}

	@Override
	public void appInitialization() {
		loadSettings();
	}

	@Override
	public void appFinalization() {
		saveSettings();
	}
	
	public void componentAdded(Component component) {
		if (component instanceof SettingSubscriber)
			subscribers.add((SettingSubscriber)component);
	}

	public void componentRemoved(Component component) {
		subscribers.remove((SettingSubscriber)component);
	}

	private void loadSettings() {

		try {
			FileReader reader = new FileReader(fileName);
			preferences.load(reader);
		} catch (IOException e) {
			// Settings file can absent
		}

		for (SettingSubscriber listener : subscribers)
			listener.loadSettings(preferences);
	}

	private void saveSettings() {

		for (SettingSubscriber listener : subscribers)
			listener.saveSettings(preferences);

		try {
			FileWriter writer = new FileWriter(fileName);
			
			@SuppressWarnings("serial")
			Properties tmp = new Properties() {

				@Override
			    public synchronized Enumeration<Object> keys() {
			        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
			    }
			};
			tmp.putAll(preferences);
			tmp.store(writer, description);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
