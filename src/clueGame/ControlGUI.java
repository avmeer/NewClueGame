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
	
	
    private ClueGame game;
    private JPanel dP,gP,gR,whoseTr;
    
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
    		game.makeAccusation();
    	}
    }
    
    
	private void createLayout(){
		
		setLayout(new GridLayout(2,3));
		
		whoseTr = new whoseTurnPanel();
		add(whoseTr);
		
		JButton  nextPlayerButton= new JButton("Next Player");
		add(nextPlayerButton);
		nextPlayerButton.addActionListener(new NextPlayerButtonListener());
		
		JButton makeAccusationButton = new JButton("Make accusation");
		add(makeAccusationButton);
		makeAccusationButton.addActionListener(new makeAccusationButtonLister());
		
		dP = new diePanel();
		dP.setBorder(new TitledBorder (new EtchedBorder(), "Die"));

		add(dP);
		
		gP = new guessPanel();
		gP.setBorder(new TitledBorder (new EtchedBorder(), "Guess"));

		add(gP);
	
		gR = new guessResultPanel();
		gR.setBorder(new TitledBorder (new EtchedBorder(), "Guess Result"));

		add(gR);
		
		
	}
	
	public void setWhoseTurn(String playerName){
		((whoseTurnPanel) whoseTr).setWhoseTurnField(playerName);
	}
	public void setDieField(String roll){
		((diePanel) dP).setDieField(roll);
	}
	public void setGuess(String guess){
		((guessPanel) gP).setGuessField(guess);
	}
	public void setGuessResult(String result){
		((guessResultPanel)gR).setResponseField(result);
	}

	public ControlGUI(ClueGame game)
	{
		// EXIT_ON_CLOSE is static int
		this.game=game;
		this.setPreferredSize(new Dimension(800, 140));
		createLayout();
	}

}
