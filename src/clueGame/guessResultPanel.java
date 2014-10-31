package clueGame;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class guessResultPanel extends JPanel {

	public guessResultPanel(){
		JLabel guessResult = new JLabel("Response");
		JTextField response = new JTextField(20);
		response.setEditable(false);
		add(guessResult);
		add(response);
	}
}
