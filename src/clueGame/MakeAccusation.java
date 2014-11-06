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

	private JComboBox<String> peopleCombo;
	private JComboBox<String> weaponCombo;
	private JComboBox<String> roomCombo;

	private ClueGame game;


	private class submitListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
				setVisible(false);
				game.finishTurn();
		}
	}

	private class cancelListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			setVisible(false);
		}
	}




	private void createLayout(){

		setLayout(new GridLayout(4, 2));

		JLabel roomLabel = new JLabel("Your room");
		add(roomLabel);
		roomCombo=drawRoomCombo(rooms);
		add(roomCombo);

		JLabel personLabel = new JLabel("Person");
		add(personLabel);
		peopleCombo=drawPeopleCombo(people);
		add(peopleCombo);

		JLabel weaponLabel = new JLabel("Weapon");
		add(weaponLabel);
		weaponCombo=drawWeaponCombo(weapons);
		add(weaponCombo);

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

	public Solution getAccusation(){
		String weapon=(String)weaponCombo.getSelectedItem();
		String person=(String)peopleCombo.getSelectedItem();
		String room=(String)roomCombo.getSelectedItem();

		Solution accusation = new Solution(person, weapon, room);
		return accusation;
	}



	public MakeAccusation(ArrayList<Card> deck,ClueGame game){
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
