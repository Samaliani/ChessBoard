package Core;

import java.util.Properties;

public interface SettingSubscriber {

	void loadSettings(Properties preferences);
	void saveSettings(Properties preferences);

}
