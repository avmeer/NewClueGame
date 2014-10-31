package clueGame;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class CardDisplay extends JPanel{
	public CardDisplay(ArrayList<Card> deck) {
		setLayout(new GridLayout(4, 1));
		setPreferredSize(new Dimension(100, 100));
		
		ArrayList<String> people = new ArrayList<String>();
		ArrayList<String> rooms = new ArrayList<String>();
		ArrayList<String> weapons = new ArrayList<String>();
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
		
		JPanel panel = drawPeoplePanel(people);
		add(panel);
		panel = drawWeaponsPanel(weapons);
		add(panel);
		panel = drawRoomsPanel(rooms);
		add(panel);
	}
	
	public JPanel drawPeoplePanel(ArrayList<String> people)	{	
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder (new EtchedBorder(), "People"));
		panel.setLayout(new GridLayout(3,0));
		for (String name : people) {
			panel.add(new JLabel(name));
		}
		return panel;

	}
	public JPanel drawWeaponsPanel(ArrayList<String> weapons)	{	
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder (new EtchedBorder(), "Weapons"));
		panel.setLayout(new GridLayout(3,0));
		for (String name : weapons) {
			panel.add(new JLabel(name));
		}
		return panel;

	}
	public JPanel drawRoomsPanel(ArrayList<String> rooms)	{	
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder (new EtchedBorder(), "Rooms"));
		panel.setLayout(new GridLayout(3,0));
		for (String name : rooms) {
			panel.add(new JLabel(name));
		}
		return panel;

	}
	
	
}
