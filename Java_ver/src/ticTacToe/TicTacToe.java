/**
 * Date: July 9, 2021
 * Description: The game of Tic-Tac-Toe
 * @author Matt
 * 
 */

package ticTacToe;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// Driver class
public class TicTacToe
{
	final static byte LEN = 3;
	final static byte SIZE = LEN * LEN;
	
	static JFrame f;
	static JPanel p;
	static JLabel[][] btns;
	static Boolean[][] states;
	static boolean turn;
	
	// Initializing mouse listener
	final static MouseAdapter ml = new MouseAdapter()
	{
		@Override
		public void mouseClicked(MouseEvent me)
		{
			if (me.getButton() == MouseEvent.BUTTON1)
			{
				// Button was left clicked
				int[] indices = getBtnIndices(me.getSource());
				int count = 0;
				
				// Checking if button was already clicked
				if (states[indices[0]][indices[1]] != null)
					return;
				
				// Changing button state and image
				states[indices[0]][indices[1]] = turn;
				setImg(btns[indices[0]][indices[1]], turn ? "X" : "O");
				
				// Counting quantity of clicked buttons
				for (Boolean[] row : states)
					for (Boolean state : row)
						if (state != null)
							count++;
				
				// Checking if game is over
				if (matchMade())
				{
					// A player won
					gameOver((turn ? "X" : "O") + " won!");
				}
				else if (count == SIZE)
				{
					gameOver("Tie game");
				}
				else
				{
					// Switching turn
					turn = !turn;
				}
			}
		}
	};
	
	// Driver code
	public static void main(String[] args)
	{
		// Setting up window components
		SwingUtilities.invokeLater(() -> new TicTacToe());
	}
	
	// Constructor
	public TicTacToe()
	{
//		getTurn();
		turn = true;
		
		newGame();
	}
	
	// Ask user who will go first
	public static void getTurn()
	{
		JFrame 	f = new JFrame();
		JPanel 	p = new JPanel();
		JButton x = new JButton();
		JButton o = new JButton();
		
		// Setting up buttons
		x.setIcon(new ImageIcon("src/ticTacToe/X.jpg"));
		o.setIcon(new ImageIcon("src/ticTacToe/O.jpg"));
		x.setPreferredSize(new Dimension(50, 50));
		o.setPreferredSize(new Dimension(50, 50));
		
		// Adding frame components
		p.add(x);
		p.add(o);
		f.add(p);
		
		// TODO Asking user who will make the first move
		/*
		do
		{
			JOptionPane op = new JOptionPane("Who will go first?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, new JButton[] {x, o}, x);
			JDialog dialog = op.createDialog("Starting tic-tac-toe");
			
			// Asking user question
			dialog.setVisible(true);
			dialog.dispose();
			
			// Continuing loop if dialog is exited prematurely
			value = op.getValue();
//			value = x.isSelected() ? 0 : o.isSelected() ? 0 : null;
		}
		while (value == null);
		
		
//		JOptionPane.showOptionDialog(f, "Who will go first?", "Starting tic-tac-toe", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new JButton[] {x, o}, x);
		
		turn = x.isSelected();
		*/
	}
	
	// Start game
	public static void newGame()
	{
		// Initializing game components
		p 				= new JPanel();
		f 				= new JFrame("Tic-Tac-Toe");
		states			= new Boolean[LEN][LEN];
		btns 			= new JLabel[LEN][LEN];
		
		// Building basic framework
		p.setLayout(new GridLayout(LEN, LEN));
		f.add(p);
		
		// Setting up buttons
		for (int i = 0; i < LEN; i++)
		{
			for (int j = 0; j < LEN; j++)
			{
				btns[i][j] = new JLabel();
				btns[i][j].setPreferredSize(new Dimension(50, 50));
				btns[i][j].addMouseListener(ml);
				setImg(btns[i][j], "Empty");
				p.add(btns[i][j]);
			}
		}
		
		// Setting up frame resize-ability
		f.addComponentListener(new ComponentAdapter()
		{
			@Override
		    public void componentResized(ComponentEvent ce)
		    {
				resizeBtns();
		    }
		});
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setResizable(true);
		f.pack();
//		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		JFrame.setDefaultLookAndFeelDecorated(false);
		f.setSize(LEN * 60, LEN * 60);
		f.validate();
		f.setVisible(true);
	}
	
	// Resizing buttons with frame
	protected static void resizeBtns()
	{
		// Resizing buttons (min. side length: 50px)
		for (int i = 0; i < LEN; i++)
		{
			for (int j = 0; j < LEN; j++)
			{
				JLabel btn = btns[i][j];
				
				// Adjusting button position
				btn.setLocation(i * btn.getWidth(), j * btn.getHeight());
				
				
				// TODO Replace image placement with resizing
				
				
//				ImageIcon scaledImage = new ImageIcon(btn.getIcon()
//		                .getScaledInstance(btn.getWidth() / 4,
//		                        btn.getHeight() / 4, 1));
//		        JLabel label = new JLabel(scaledImage);
				
//				btn.setSize(Math.max(50, f.getContentPane().getWidth() / len),
//						Math.max(50, f.getContentPane().getWidth() / len));
				
				btn.setSize(f.getContentPane().getWidth() / LEN,
						f.getContentPane().getWidth() / LEN);
			}
		}
		
		f.repaint();
	}
	
	// Change button image
	protected static void setImg(JLabel img, String path)
	{
		img.setIcon(new ImageIcon("src/ticTacToe/" + path + ".jpg"));
//		img.repaint();
	}
	
	// Find index of button in btns
	protected static int[] getBtnIndices(Object btn)
	{
		// Finding which button was left-clicked
		for (int i = 0; i < LEN; i++)
			for (int j = 0; j < LEN; j++)
				if (btns[i][j] == btn)
					return new int[] {i, j};
		
		// Shouldn't occur
		return null;
	}
	
	// Returns a boolean signifying as to whether or not a match exists
	public static boolean matchMade()
	{
		// Checking rows and columns for match
		for (int i = 0; i < LEN; i++)
		{
			boolean row = true;
			boolean col = true;
			
			for (int j = 0; j < LEN; j++)
			{
				if (states[i][i] != states[i][j] || states[i][j] == null) col = false;
				if (states[i][i] != states[j][i] || states[j][i] == null) row = false;
			}
			
			if (row || col)
				return true;
		}
		
		boolean ldiag = true;
		boolean rdiag = true;
		
		// Checking diagonals for match
		for (int i = 0, j = LEN - 1; i < LEN; i++,j--)
		{
			if (states[0][0] 	   != states[i][i] || states[i][i] == null) ldiag = false;
			if (states[0][LEN - 1] != states[i][j] || states[i][j] == null) rdiag = false;
		}
		
		return ldiag || rdiag;
	}
	
	// Game over
	public static void gameOver(String msg)
	{
//		// Removing all buttons' mouse listener
//		for (JLabel[] row : btns)
//			for (JLabel btn : row)
//				btn.removeMouseListener(ml);
		
		// Displaying dialog and terminating program
		JOptionPane.showMessageDialog(new JFrame(), msg,
				"Game over", JOptionPane.PLAIN_MESSAGE);
		System.exit(0);
	}
}
