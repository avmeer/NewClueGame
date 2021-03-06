package clueGame;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

public class ComputerPlayer extends Player {
	public ComputerPlayer(String name, String color, int row, int col) {
		super(name, color, row, col);
		// TODO Auto-generated constructor stub
	}

	private char lastRoomVisited;
	
	private boolean makeAccusation=false;
	private Solution answer;

	public BoardCell pickLocation (Set<BoardCell> targets) {
		ArrayList<BoardCell> roomCells = new ArrayList<BoardCell>();
		for (BoardCell cell : targets) {
			if (cell.isDoorway()) {
				if (((RoomCell)cell).getInitial() != this.lastRoomVisited) {
					//If room is not last room visited, it must be the target so return it
					this.lastRoomVisited = ((RoomCell)cell).getInitial();
					return cell;
				} else {
					//If the room is last room visited, it cannot be a target so add to removal list
					roomCells.add(cell);
				}
			}
		}
		
		//remove any doors that shouldn't be targets
		for (BoardCell cell : roomCells) {
			targets.remove(cell);
		}
		int random = (int)(Math.random() * targets.size());
		
		for (BoardCell cell : targets) {
			if (random == 0) {
				return cell;
			}
			random--;
		}
		return null;
	}

	public Solution createSuggestion (ArrayList<Card> seen, ArrayList<Card> deck, Map<Character,String> legend) {
		int random = (int)(Math.random() * deck.size());
		String person = null;
		String room = null;
		String weapon = null;
		//Select randomly from cards we have not seen
		while(person == null) {
			Card card = deck.get(random);
			//Find the guess for person
			if (person == null && card.type == CardType.PERSON) {
				if (!seen.contains(card) && !hand.contains(card)) {
					person = card.name;
				}
			} 
			random = (int)(Math.random() * (deck.size()));
		}
		while( weapon == null){
			Card card = deck.get(random);

			//Find guess for weapon
			if (weapon == null && card.type == CardType.WEAPON) {
				if (!seen.contains(card) && !hand.contains(card)) {
					weapon = card.name;
				}
			}
			random = (int)(Math.random() * (deck.size()));
		}
		
		//Set last visited room
		room = legend.get(lastRoomVisited);
		return new Solution(person, weapon, room);
	}

	public char getLastRoomVisited() {
		return lastRoomVisited;
	}

	public void setLastRoomVisited(char lastRoomVisited) {
		this.lastRoomVisited = lastRoomVisited;
	}

	@Override
	public void makeMove(ClueGame game, Board theBoard,int roll) {
		Solution suggestion;
		
		
		if(makeAccusation){
			if(game.checkAccusation(answer)){

				JOptionPane.showMessageDialog(game,this.getName()+"  wins!");
			}
		}
		
		theBoard.calcTargets(this.getRow(),this.getCol(), roll);
		BoardCell target=pickLocation(theBoard.getTargets());
		this.setRow(target.getRow());
		this.setCol(target.getColumn());
		
		if(target.isDoorway()){
			Card tempCard = null;
			suggestion=createSuggestion(game.getSeen(),game.getDeck(),game.getLegend());
			game.setGuess(suggestion.person+" "+ suggestion.room+ " " +suggestion.weapon);
			
			
			tempCard=game.handleSuggestion(this, suggestion.person, suggestion.room, suggestion.weapon);
			if(tempCard==null){
				answer=suggestion;
				makeAccusation=true;
			}
		}
		
		
		
		theBoard.repaint();
		
	}
}
