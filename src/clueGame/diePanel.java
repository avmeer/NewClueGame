package clueGame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class diePanel extends JPanel {
	private JTextField roll;
	public diePanel(){
		setLayout(new GridLayout(1, 2));
		JLabel die = new JLabel("Roll");
		roll = new JTextField(20);
		roll.setEditable(false);
		add(die, BorderLayout.NORTH);
		add(roll, BorderLayout.NORTH);
	}
	
	public void setDieField(String rollIn) {
		roll.setText(rollIn);
	}
}
