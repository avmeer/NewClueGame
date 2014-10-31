package clueGame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class ClueGame extends JFrame {
	private Map<Character,String> rooms;
	private Board theBoard;
	private String layoutFile;
	private String configFile;
	private String playerConfigFile;
	private String deckConfigFile;
	private ArrayList<Player> players;
	private ArrayList<Card> deck;
	private ArrayList<Card> seen;
	private Solution solution;
	public static final int CELL_SIZE = 26;
	private DetectiveNotes dN;

	public ClueGame()
	{
		this("ClueLayout.csv","ClueLegend.txt","CluePlayers.txt","Deck.txt");
	}

	public ClueGame(String layout, String legend)
	{
		this(layout, legend,"CluePlayers.txt","Deck.txt");
	}
	public ClueGame(String layout, String legend, String playerConfig, String deckConfig)
	{
		rooms = new HashMap<Character, String>();
		layoutFile = layout;
		configFile = legend;
		playerConfigFile = playerConfig;
		deckConfigFile = deckConfig;
		theBoard = new Board(layout);
		players = new ArrayList<Player>();
		deck = new ArrayList<Card>();
		seen = new ArrayList<Card>();
		add(theBoard,BorderLayout.CENTER);
		setSize(800, 800);
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(createFileMenu());
	}

	public void loadRoomConfig() throws FileNotFoundException, BadConfigFormatException {
		String line;
		String[] splitLine;
		Character tempKey;
		String tempValue;

		Scanner scan = new Scanner(new FileReader(configFile));

		while(scan.hasNextLine()) {
			line = scan.nextLine();
			splitLine = line.split(",");


			if(splitLine.length != 2) {
				scan.close();
				throw new BadConfigFormatException("Bad legend line formatting.");
			}
			if(splitLine[0].length() > 1) {
				scan.close();
				throw new BadConfigFormatException("Room Symbol is too long.");
			}
			tempKey =  line.charAt(0);
			tempValue = splitLine[1];

			tempValue = tempValue.trim();
			rooms.put(tempKey,tempValue);				
		}

		scan.close();
		theBoard.setRooms(rooms);
	}
	public void loadDeckConfig() throws FileNotFoundException, BadConfigFormatException {
		String line;
		String[] splitLine;

		Scanner scan = new Scanner(new FileReader(deckConfigFile));
		while(scan.hasNextLine()) {
			line = scan.nextLine();
			splitLine = line.split(",");
			//read room cards
			if(splitLine[1].equalsIgnoreCase("R")){
				deck.add(new Card(splitLine[0], CardType.ROOM));
			}
			//read person cards
			if(splitLine[1].equalsIgnoreCase("P")){
				deck.add(new Card(splitLine[0], CardType.PERSON));
			}
			//read weapon cards
			if(splitLine[1].equalsIgnoreCase("W")){
				deck.add(new Card(splitLine[0], CardType.WEAPON));
			}
		}
		scan.close();
	}

	public void loadPlayerConfig() throws FileNotFoundException, BadConfigFormatException {
		String line;
		String[] splitLine;

		Scanner scan = new Scanner(new FileReader(playerConfigFile));

		boolean isFirstRound = true;
		while(scan.hasNextLine()) {
			line = scan.nextLine();
			splitLine = line.split(",");

			if(splitLine.length != 4) {
				scan.close();
				throw new BadConfigFormatException("Bad player line formatting.");
			}

			if (isFirstRound) {
				players.add(new HumanPlayer(splitLine[0], splitLine[1], Integer.valueOf(splitLine[2]), Integer.valueOf(splitLine[3])));
			} else {
				players.add(new ComputerPlayer(splitLine[0], splitLine[1], Integer.valueOf(splitLine[2]), Integer.valueOf(splitLine[3])));
			}

			isFirstRound = false;
		}
		scan.close();
	}



	public void loadConfigFiles() {

		try {
			loadRoomConfig();
			theBoard.loadBoardConfig();
			loadPlayerConfig();
			loadDeckConfig();
		} catch (FileNotFoundException | BadConfigFormatException e) {

			//
		}
	}

	public void deal(){
		HashSet<Integer> dealt = new HashSet<Integer>();
		int random = (int)(Math.random()*21);
		for(int i = 0; i < deck.size(); i++) {
			while (dealt.contains(random)) {
				random = (int)(Math.random()*21);
			}
			dealt.add(random);
			players.get(i % players.size()).dealCard(deck.get(random));
		}
	}

	public Card handleSuggestion(Player suggestee, String person, String room, String weapon) {
		for(int i = players.indexOf(suggestee) + 1; i < players.indexOf(suggestee) + players.size(); i++) {
			Card disprove = players.get(i % players.size()).disproveSuggestion(person, room, weapon);
			if (disprove != null) {
				return disprove;
			}
		}

		//No one can disprove so return null
		return null;

	}

	public void seenCard(Card card) {
		seen.add(card);
	}

	public boolean checkAccusation(Solution accusation) {
		return accusation.equals(solution);
	}

	public Board getBoard() {
		return theBoard;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public ArrayList<Card> getDeck() {
		return deck;
	}

	public ArrayList<Card> getSeen() {
		return seen;
	}

	public Map<Character,String> getLegend() {
		return rooms;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}

	public DetectiveNotes getNotes() {
		return dN;
	}

	public void setNotes(DetectiveNotes dN) {
		this.dN = dN;
	}

	private JMenu createFileMenu() {
		JMenu menu = new JMenu("File"); 
		menu.add(createDetectiveNotesItem());
		menu.add(createFileExitItem());
		return menu;
	}

	private JMenuItem createFileExitItem() {
		JMenuItem item = new JMenuItem("Exit");
		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		}
		item.addActionListener(new MenuItemListener());
		return item;
	}

	private JMenuItem createDetectiveNotesItem() {
		JMenuItem item = new JMenuItem("Show Detective Notes");
		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e)
			{
				dN.setVisible(true);
			}
		}
		item.addActionListener(new MenuItemListener());
		return item;
	}

	public static void main(String[] args) {
		/*		ClueGame game = new ClueGame();
		Board board = game.getBoard();
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setVisible(true);*/

		ClueGame game = new ClueGame("ourBoardLayout.csv", "ourLegend.csv");
		game.loadConfigFiles();
		Board board = game.getBoard();
		board.calcAdjacencies();
		game.deal();

		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setVisible(true);
		// This will cause rectangle to display in new location
		board.setPlayers(game.getPlayers());
		board.repaint();

		game.setNotes(new DetectiveNotes(game.getDeck()));
		game.getNotes().setVisible(false);
		//JOptionPane.showMessageDialog(game,"You are "+game.getPlayers().get(0).getName()+", press Next Player to begin play","Welcome to Clue",JOptionPane.INFORMATION_MESSAGE);
	}

}
