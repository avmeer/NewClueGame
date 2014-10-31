package clueGame;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class guessPanel extends JPanel {
	
	public guessPanel(){
		JPanel Guess = new JPanel();
		JTextField guess = new JTextField(20);
		JLabel guessResult = new JLabel("Guess");
		guess.setEditable(false);
		add(guessResult);
		add(guess);
	}
}
