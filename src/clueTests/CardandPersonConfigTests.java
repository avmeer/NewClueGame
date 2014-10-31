package clueTests;

import static org.junit.Assert.*;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import clueGame.BadConfigFormatException;
import clueGame.Board;
import clueGame.BoardCell;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ClueGame;
import clueGame.ComputerPlayer;
import clueGame.Player;
import clueGame.Solution;

public class CardandPersonConfigTests {

		private static ClueGame game;
		private static Board board;
		private LinkedList<BoardCell> adjList;

		@BeforeClass
		public static void setup() {
			game = new ClueGame("ourBoardLayout.csv", "ourLegend.csv");
			game.loadConfigFiles();
			board = game.getBoard();
			board.calcAdjacencies();
			game.deal();
		}
		
		@Test 
		public void humanPlayerTest() {
			Player human = game.getPlayers().get(0);
			
			//Check that the human player's name is correct
			assertEquals(human.getName(), "Ted");
			
			//Check the human player's location is correct
			assertEquals(human.getRow(), 20);
			assertEquals(human.getCol(), 8);
			
			//Check the human player's color is correct
			assertEquals(human.getColor(), Color.black);
		}
		
		//Check the same info as human player for computer player 1
		@Test 
		public void computerPlayerTest1() {
			Player computer = game.getPlayers().get(1);
			assertEquals(computer.getName(), "Lily");
			assertEquals(computer.getRow(), 1);
			assertEquals(computer.getCol(), 7);
			assertEquals(computer.getColor(), Color.red);
		}
		
		//Check the same info for computer player 2
		@Test 
		public void computerPlayerTest2() {
			Player computer = game.getPlayers().get(5);
			assertEquals(computer.getName(), "Carl");
			assertEquals(computer.getRow(), 1);
			assertEquals(computer.getCol(), 20);
			assertEquals(computer.getColor(), Color.yellow);
		}
		
		@Test 
		public void deckCountTest() {
			ArrayList<Card> deck = game.getDeck();
			
			//Check that the deck has the correct number of cards
			assertEquals(deck.size(), 21);
			int weapon = 0;
			int room = 0;
			int person = 0;
			
			//Check that the deck has the correct number of each type of card
			for (Card card : deck) {
				if (card.type == CardType.PERSON) {
					person++;
				} else if (card.type == CardType.ROOM) {
					room++;
				} else if (card.type == CardType.WEAPON) {
					weapon++;
				}
			}
			assertEquals(weapon, 6);
			assertEquals(room, 9);
			assertEquals(person, 6);
		}
		
		@Test 
		public void deckContentsTest() {
			//Check that the deck does actually contain the correct cards by using three examples
			ArrayList<Card> deck = game.getDeck();
			assertTrue(deck.contains(new Card("Ted", CardType.PERSON)));
			assertTrue(deck.contains(new Card("Lounge", CardType.ROOM)));
			assertTrue(deck.contains(new Card("Knife", CardType.WEAPON)));
		}
		
		@Test 
		public void allCardsDealtTest() {
			ArrayList<Player> players = game.getPlayers();
			HashSet<Card> allCards = new HashSet<Card>();
			ArrayList<Card> deck = game.getDeck();
			int numDealt = 0;
			for(Player player : players) {
				numDealt += player.getHand().size();
				for(Card card : player.getHand()) {
					allCards.add(card);
				}
			}
			
			//Checks that every card was dealt
			for(Card card : deck) {
				assertTrue(allCards.contains(card));
			}
			
			//check the entire deck was dealt
			assertEquals(numDealt, 21);
		}
		
		@Test 
		public void noDuplicatesTest() {
			ArrayList<Player> players = game.getPlayers();
			HashSet<Card> allCards = new HashSet<Card>();
			for(Player player : players) {
				for(Card card : player.getHand()) {
					allCards.add(card);
				}
			}
			//The set can't contain duplicates so if the size is still 21, there are no duplicates
			assertEquals(allCards.size(), 21);
		}
		
