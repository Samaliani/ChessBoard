package Game;

import java.util.EventListener;

import Chess.Game;

public interface GameEventListener extends EventListener {

	void beforeGame(Game game);
	void startGame(Game game);
	void makeMove(Game game);
	void endGame(Game game);
}
