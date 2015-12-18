package Game;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Properties;

import App.Messages;
import Core.Component;
import Core.EventManager;
import Core.EventProvider;
import Core.Manager;
import Core.SettingSubscriber;

public class GameModelManager extends Component implements SettingSubscriber, EventProvider {

	public static final String GameModelManagerId = "models";

	GameModel activeModel;
	List<GameModel> models;

	public GameModelManager(Manager manager) {
		super(manager);

		models = new ArrayList<GameModel>();
		models.add(new GameModel(getManager(), Messages.Game.Standard));
		models.add(new OpenningModel(getManager(), Messages.Game.Openning));
		models.add(new GameModel(getManager(), Messages.Game.Endgame));
	}

	@Override
	public String getId() {
		return GameModelManagerId;
	}

	public int getModelCount() {
		return models.size();
	}

	public String getModelName(int index) {
		return models.get(index).getName();
	}

	public int getActiveModel() {
		return models.indexOf(activeModel);
	}

	public void setActiveModel(int index) {
		setActiveModel(models.get(index));
	}

	private GameModel getModelById(String id) {
		for (GameModel model : models)
			if (model.getId().equals(id))
				return model;
		return models.get(0);
	}

	private void raiseModelChangeEvent() {
		EventManager eventManager = (EventManager) getManager().getComponent(EventManager.EventManagerId);
		for (EventListener listener : eventManager.getListeners(getId()))
			((GameModelListener) listener).gameModeChanged();
	}

	private void setActiveModel(GameModel model) {
		if (model == activeModel){
			raiseModelChangeEvent();
			return;
		}	
		
		EventManager eventManager = (EventManager) getManager().getComponent(
				EventManager.EventManagerId);
		eventManager.removeListener(activeModel);
		activeModel = model;
		eventManager.addListener(activeModel);
		raiseModelChangeEvent();
	}

	@Override
	public void loadSettings(Properties preferences) {
		String modeId = preferences.getProperty("Game.Mode", "base");
		GameModel model = getModelById(modeId);
		setActiveModel(model);
	}

	@Override
	public void saveSettings(Properties preferences) {
		preferences.setProperty("Game.Mode", activeModel.getId());
	}

	@Override
	public boolean isSupportedListener(EventListener listener) {
		return (listener instanceof GameModelListener);
	}
}
