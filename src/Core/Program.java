package Core;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Communication.rxtxNativeHelper;

public class Program {

	public static void main(String args[]) {

		rxtxNativeHelper.initialize();
		initUIManager();
		
		Manager manager = new Manager();

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {

				manager.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				manager.getFrame().setVisible(true);
				
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