		@Test 
		public void playersHaveSameNumCardsTest() {
			ArrayList<Player> players = game.getPlayers();
			ArrayList<Card> deck = game.getDeck();
			double amountCards = deck.size() / players.size();
			for(Player player : players) {
				// shows the number of cards in the hand is equal to average num cards plus or minus 1
				assertEquals(amountCards, player.getHand().size(), 1);
			}
		}
		
		@Test 
		public void accusationTest() {
			game.setSolution(new Solution("Ted", "Knife", "Lounge"));
			
			//Check that the correct accusation returns true
			assertTrue(game.checkAccusation(new Solution("Ted", "Knife", "Lounge")));
			
			//Check that various wrong accusations return false
			assertFalse(game.checkAccusation(new Solution("Robin", "Knife", "Lounge")));
			assertFalse(game.checkAccusation(new Solution("Ted", "Rope", "Lounge")));
			assertFalse(game.checkAccusation(new Solution("Ted", "Knife", "Bedroom")));
			assertFalse(game.checkAccusation(new Solution("Barney", "Wrench", "Garage")));
		}
		
		@Test 
		public void targetLocationNoDoorTest() {
			ComputerPlayer cp = new ComputerPlayer("Lily", "red", 2, 7);
			board.calcTargets(5, 6 , 2);
			
			//This room has two doors so check both of them for selections
			int loc_3_6 = 0;
			int loc_7_6 = 0;
			int loc_5_8 = 0;
			int loc_5_4 = 0;
			int loc_4_5 = 0;
			int loc_4_7 = 0;
			int loc_6_7 = 0;
			int loc_6_5 = 0;
			
			Set<BoardCell> targets = board.getTargets();
			for (int i = 0; i < 100; i++) {
				BoardCell target = cp.pickLocation(targets);
				if(target == board.getCellAt(3, 6)) {
					loc_3_6++;
				} else if (target == board.getCellAt(7, 6)) {
					loc_7_6++;
				} else if (target == board.getCellAt(5, 8)) {
					loc_5_8++;
				} else if (target == board.getCellAt(5, 4)) {
					loc_5_4++;
				} else if (target == board.getCellAt(4, 5)) {
					loc_4_5++;
				} else if (target == board.getCellAt(4, 7)) {
					loc_4_7++;
				} else if (target == board.getCellAt(6, 7)) {
					loc_6_7++;
				} else if (target == board.getCellAt(6, 5)) {
					loc_6_5++;
				}
			}
			
			//Make sure total count is 100
			assertEquals(100, loc_3_6 + loc_7_6 + loc_5_8 + loc_5_4 + loc_4_5 + loc_4_7 + loc_6_7 + loc_6_5);
			
			//Make sure each spot is picked at least 5 times
			assertTrue(5 < loc_3_6);
			assertTrue(5 < loc_7_6);
			assertTrue(5 < loc_5_8);
			assertTrue(5 < loc_5_4);
			assertTrue(5 < loc_4_5);
			assertTrue(5 < loc_4_7);
			assertTrue(5 < loc_6_7);
			assertTrue(5 < loc_6_5);
		}
		
		@Test 
		public void targetLocationIsDoorTest() {
			ComputerPlayer cp = new ComputerPlayer("Lily", "red", 2, 7);

			board.calcTargets(11, 6 , 2);
			
			//This room has two doors so check both of them for selections
			int top_door_105 = 0;
			int bot_door_115 = 0;
			Set<BoardCell> targets = board.getTargets();

			BoardCell target = cp.pickLocation(targets);
			//System.out.println(target.getRow() + ", " + target.getColumn());
			if(target == board.getCellAt(10, 5)) {
				top_door_105++;
			} else if (target == board.getCellAt(11, 5)) {
				bot_door_115++;
			}
			
			//It has to pick one of the doors so their sum must be 1
			assertEquals(1, bot_door_115 + top_door_105);
		}
		
