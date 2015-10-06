import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Chess.Board;
import Communication.Event;
import Communication.EventStorage;
import Communication.FileCommunication;
import GUI.MainWindow;
import LowLevel.Analyzer;
import PGN.PGN;

public class Runner {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		
		//BoardData bd = Utils.LoadFromFile(args[0]);
		//System.out.println(bd.toString());
		 

		Board board = new Board();
		board.reset();
		

		EventStorage events = new EventStorage();
		FileCommunication comm = new FileCommunication(events, ".\\Data\\openning.txt");

		comm.run();
		//while(comm.isAlive())
		//	Thread.sleep(100);

		Analyzer an = new Analyzer(board, events);
		an.start();
		
		while (events.getEventCount() > 0)
			an.processEvents();

		PGN pgn = new PGN(board);
		System.out.println(pgn.exportMoves());
		


	}

}
