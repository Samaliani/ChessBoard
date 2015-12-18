package Core;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager extends Component implements ManagerExtender {

	public static final String EventManagerId = "events";

	boolean isStarted;

	List<EventProvider> providers = new ArrayList<EventProvider>();
	List<EventListener> listeners = new ArrayList<EventListener>();
	Map<String, List<EventListener>> table = new HashMap<String, List<EventListener>>();

	public EventManager(Manager manager) {
		super(manager);
	}

	@Override
	public String getId() {
		return EventManagerId;
	};

	@Override
	public void appInitialization() {
		isStarted = true;
		updateTable();
	}

	@Override
	public void appFinalization() {
		isStarted = false;
		table.clear();
		listeners.clear();
		providers.clear();
	}

	private void updateTable() {
		if (!isStarted)
			return;

		for(EventProvider provider : providers)
		{
			List<EventListener> list = new ArrayList<EventListener>();
			for(EventListener listener : listeners)
				if (provider.isSupportedListener(listener)){
					list.add(listener);
					}
			if(list.size() != 0)
				table.put(provider.getId(), list);
		}
	}

	public List<EventListener> getListeners(String id) {
		return table.getOrDefault(id, new ArrayList<EventListener>());
	}
	
	public void addListener(EventListener listener){
		listeners.add(listener);
		updateTable();
	}
	
	public void removeListener(EventListener listener){
		listeners.remove(listener);
		updateTable();
	}

	@Override
	public void componentAdded(Component component) {
		boolean needUpdate = false;
		if (component instanceof EventProvider) {
			providers.add((EventProvider) component);
			needUpdate = true;
		}

		if (component instanceof EventListener) {
			listeners.add((EventListener) component);
			needUpdate = true;
		}

		if (needUpdate)
			updateTable();
	}

	@Override
	public void componentRemoved(Component component) {
		boolean needUpdate = false;
		if (component instanceof EventProvider) {
			providers.remove((EventProvider) component);
			needUpdate = true;
		}

		if (component instanceof EventListener) {
			listeners.remove((EventListener) component);
			needUpdate = true;
		}

		if (needUpdate)
			updateTable();
	}

}