		@Test 
		public void targetLocationIsDoorLastUsedTest() {
			ComputerPlayer cp = new ComputerPlayer("Lily", "red", 2, 7);
			board.calcTargets(11, 6 , 2);
			cp.setLastRoomVisited('K');
			
			//This room has two doors so check both of them for selections
			int loc_9_6 = 0;
			int loc_13_6 = 0;
			int loc_12_7 = 0;
			int loc_10_7 = 0;
			
			Set<BoardCell> targets = board.getTargets();
			for (int i = 0; i < 100; i++) {
				BoardCell target = cp.pickLocation(targets);
				if(target == board.getCellAt(9, 6)) {
					loc_9_6++;
				} else if (target == board.getCellAt(13, 6)) {
					loc_13_6++;
				} else if (target == board.getCellAt(12, 7)) {
					loc_12_7++;
				} else if (target == board.getCellAt(10, 7)) {
					loc_10_7++;
				}
			}
			
			//Make sure total count is 100
			assertEquals(100, loc_9_6 + loc_13_6 + loc_12_7 + loc_10_7);
			
			//Make sure each spot is picked at least 10 times
			assertTrue(10 < loc_9_6);
			assertTrue(10 < loc_13_6);
			assertTrue(10 < loc_12_7);
			assertTrue(10 < loc_10_7);
		}
		
		@Test 
		public void disproveSuggestionOnePlayerOneMatchTest() {
			ArrayList<Player> players = game.getPlayers();
			ArrayList<Card> deck = game.getDeck();
			
			//empty everyone's hand
			for (Player player : players) {
				player.setHand(new ArrayList<Card>());
			}
			
			//Add Ted
			players.get(0).dealCard(deck.get(12));
			//Add Robin
			players.get(0).dealCard(deck.get(13));
			//Add Garage
			players.get(0).dealCard(deck.get(0));
			//Add Kitchen
			players.get(0).dealCard(deck.get(1));
			//Add Rope
			players.get(0).dealCard(deck.get(19));
			//Add Wrench
			players.get(0).dealCard(deck.get(20));
			
			//Should show Ted
			assertEquals(players.get(0).disproveSuggestion("Ted", "Lounge", "Knife"), deck.get(12));
			//Should show Garage
			assertEquals(players.get(0).disproveSuggestion("Carl", "Garage", "Knife"), deck.get(0));
			//Should show Rope
			assertEquals(players.get(0).disproveSuggestion("Carl", "Lounge", "Rope"), deck.get(19));
			//Should show null
			assertEquals(players.get(0).disproveSuggestion("Carl", "Lounge", "Knife"), null);
		}
		
		@Test 
		public void disproveSuggestionOnePlayerMultipleMatchTest() {
			ArrayList<Player> players = game.getPlayers();
			ArrayList<Card> deck = game.getDeck();
			
			//empty everyone's hand
			for (Player player : players) {
				player.setHand(new ArrayList<Card>());
			}
			
			//Extra cards to ensure unwanted cards don't get returned
			//Add Ted
			players.get(0).dealCard(deck.get(12));
			//Add Robin
			players.get(0).dealCard(deck.get(13));
			//Add Garage
			players.get(0).dealCard(deck.get(0));
			//Add Kitchen
			players.get(0).dealCard(deck.get(1));
			//Add Rope
			players.get(0).dealCard(deck.get(19));
			//Add Wrench
			players.get(0).dealCard(deck.get(20));
			
			int countTed = 0;
			int countGarage = 0;
			int countRope = 0;
			
			for (int i = 0; i < 100; i++) {
				Card disprove = players.get(0).disproveSuggestion("Ted", "Garage", "Rope");
				if (disprove.equals(deck.get(12))) {
					countTed++;
				} else if (disprove.equals(deck.get(0))) {
					countGarage++;
				} else if (disprove.equals(deck.get(19))) {
					countRope++;
				}
			}
			
			//Ensure those were the only cards returned
			assertEquals(100, countTed + countGarage + countRope);
			
			//Ensure each response is picked a decent amount of times
			assertTrue(10 < countTed);
			assertTrue(10 < countGarage);
			assertTrue(10 < countRope);
		}
		
