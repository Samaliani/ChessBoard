package Core;

public abstract class Component {

	Manager manager;

	public Component(Manager manager) {
		this.manager = manager;
	}

	abstract public String getId();

	protected Manager getManager() {
		return manager;
	}

	public void appInitialization() {
	}

	public void appStart() {
	}

	public void appFinalization() {
	}
}
