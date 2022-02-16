/**
 * Date: April 2, 2021
 * Description: The game of Minesweeper
 * @author Matt
 * 
 */

package minesweeper;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

// Driver class
public class Minesweeper
{
	// 0 ~ 9 surrounding mines -> 0 ~ 9
	final static byte 		  FLAG 					= 10;
	final static byte 		  EMPTY 				= 11;
	final static byte 		  QUESTION_MARK 		= 12;
	final static byte 		  FLAG_MINE 			= 13;
	final static byte 		  EMPTY_MINE 			= 14;
	final static byte 		  QUESTION_MARK_MINE 	= 15;
	
	static int[] 			  initialIndices;
	static boolean[][] 		  mines;
	static int 				  TOTAL_MINES;
	
	static Number d;
	
	static JFrame 			  f;
	static JPanel 			  p;
	static Point 			  GRID;
	static JLabel[][] 		  btns;
	
	protected static byte[][] states;
	
	// Initializing default pseudo-random mine generation algorithm
	public static Consumer<Number> algorithm = density ->
	{
		List<int[]> nearby = new ArrayList<int[]>(nearbyBtns(initialIndices));
		
		for (int i = 0; i < GRID.x; i++)
		{
			for (int j = 0; j < GRID.y; j++)
			{
				boolean clicked = false;
				
				// Checking that this button hasn't been clicked
				for (int k = 0; k < nearby.size(); k++)
				{
					if (nearby.get(k)[0] == i && nearby.get(k)[1] == j)
					{
						// This button was clicked
						clicked = true;
						break;
					}
				}
				
				if (clicked)
				{
					// Skipping this clicked button
					continue;
				}
				
				// Attempting to place mine here
				if (Math.random() >= 1 - density.doubleValue())
				{
					// Placing and counting mine
					setMine(i, j);
					TOTAL_MINES++;
				}
				else
				{
					states[i][j] = EMPTY;
				}
					
			}
		}
	};
	
	// Initializing initial mouse listener
	volatile static MouseAdapter initialMl = new MouseAdapter()
	{
		@Override
		public void mouseClicked(MouseEvent me)
		{
			// Getting indices of initial button click and continuing main method
			initialIndices = getBtnIndices(me.getSource());
			
			// Replacing mouse listeners
			for (int i = 0; i < GRID.x; i++)
			{
				for (int j = 0; j < GRID.y; j++)
				{
					btns[i][j].removeMouseListener(initialMl);
					btns[i][j].addMouseListener(ml);
				}
			}
			
			// Running pseudo-random mine generation algorithm
			algorithm.accept(d);
			
			// Performing click method on clicked button
			lclickWithoutCheck(nearbyBtns(initialIndices));
			f.repaint();
		}
	};
	
	// Initializing mouse listener
	final static MouseAdapter ml = new MouseAdapter()
	{
		@Override
		public void mouseClicked(MouseEvent me)
		{
			click(me);
		}
	};
	
	// Driver code
	public static void main(String[] args)
	{
		// Setting up window components
		SwingUtilities.invokeLater(() ->
		{
			new Minesweeper(0.25);
		});
	}
	
	// Default constructor
	public Minesweeper(double d)
	{
		this(30, 16, d, algorithm); // Expert board (30 wide, 16 tall) is default size
	}
	
	// Constructor
	public Minesweeper(int w, int h, Number density, Consumer<Number> mineGenAlgo)
	{
		algorithm 	= mineGenAlgo;
		d 			= density;
		
		newGame(w, h);
	}
	
