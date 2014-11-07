package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class Board extends JPanel implements MouseListener{

	private Map<BoardCell, LinkedList<BoardCell>> adjMtx;
	private Set<BoardCell> targets;
	private ArrayList<BoardCell> visited;
	private BoardCell current;
	private ArrayList<Player> players;
	private static String layoutFile;
	private BoardCell[][] board;
	private Map<Character,String> rooms;
	private HumanPlayer humanPlayer;
	private boolean humanPlayerMustFinish=false;
	private ClueGame theGame;


	public boolean isHumanPlayerMustFinish() {
		return humanPlayerMustFinish;
	}

	public void setHumanPlayerMustFinish(boolean humanPlayerMustFinish) {
		this.humanPlayerMustFinish = humanPlayerMustFinish;
	}

	public HumanPlayer getHumanPlayer() {
		return humanPlayer;
	}

	public void setHumanPlayer(HumanPlayer humanPlayer) {
		this.humanPlayer = humanPlayer;
	}

	int numRows;
	int numColumns;



	public Board(String lF) {
		layoutFile = lF;
		targets = new HashSet<BoardCell>();
		addMouseListener(this);
	}
	
	public Board(String lF, ClueGame game) {
		layoutFile = lF;
		theGame=game;
		targets = new HashSet<BoardCell>();
		addMouseListener(this);
	}

	public void loadBoardConfig() throws BadConfigFormatException, FileNotFoundException{

		ArrayList<ArrayList<String>> tempBoard = new ArrayList<ArrayList<String>>();
		Scanner boardRead = new Scanner( new FileReader(layoutFile));

		numRows = 0;
		while(boardRead.hasNextLine()) {

			tempBoard.add(new ArrayList<String>());
			String readLine = boardRead.nextLine();
			String[] letters = readLine.split(",");

			for(int i = 0; i < letters.length; i++) {
				tempBoard.get(numRows).add(letters[i]);
			}

			if(numColumns != 0 && numColumns != letters.length){
				boardRead.close();
				throw new BadConfigFormatException("Row lengths are inconsistant in layout config file.");
			} else {
				numColumns = letters.length;
			}

			numRows++;
		}
		boardRead.close();

		board = new BoardCell[numRows][numColumns];

		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < numColumns; j++) {
				if(i > tempBoard.size()) {
					throw new BadConfigFormatException("Temp board row count doesn't match config file");
				}
				if(j > tempBoard.get(0).size()) {
					throw new BadConfigFormatException("Temp board column count doesn't match config file");
				}
				if(!rooms.containsKey(tempBoard.get(i).get(j).charAt(0))) {
					throw new BadConfigFormatException("Found undefined key in layout file");
				}

				switch(tempBoard.get(i).get(j)) {
				case "W":
					board[i][j] = new WalkwayCell(i, j);
					break;
				default:
					board[i][j] = new RoomCell(i, j, tempBoard.get(i).get(j));
					break;
				}
			}
		}


	}


	public void setRooms(Map<Character,String> inRooms) throws BadConfigFormatException {
		rooms = new HashMap<Character,String>();
		if(inRooms == null){
			throw new BadConfigFormatException();
		} else {
			rooms = inRooms;
		}
	}

	public BoardCell[][] getBoard() {
		return board;
	}

	public Map<Character, String> getRooms() {
		return rooms;
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public RoomCell getRoomCellAt(int r, int c) {
		return (RoomCell) board[r][c];
	}

	public BoardCell getCellAt(int r, int c) {
		return board[r][c];
	}


	public WalkwayCell getWalkwayCellAt(int r, int c) {
		return (WalkwayCell) board[r][c];
	}

	public void calcAdjacencies() {
		adjMtx = new HashMap<BoardCell, LinkedList<BoardCell>>();
		for(int i=0;i<numRows;i++) {
			for(int j=0;j<numColumns;j++) {
				BoardCell Up=null,Down=null,Left=null,Right=null,Current;
				if(i>0)
					Up=board[i-1][j];
				if(i<numRows-1)
					Down=board[i+1][j];
				if(j>0)
					Left=board[i][j-1];
				if(j<numColumns-1)
					Right=board[i][j+1];
				Current=board[i][j];


				LinkedList<BoardCell> adjList = new LinkedList<BoardCell>();
				if(Current.isDoorway()){
					switch(((RoomCell) Current).getDoorDirection()) {
					case UP:
						adjList.add(Up);
						break;
					case DOWN:
						adjList.add(Down);
						break;
					case LEFT:
						adjList.add(Left);
						break;
					case RIGHT:
						adjList.add(Right);
						break;
					case NONE:
						break;
					}
				} else if (Current.isWalkway()) {
					if(i>0 && Up.isWalkway()){
						adjList.add(Up);
					} else if (i>0 && Up.isDoorway()){
						if( ((RoomCell) Up).getDoorDirection()==RoomCell.DoorDirection.DOWN) {
							adjList.add(Up);
						}
					}
					if(i<numRows-1 && Down.isWalkway()){
						adjList.add(Down);
					} else if (i<numRows-1 && Down.isDoorway()){
						if( ((RoomCell) Down).getDoorDirection()==RoomCell.DoorDirection.UP) {
							adjList.add(Down);
						}
					}
					if(j>0 && Left.isWalkway()){
						adjList.add(Left);
					} else if (j>0 && Left.isDoorway()){
						if( ((RoomCell) Left).getDoorDirection()==RoomCell.DoorDirection.RIGHT) {
							adjList.add(Left);
						}
					}
					if(j<numColumns-1 && Right.isWalkway()){
						adjList.add(Right);
					} else if (j<numColumns-1 && Right.isDoorway()){
						if( ((RoomCell) Right).getDoorDirection()==RoomCell.DoorDirection.LEFT) {
							adjList.add(Right);
						}
					}
				} else {

				}
				adjMtx.put(Current, adjList);
			}
		}

	}

	public void calcTargets(int row, int col, int steps) {
		targets.clear();
		current = board[row][col];
		visited = new ArrayList<BoardCell>();
		if(steps > 0 ) {
			targetHelper(current.getRow(), current.getColumn(), steps);
		}

	}

	public void targetHelper(int row, int col, int steps) {
		if(steps == 0) {
			if (!targets.contains(board[row][col])) {
				targets.add(board[row][col]);
			}
		} else if(board[row][col].isDoorway() && board[row][col]!=current) {
			if (!targets.contains(board[row][col])) {
				targets.add(board[row][col]);
			}
		} else { 
			LinkedList<BoardCell> nextList = adjMtx.get(board[row][col]);
			for(BoardCell cell : nextList) {
				if(!visited.contains(cell) && !cell.equals(current)){
					visited.add(cell);
					targetHelper(cell.getRow(),cell.getColumn(),steps-1);
				}
				visited.remove(cell);
			}
		}


	}

	public Set<BoardCell> getTargets() {
		targets.remove(current);
		return targets;
	}

	public LinkedList<BoardCell> getAdjList(int row, int col) {
		return adjMtx.get(board[row][col]);
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBoardCells(g);
		drawPlayers(g);
	}

	public void drawPlayers(Graphics g){
		for(Player player : players) {
			player.draw(g);
		}
	}
	public void drawBoardCells(Graphics g){
		for(int row = 0; row < numRows; row++) {
			for(int col = 0; col < numColumns; col++) {
				board[row][col].draw(g, this);
			}
		}
	}
	public void highlightTargets(){
		for(BoardCell t: targets){
			t.setHighlighted(true);
		}
	}
	public void unHighlightTargets(){
		for(BoardCell t: targets){
			t.setHighlighted(false);
		}
	}

	public void mousePressed(MouseEvent e)  {
		if(humanPlayerMustFinish){
			for(BoardCell t: targets){
				if (t.containsClick(e.getX(), e.getY())) {
					humanPlayer.setRow(t.getRow());
					humanPlayer.setCol(t.getColumn());
					humanPlayerMustFinish=false;
					if(t.isDoorway()){
						RoomCell tempCell = (RoomCell)t;
						String roomName = theGame.getLegend().get(tempCell.getInitial());
						theGame.makeSuggestion(roomName);
					}
					unHighlightTargets();
					this.repaint();
					return;
				}
			}
			JOptionPane.showMessageDialog(this,"This is not a target.");
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}



}