		@Test 
		public void disproveSuggestionAllPlayersRequired() {
			ArrayList<Player> players = game.getPlayers();
			ArrayList<Card> deck = game.getDeck();
			
			//empty everyone's hand
			for (Player player : players) {
				player.setHand(new ArrayList<Card>());
			}
			//Human hand
			//Add Ted
			players.get(0).dealCard(deck.get(12));
			//Add Garage
			players.get(0).dealCard(deck.get(0));
			//Add Wrench
			players.get(0).dealCard(deck.get(20));
			
			//Computer player hand
			//Add Robin
			players.get(1).dealCard(deck.get(13));
			//Add Kitchen
			players.get(1).dealCard(deck.get(1));
			//Add Rope
			players.get(1).dealCard(deck.get(19));
			
			//Computer player hand
			//Add Barney
			players.get(5).dealCard(deck.get(11));
			//Add Pool Room
			players.get(5).dealCard(deck.get(4));
			//Add Candlestick
			players.get(5).dealCard(deck.get(16));
			
			//Computer player hand
			//Add Marshall
			players.get(4).dealCard(deck.get(10));
			//Add Study
			players.get(4).dealCard(deck.get(3));
			//Add Lead Pipe
			players.get(4).dealCard(deck.get(17));
			
			//Ensure no one can disprove suggestion
			assertTrue((game.handleSuggestion(players.get(1), "Carl", "Lounge", "Knife")) == null);
			
			//Ensure only human can disprove suggestion
			//Also ensures player farthest from suggestee is used
			assertTrue((game.handleSuggestion(players.get(1), "Ted", "Lounge", "Knife")).equals(deck.get(12)));
			
			//Ensure no one can disprove suggestion except person who made suggestion
			assertTrue((game.handleSuggestion(players.get(1), "Robin", "Kitchen", "Rope")) == null);
			
			//Ensure closest player disproves in this case player 4 disproves by showing study
			assertTrue((game.handleSuggestion(players.get(1), "Barney", "Study", "Knife")).equals(deck.get(3)));
			
			//Ensure closest player disproves other player in this case player 1 disproves by showing kitchen
			assertTrue((game.handleSuggestion(players.get(5), "Marshall", "Kitchen", "Knife")).equals(deck.get(1)));
		}
		
		@Test 
		public void computerSuggestionTest() {
			ArrayList<Player> players = game.getPlayers();
			ArrayList<Card> deck = game.getDeck();
			
			//Put player in Kitchen
			((ComputerPlayer)(players.get(2))).setLastRoomVisited('K');
		
			//Computer player hand
			//Add Robin
			players.get(2).dealCard(deck.get(13));
			//Add Kitchen
			players.get(2).dealCard(deck.get(1));
			//Add Lounge
			players.get(2).dealCard(deck.get(2));
			//Add Rope
			players.get(2).dealCard(deck.get(19));
			
			
			//Seen Cards
			//Add Barney
			game.seenCard(deck.get(11));
			//Add Pool Room
			game.seenCard(deck.get(4));
			//Add Candlestick
			game.seenCard(deck.get(16));
			//Add Marshall
			game.seenCard(deck.get(10));
			//Add Study
			game.seenCard(deck.get(3));
			//Add Lead Pipe
			game.seenCard(deck.get(17));
			
			Solution suggestion = ((ComputerPlayer)(players.get(2))).createSuggestion(game.getSeen(), game.getDeck(), game.getLegend());
			
			//Makes sure the cards in the player hand are not in suggestion
			//Kitchen can be in suggestion because it is the room the player is in
			assertNotEquals(suggestion.person, deck.get(13).name);
			assertNotEquals(suggestion.weapon, deck.get(19).name);
			assertNotEquals(suggestion.room, deck.get(2).name);
			
			//Check that room is correct
			assertEquals(suggestion.room, deck.get(1).name);
			
			//Makes sure the cards that have been seen are not in suggestion
			//Kitchen can be in suggestion because it is the room the player is in
			assertNotEquals(suggestion.person, deck.get(11).name);
			assertNotEquals(suggestion.weapon, deck.get(16).name);
			assertNotEquals(suggestion.room, deck.get(4).name);
			assertNotEquals(suggestion.person, deck.get(10).name);
			assertNotEquals(suggestion.weapon, deck.get(17).name);
			assertNotEquals(suggestion.room, deck.get(3).name);
			
			
		}

}