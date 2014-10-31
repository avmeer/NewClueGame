package clueGame;

public class HumanPlayer extends Player {
	public HumanPlayer(String name, String color, int row, int col) {
		super(name, color, row, col);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void makeMove(Board theBoard) {
		int roll=(int)(Math.random() *5)+1;
		theBoard.calcTargets(this.getRow(),this.getCol(), roll);	
		for(BoardCell t: theBoard.getTargets()){
			t.setHighlighted(true);
		}
		theBoard.repaint();
	}


}
