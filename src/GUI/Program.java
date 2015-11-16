package GUI;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Program {

	public static void main(String args[]) {

		Manager manager = new Manager();

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {

				initUIManager();

				ChessBoardMain frame = new ChessBoardMain();
				manager.setFrame(frame);

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}	

}
