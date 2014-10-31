package clueGame;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class whoseTurnPanel extends JPanel {
	private JTextField whoseTurnField;
	public whoseTurnPanel(){
		JLabel whoseTurn = new JLabel("Whose turn?");
		whoseTurnField = new JTextField(20);
		add(whoseTurn);
		add(whoseTurnField);
	}
	public JTextField getWhoseTurnField() {
		return whoseTurnField;
	}
	public void setWhoseTurnField(String playerName) {
		whoseTurnField.setText(playerName);
	}
	
}
