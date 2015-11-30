package Core;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import GUI.ChessBoardMain;

public class Program {

	public static void main(String args[]) {

		rxtxNativeHelper.initialize();
		initUIManager();
		
		Manager manager = new Manager();
		ChessBoardMain frame = new ChessBoardMain();
		manager.setFrame(frame);

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {

				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setVisible(true);
				
			}
		});
		
		manager.run();
	}
	
	private static void initUIManager() {

		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}		
	}

}
