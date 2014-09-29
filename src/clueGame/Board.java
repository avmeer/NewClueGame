package clueGame;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.Scanner;

import clue.BoardCell;
import clue.IntBoard;

public class Board {
	private BoardCell[][] layout;
	Map<Character,String> rooms;
	int numRows;
	int numColumns;
	
	
	public Board()
	{
		
	}
	
	public void loadBoardConfig(){
				
		FileReader fr = null;
		Scanner sc = null;
		String line = "";
		String comma = ",";
		String[] splitStrings;
		
		try
		{
			fr = new FileReader("boardLayout.txt");
			System.out.println("got file");
		}
		catch(FileNotFoundException e)
		{
			System.out.println(e);
		}
				
		sc = new Scanner(fr);
		
		while(sc.hasNextLine())
		{
			numColumns = 0;
			
			line = sc.nextLine();
			splitStrings = line.split(comma);
			
			for(int i = 0; i < splitStrings.length; i++)
			{
				//layout[numRows][numColumns] = splitStrings[i];
				System.out.println("befor null");
				//layout[numRows][numColumns] = IntBoard.getCell(numRows,numColumns);
				
				
				numColumns ++;
			}
			
			
			
			numRows++;
			
			
			
		}
		
		
		
		
	}

	public BoardCell[][] getLayout() {
		return layout;
	}

	public Map<Character, String> getRooms(char room) {
		return rooms;
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumColumns() {
		return numColumns;
	}
	
	public RoomCell getBoardCell(int r, int c)
	{
		
		RoomCell temp = new RoomCell();
		
		return temp;
	}
	
	
	public static void main(String[] args)
	{
		
		Board a = new Board();
		
		a.loadBoardConfig();
		
		//System.out.println("tj");
		
	}
	
	
	
}
