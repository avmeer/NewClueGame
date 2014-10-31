package clueGame;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

public class CardDisplay extends JPanel{
	public CardDisplay(ArrayList<Card> deck) {
		setLayout(new GridLayout(1, 3));
		
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
	}
}
