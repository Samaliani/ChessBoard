package Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {

	Map<String, Component> components;
	Map<String, Object> objects;
	List<ManagerExtender> extenders;

	public Manager() {
		components = new HashMap<String, Component>();
		objects = new HashMap<String, Object>();
		extenders = new ArrayList<ManagerExtender>();
		createComponents();
		initialize();
	}

	protected void createComponents() {
	}

	protected void initialize() {
		for (Component component : components.values())
			component.appInitialization();
	}

	protected void finish() {
		for (Component component : components.values())
			component.appFinalization();
	}

	public void run() {
		for (Component component : components.values())
			component.appStart();
	}

	public void addObject(String id, Object object) {
		objects.put(id, object);
	}

	public void removeObject(String id) {
		objects.remove(id);
	}

	public void addComponent(Component component) {

		components.put(component.getId(), component);
		for (ManagerExtender extender : extenders)
			extender.componentAdded(component);

		if (component instanceof ManagerExtender)
			extenders.add((ManagerExtender) component);
	}

	public void removeComponent(Component component) {
		if (component instanceof ManagerExtender)
			extenders.remove((ManagerExtender) component);

		for (ManagerExtender extender : extenders)
			extender.componentRemoved(component);
		components.remove(component.getId());
	}

	public Component getComponent(String id) {
		return components.getOrDefault(id, null);
	}

}
