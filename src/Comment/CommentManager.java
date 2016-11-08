package Comment;

import java.util.EventListener;
import java.util.List;
import java.util.Properties;

import Chess.Game;
import Core.Manager;
import Core.SettingSubscriber;
import Game.GameEventListener;
import Core.Component;
import Core.EventManager;
import Core.EventProvider;

public class CommentManager extends Component implements SettingSubscriber, EventProvider, GameEventListener {

	public CommentManager(Manager manager) {
		super(manager);
	}

	public static final String CommentManagerId = "comment";

	boolean allowComments;

	@Override
	public String getId() {
		return CommentManagerId;
	}

	@Override
	public boolean isSupportedListener(EventListener listener) {
		return (listener instanceof CommentListener);
	}

	@Override
	public void loadSettings(Properties preferences) {
		allowComments = Boolean.parseBoolean(preferences.getProperty("Comments.Allow", "false"));
	}

	@Override
	public void saveSettings(Properties preferences) {
	}

	@Override
	public void beforeGame(Game game) {
		removeComment();
	}

	@Override
	public void startGame(Game game) {
		removeComment();
	}

	@Override
	public void makeMove(Game game) {
		removeComment();
	}

	@Override
	public void rollbackMove(Game game) {
		removeComment();
	}

	@Override
	public void endGame(Game game) {
		removeComment();
	}

	private List<EventListener> getListeners() {
		EventManager eventManager = (EventManager) getManager().getComponent(EventManager.EventManagerId);
		return eventManager.getListeners(getId());
	}

	public void addComment(String commentText) {
		if (allowComments && (!commentText.isEmpty()))
			for (EventListener listener : getListeners())
				((CommentListener) listener).addComment(commentText);
	}

	public void removeComment() {
		if (allowComments)
			for (EventListener listener : getListeners())
				((CommentListener) listener).removeComment();
	}

}
