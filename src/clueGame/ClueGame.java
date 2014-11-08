package clueGame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ClueGame extends JFrame  implements ComponentListener  {
	private Map<Character,String> rooms;
	private Board theBoard;
	private String layoutFile;
	private String configFile;
	private String playerConfigFile;
	private String deckConfigFile;
	private ArrayList<Player> players;
	private ArrayList<Card> deck;
	private ArrayList<Card> seen;
	private Solution solution = null;
	public static int CELL_SIZE = 26;
	private DetectiveNotes dN;
	private HumanPlayer humanPlayer;
	private int currentTurn=0;

	private ControlGUI controlGUI;

	private CardDisplay cardDisplay;

	private MakeAccusation accusationPanel;

	private MakeSuggestion suggestionPanel;

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
		theBoard = new Board(layout, this);
		players = new ArrayList<Player>();
		deck = new ArrayList<Card>();
		seen = new ArrayList<Card>();
		add(theBoard,BorderLayout.CENTER);
		setSize(700, 800);
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(createFileMenu());
		this.addComponentListener(this);

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
				humanPlayer = new HumanPlayer(splitLine[0], splitLine[1], Integer.valueOf(splitLine[2]), Integer.valueOf(splitLine[3]));
				players.add(humanPlayer);
			} else {
				players.add(new ComputerPlayer(splitLine[0], splitLine[1], Integer.valueOf(splitLine[2]), Integer.valueOf(splitLine[3])));
			}

			isFirstRound = false;
		}
		scan.close();
		
		for(Player p: players){
			p.setPlayers(players);
		}
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
		int random = (int)(Math.random()*deck.size());
		for(int i = 0; i < deck.size(); i++) {
			while (dealt.contains(random)) {
				random = (int)(Math.random()*deck.size());
			}
			dealt.add(random);
			if(solution!=null){
				if(!(deck.get(random).name.equals(solution.person)||deck.get(random).name.equals(solution.weapon)||deck.get(random).name.equals(solution.room)))
					players.get(i % players.size()).dealCard(deck.get(random));
			}
			else{
				players.get(i % players.size()).dealCard(deck.get(random));
			}
		}
	}

	public Card handleSuggestion(Player suggestee, String person, String room, String weapon) {
		
		for(Player p: players){
			if(p.getName().equalsIgnoreCase(person)){
				p.setCol(suggestee.getCol());
				p.setRow(suggestee.getRow());
				repaint();
			}
		}
		
		
		for(int i = players.indexOf(suggestee) + 1; i < players.indexOf(suggestee) + players.size(); i++) {
			Card disprove = players.get(i % players.size()).disproveSuggestion(person, room, weapon);
			if (disprove != null) {
				if(controlGUI!=null)
					controlGUI.setGuessResult(disprove.name);
				if(!seen.contains(disprove)){
					seen.add(disprove);
				}
				return disprove;
			}
		}
		
		
		if(controlGUI!=null)
			controlGUI.setGuessResult("No new clue");
		//No one can disprove so return null
		return null;

	}
	public void setGuess(String guess){
		controlGUI.setGuess(guess);
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
	public void nextPlayer(){
		Player currentPlayer=players.get(currentTurn);
		if(!theBoard.isHumanPlayerMustFinish()){
			controlGUI.setWhoseTurn(currentPlayer.getName());

			//clear Guess panel
			controlGUI.setGuess("");
			controlGUI.setGuessResult("");

			currentTurn++;
			if(currentTurn>players.size()-1){
				currentTurn=0;
			}
			if(currentPlayer==humanPlayer){
				theBoard.setHumanPlayerMustFinish(true);
			}
			int roll=(int)(Math.random() *5)+1;
			currentPlayer.makeMove(this,theBoard,roll);
			controlGUI.setDieField(Integer.toString(roll));
		}
		else{
			JOptionPane.showMessageDialog(this,"You must complete your turn");
		}
	}

	public void makeAccusation(){
		if(theBoard.isHumanPlayerMustFinish()){
			accusationPanel= new MakeAccusation(getDeck(),this);
			accusationPanel.setVisible(true);
		}
		else{
			JOptionPane.showMessageDialog(this,"It is not your turn!");
		}
	}
	public void makeSuggestion(String currentRoom){
		suggestionPanel= new MakeSuggestion(currentRoom,deck,this);
		suggestionPanel.setVisible(true);
	}

	public void finishSuggestion(){
		Solution tempSuggestion = suggestionPanel.getSuggestion();
		Card tempCard = handleSuggestion(humanPlayer, tempSuggestion.person,  tempSuggestion.room,tempSuggestion.weapon);
		setGuess(tempSuggestion.person+" "+ tempSuggestion.room+ " " +tempSuggestion.weapon);
	}

	public void finishAccusation(){
		if(theBoard.isHumanPlayerMustFinish()){
			theBoard.setHumanPlayerMustFinish(false);
			theBoard.unHighlightTargets();
			if(checkAccusation(accusationPanel.getAccusation())){
				JOptionPane.showMessageDialog(this,"Correct Accusation! You win!");
			}
			else{
				JOptionPane.showMessageDialog(this,"Incorrect Accusation!");
			}

			currentTurn++;
		}
	}


	public void createSolution () {
		int random = (int)(Math.random() * deck.size());
		String person = null;
		String room = null;
		String weapon = null;
		//Select randomly from cards 
		while(person == null) {
			Card card = deck.get(random);
			//Find the guess for person
			if (person == null && card.type == CardType.PERSON) {
				person = card.name;
			} 
			random = (int)(Math.random() * (deck.size()));
		}
		while( weapon == null){
			Card card = deck.get(random);

			//Find guess for weapon
			if (weapon == null && card.type == CardType.WEAPON) {
				weapon = card.name;
			}
			random = (int)(Math.random() * (deck.size()));
		}

		while( room == null){
			Card card = deck.get(random);

			//Find guess for weapon
			if (room == null && card.type == CardType.ROOM) {
				room = card.name;
			}
			random = (int)(Math.random() * (deck.size()));
		}
		solution=new Solution(person,weapon,room);

	}

	public static void main(String[] args) {

		ClueGame game = new ClueGame("ourBoardLayout.csv", "ourLegend.csv");
		game.loadConfigFiles();
		game.controlGUI= new ControlGUI(game);
		game.add(game.controlGUI,BorderLayout.SOUTH);


		Board board = game.getBoard();
		board.calcAdjacencies();
		game.createSolution();
		game.deal();
		game.cardDisplay = new CardDisplay(game.humanPlayer.getHand());
		game.add(game.cardDisplay,BorderLayout.EAST);

		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setVisible(true);
		// This will cause rectangle to display in new location
		board.setPlayers(game.getPlayers());
		board.repaint();

		game.setNotes(new DetectiveNotes(game.getDeck()));
		game.getNotes().setVisible(false);
		
		
		JOptionPane.showMessageDialog(game,"You are "+game.humanPlayer.getName()+", press Next Player to begin play","Welcome to Clue",JOptionPane.INFORMATION_MESSAGE);
		board.setHumanPlayer(game.humanPlayer);
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		int horizontalSize,verticalSize;
		horizontalSize=(getWidth()-cardDisplay.getWidth())/(theBoard.getNumColumns()+1);
		verticalSize=(this.getHeight()-controlGUI.getHeight())/(theBoard.getNumRows()+2);
		if(verticalSize>horizontalSize){
			CELL_SIZE=horizontalSize;
		}
		else{
			CELL_SIZE=verticalSize;
		}
		
		repaint();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

}
