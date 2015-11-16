import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Chess.Board;
import Chess.PGN.PGN;
import Communication.Event;
import Communication.EventStorage;
import Communication.FileCommunication;
import LowLevel.StateMachine;

public class Runner {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		
		//BoardData bd = Utils.LoadFromFile(args[0]);
		//System.out.println(bd.toString());
		 

		Board board = new Board();
		board.reset();
		

		//EventStorage events = new EventStorage();
		//FileCommunication comm = new FileCommunication(events, ".\\Data\\openning5.txt");

		//comm.run();
		//while(comm.isAlive())
		//	Thread.sleep(100);

//		StateMachine sm = new StateMachine(board);
//		sm.start();
		//Analyzer an = new Analyzer(board, events);
		//an.start();
		
		//while (events.getEventCount() > 0)
		//	an.processEvents();

	//	PGN pgn = new PGN(board);
	//	System.out.println(pgn.exportMoves());
		


	}

}
