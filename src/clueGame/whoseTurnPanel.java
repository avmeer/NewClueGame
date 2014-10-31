package clueGame;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class whoseTurnPanel extends JPanel {

	public whoseTurnPanel(){
		JLabel whoseTurn = new JLabel("Whose turn?");
		JTextField whoseTurnField = new JTextField(20);
		add(whoseTurn);
		add(whoseTurnField);
	}
	
}
