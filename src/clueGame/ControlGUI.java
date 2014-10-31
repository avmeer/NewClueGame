package clueGame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class ControlGUI extends JPanel {
	
	
    private JTextField whoseTurnField, roll, guess, response;
    private ClueGame game;
    
    private class NextPlayerButtonListener implements ActionListener
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		game.nextPlayer();
    	}
    }
    
    private class makeAccusationButtonLister implements ActionListener
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		//TODO
    	}
    }
    
    
	private void createLayout(){
		
		setLayout(new GridLayout(2,3));
		
		JPanel whoseTr = new whoseTurnPanel();
		add(whoseTr);
		
		JButton  nextPlayerButton= new JButton("Next Player");
		add(nextPlayerButton);
		nextPlayerButton.addActionListener(new NextPlayerButtonListener());
		
		JButton makeAccusationButton = new JButton("Make accusation");
		add(makeAccusationButton);
		makeAccusationButton.addActionListener(new makeAccusationButtonLister());
		
		JPanel dP = new diePanel();
		dP.setBorder(new TitledBorder (new EtchedBorder(), "Die"));

		add(dP);
		
		JPanel gP = new guessPanel();
		gP.setBorder(new TitledBorder (new EtchedBorder(), "Guess"));

		add(gP);
	
		JPanel gR = new guessResultPanel();
		gR.setBorder(new TitledBorder (new EtchedBorder(), "Guess Result"));

		add(gR);
		
		
	}
	
	
	public JTextField getWhoseTurnField() {
		return whoseTurnField;
	}


	public void setWhoseTurnField(JTextField whoseTurnField) {
		this.whoseTurnField = whoseTurnField;
	}


	public JTextField getRoll() {
		return roll;
	}


	public void setRoll(JTextField roll) {
		this.roll = roll;
	}


	public JTextField getGuess() {
		return guess;
	}


	public void setGuess(JTextField guess) {
		this.guess = guess;
	}


	public JTextField getResponse() {
		return response;
	}


	public void setResponse(JTextField response) {
		this.response = response;
	}


	public ControlGUI(ClueGame game)
	{
		// EXIT_ON_CLOSE is static int
		this.game=game;
		this.setPreferredSize(new Dimension(800, 140));
		createLayout();
	}

}