	// Start game
	public void newGame(int w, int h)
	{
		// Initializing game components
		GRID 			= new Point(w, h);
		p 				= new JPanel();
		f 				= new JFrame("Minesweeper");
		mines 			= new boolean[GRID.x][GRID.y];
		states			= new byte[GRID.x][GRID.y];
		initialIndices 	= null;
		btns 			= new JLabel[GRID.x][GRID.y];
		
		// Building basic framework
//		f.setVisible(false);
//		f.setUndecorated(true);
		p.setLayout(new GridLayout(GRID.y, GRID.x));
		f.add(p);
		
		// Setting up buttons
		for (int i = 0; i < GRID.x; i++)
		{
			for (int j = 0; j < GRID.y; j++)
			{
				// Setting up button
				btns[i][j] = setupImg("empty");
				btns[i][j].addMouseListener(initialMl);
				
				// Adding button to panel
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
		
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setResizable(true);
		f.pack();
//		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		JFrame.setDefaultLookAndFeelDecorated(false);
		f.setSize(GRID.x * 60, GRID.y * 60);
		f.validate();
		f.setVisible(true);
	}
	
	// Set mine method
	public static void setMine(int i, int j)
	{
//		try
//		{
			mines[i][j] = true;
			states[i][j] = EMPTY_MINE;
//		}
//		catch (ArrayIndexOutOfBoundsException e)
//		{
//			// I pity the fool who threw an exception
//		}
	}
	
	// General click method
	protected static void click(MouseEvent me)
	{
		// Getting clicked button
		int[] btnIndices = getBtnIndices(me.getSource());
		
		if (me.getButton() == MouseEvent.BUTTON1)
		{
			// Button was left clicked
			Set<int[]> btns = new HashSet<int[]>();
			btns.add(btnIndices);
			btnsClicked(btns);
		}
		else if (me.getButton() == MouseEvent.BUTTON2)
		{
			// Button was middle clicked
			// Getting surrounding buttons
			Set<int[]> nearbyBtns = nearbyBtns(btnIndices);
			
			// TODO Implement surrounding mine detection
			
			// Clicking nearby buttons
			btnsClicked(nearbyBtns);
		}
		else if (me.getButton() == MouseEvent.BUTTON3)
		{
			// Button was right-clicked
			int i = btnIndices[0];
			int j = btnIndices[1];
			
			// Temporarily initializing new button image
			JLabel img = null;
			
			// Cycling to next image
			if (states[i][j] == EMPTY)
			{
				states[i][j] = FLAG;
				img = setupImg("flag");
			}
			else if (states[i][j] == FLAG)
			{
				states[i][j] = QUESTION_MARK;
				img = setupImg("questionMark");
			}
			else if (states[i][j] == QUESTION_MARK)
			{
				states[i][j] = EMPTY;
				img = setupImg("empty");
			}
			else if (states[i][j] == EMPTY_MINE)
			{
				states[i][j] = FLAG_MINE;
				img = setupImg("flag");
			}
			else if (states[i][j] == FLAG_MINE)
			{
				states[i][j] = QUESTION_MARK_MINE;
				img = setupImg("questionMark");
			}
			else if (states[i][j] == QUESTION_MARK_MINE)
			{
				states[i][j] = EMPTY_MINE;
				img = setupImg("empty");
			}
			
			if (img != null)
			{
				changeImg(new int[] {i, j}, img);
				f.repaint();
			}
		}
	}
	
	// Left click method
	protected static void btnsClicked(Set<int[]> btnIndices)
	{
		// Button was (left) clicked
		Set<JLabel> exploded = new HashSet<JLabel>();
		
		// TODO Test if needed: Avoiding NullPointerException
//		btnIndices.remove(null);
		
		btnIndices.forEach(indices ->
		{
			// Accumulating exploded mines
			if (mines[indices[0]][indices[1]] && states[indices[0]][indices[1]] != FLAG_MINE)
			{
				exploded.add(btns[indices[0]][indices[1]]);
			}
		});
		
		Set<int[]> clicked = new HashSet<int[]>();
		
		// Checking if the game has ended
		if (exploded.isEmpty())
		{
			// Getting clicked buttons
			btnIndices.forEach(indices ->
			{
				if (states[indices[0]][indices[1]] == EMPTY &&
						!exploded.contains(btns[indices[0]][indices[1]]))
				{
					clicked.add(indices);
				}
			});
			
			// Updating clicked buttons
			lclickWithoutCheck(clicked);
			
			int count = 0;
			
			// Counting number of clicked buttons
			for (int i = 0; i < GRID.x; i++)
			{
				for (int j = 0; j < GRID.y; j++)
				{
					if (states[i][j] <= 9)
					{
						count++;
					}
				}
			}
			
			// Checking if the game was won
			if (count == GRID.x * GRID.y - TOTAL_MINES)
			{
				// Removing all buttons' mouse listener
				rmvAllListeners(ml);
				
				gameWon();
			}
		}
		else
		{
			// Getting clicked buttons
			btnIndices.forEach(indices ->
			{
				if ((states[indices[0]][indices[1]] == EMPTY || states[indices[0]][indices[1]] == QUESTION_MARK) && !exploded.contains(btns[indices[0]][indices[1]]))
				{
					clicked.add(indices);
				}
			});
			
			// Updating button images for game over
			for (int i = 0; i < GRID.x; i++)
			{
				for (int j = 0; j < GRID.y; j++)
				{
					JLabel img = null;
					
					// Finding which image to use
					if (states[i][j] == FLAG)
					{
						img = setupImg("noMine");
					}
					else if (states[i][j] == QUESTION_MARK_MINE)
					{
						img = setupImg(exploded.contains(btns[i][j]) ? "questionMarkExplodedMine" : "questionMarkMine");
					}
					else if (exploded.contains(btns[i][j]))
					{
						img = setupImg("explodedMine");
					}
					else if (states[i][j] == EMPTY_MINE)
					{
						img = setupImg(exploded.contains(btns[i][j]) ? "explodedMine" : "mine");
					}
					
					// Updating image if it's supposed to be updated
					if (img != null)
					{
						changeImg(new int[] {i, j}, img);
					}
				}
			}
			
			// Removing all buttons' mouse listener
			rmvAllListeners(ml);
			
			gameLost();
		}
		
		f.repaint();
	}
	
	// Clicks buttons without checking for mines
	protected static void lclickWithoutCheck(Set<int[]> btnIndices)
	{
		btnIndices.forEach(indices ->
		{
			byte mineCount = mineCount(indices);
			JLabel img = setupImg("" + mineCount);
			states[indices[0]][indices[1]] = mineCount;
			
			changeImg(indices, img);
		});
	}
	
	// Remove all buttons' mouse listener
	protected static void rmvAllListeners(MouseListener ml)
	{
		// Removing button mouse listeners
		for (int i = 0; i < GRID.x; i++)
		{
			for (int j = 0; j < GRID.y; j++)
			{
				btns[i][j].removeMouseListener(ml);
			}
		}
	}
	
	// Collecting surrounding button indices
	protected static Set<int[]> nearbyBtns(int[] btnIndices)
	{
		Set<int[]> out = new HashSet<int[]>();
		
		for (int w = -1; w <= 1; w++)
		{
			for (int h = -1; h <= 1; h++)
			{
				// Checking if indices are within bounds
				if (btnIndices[0] + w >= 0 && btnIndices[0] + w < GRID.x && btnIndices[1] + h >= 0 && btnIndices[1] + h < GRID.y)
				{
					out.add(new int[] {btnIndices[0] + w, btnIndices[1] + h});
				}
			}
		}
		
		// TODO Implement iterative empty tile clicking
		
		return out;
	}
	
	// Resizing buttons with frame
	protected static void resizeBtns()
	{
		// Resizing buttons (min. side length: 50px)
		for (int i = 0; i < GRID.x; i++)
		{
			for (int j = 0; j < GRID.y; j++)
			{
				JLabel btn = btns[i][j];
				
				// Adjusting button position
				btn.setLocation(i * btn.getWidth(), j * btn.getHeight());
				
				
				// TODO Replace image placement with resizing
				
				
//				ImageIcon scaledImage = new ImageIcon(btn.getIcon()
//		                .getScaledInstance(btn.getWidth() / 4,
//		                        btn.getHeight() / 4, 1));
//		        JLabel label = new JLabel(scaledImage);
				
//				btn.setSize(Math.max(50, f.getContentPane().getWidth() / GRID.x),
//						Math.max(50, f.getContentPane().getWidth() / GRID.y));
				
				btn.setSize(f.getContentPane().getWidth() / GRID.x,
						f.getContentPane().getWidth() / GRID.y);
			}
		}
		
		f.repaint();
	}
	
	// Count surrounding mines
	protected static byte mineCount(int[] btnIndices)
	{
		byte out = 0;
		
		// Counting surrounding mines
		for (int w = -1; w <= 1; w++)
		{
			for (int h = -1; h <= 1; h++)
			{
				try
				{
					if (mines[btnIndices[0] + w][btnIndices[1] + h])
					{
						out++;
					}
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					
				}
			}
		}
		
		return out;
	}
	
	// Finding and setting up image
	protected static JLabel setupImg(String path)
	{
		// Finding and setting up new image
		JLabel img = new JLabel();
		img.setIcon(new ImageIcon("src/minesweeper/" + path + "Tile.png"));
		img.setPreferredSize(new Dimension(50, 50));
		
		return img;
	}
	
	// Change button image
	protected static void changeImg(int[] oldImgIndices, JLabel newImg)
	{
		// Getting image
		JLabel oldImg = btns[oldImgIndices[0]][oldImgIndices[1]];
		// Adjusting image appearance
		newImg.setLocation(oldImg.getX(), oldImg.getY());
		newImg.setSize(oldImg.getWidth(), oldImg.getHeight());
		
		// Adding mouse listener if not already present
		if (newImg.getMouseListeners().length < 1)
		{
			newImg.addMouseListener(ml);
		}
		
		// Finding index of old image in array
		int[] indices = getBtnIndices(oldImg);
		int oldIndex = indices[0] * GRID.y + indices[1];
		
		// Changing image
		p.remove(oldIndex);
		p.add(newImg, oldIndex);
		btns[indices[0]][indices[1]] = newImg;
	}
	
	// Find index of button in btns
	protected static int[] getBtnIndices(Object btn)
	{
		// Finding which button was left-clicked
		for (int i = 0; i < GRID.x; i++)
		{
			for (int j = 0; j < GRID.y; j++)
			{
				// Checking if this button was left-clicked
				if (btns[i][j] == btn)
				{
					return new int[] {i, j};
				}
			}
		}
		
		// Shouldn't occur
		return null;
	}
	
	// Game won
	public static void gameWon()
	{
		JOptionPane.showMessageDialog(new JFrame(), "You win!",
				"Game over", JOptionPane.PLAIN_MESSAGE);
	}
	
	// Game lost
	public static void gameLost()
	{
		JOptionPane.showMessageDialog(new JFrame(), "You lose",
				"Game over", JOptionPane.PLAIN_MESSAGE);
	}
}
