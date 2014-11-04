package clueGame;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clueGame.Card;
import clueGame.CardType;


public class MakeAccusation extends JFrame {
	private ArrayList<String> people = new ArrayList<String>();
	private ArrayList<String> rooms = new ArrayList<String>();
	private ArrayList<String> weapons = new ArrayList<String>();
	
	 private ClueGame game;
	
	public boolean accusationMade=false;
	
	private class submitListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			accusationMade=true;
			setVisible(false);
			game.finishTurn();
			System.out.println("Button pressed");
		}
	}

	private class cancelListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("Button pressed");
		}
	}

	
	
	
	private void createLayout(){
		
		setLayout(new GridLayout(4, 2));
	
		JLabel roomLabel = new JLabel("Your room");
		add(roomLabel);
		add(drawRoomCombo(rooms));
		
		JLabel personLabel = new JLabel("Person");
		add(personLabel);
		add(drawPeopleCombo(people));
		
		JLabel weaponLabel = new JLabel("Weapon");
		add(weaponLabel);
		add(drawWeaponCombo(weapons));
		
		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(new submitListener());
		add(submitButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new cancelListener());
		add(cancelButton);

	}
	
	public JComboBox<String> drawPeopleCombo(ArrayList<String> people)	{
		JComboBox<String> combo = new JComboBox<String>();
		
		for (String name : people) {

			combo.addItem(name);
		}
		return combo;

	}
	
	public JComboBox<String> drawWeaponCombo(ArrayList<String> weapons)	{
		JComboBox<String> combo = new JComboBox<String>();
		
		for (String name : weapons) {

			combo.addItem(name);
		}
		return combo;

	}
	
	public JComboBox<String> drawRoomCombo(ArrayList<String> rooms)	{
		JComboBox<String> combo = new JComboBox<String>();
		
		for (String name : rooms) {

			combo.addItem(name);
		}
		return combo;

	}
	
	
	
	public MakeAccusation(ArrayList<Card> deck,ClueGame game){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Make An Accusation");
		setSize(300, 300);
		this.game=game;
		
		for (Card card : deck) {
			CardType type = card.type;
			switch (type) {
				case WEAPON: weapons.add(card.name);
					break;
				case PERSON:  people.add(card.name);
					break;
				case ROOM: rooms.add(card.name);
					break;
			}
		}
		
		createLayout();
	}

}
