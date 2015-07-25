
package gui.components;

import gui.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.card.Card;

/**
 * Abstract class for a Panel that displays cards for viewing/selecting/using.  
 * Allows multiple cards to be selected at the same time.
 */
public abstract class MultiSelectCardPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	// Constants with pixel heights/widths of specific GUI objects
	private final int SCROLL_BAR_SPACING = 10;
	private final int HEIGHT_PADDING = 20;
	private final int CARD_SPACING = 10;
	private final int CARD_WIDTH = 66;
	private final int CARD_PANEL_HEIGHT = 123;
	private final int MAX_ROWS = 3;

	/**
	 * Label that will display info on all selected/checked cards/image labels.
	 */
	protected JLabel checkedInfoLabel;
	
	// GUI controls for displaying info and getting user input
	private JPanel cardsPanel;
	private LinkedList<ImagePanel> cardImagePanels;
	private JLabel mainImageLabel;
	private JButton actionButton;
	private JButton doneButton;	
	private ImagePanel selectedImagePanel;
	
	// the maximum number of cards that will be displayed on the horizontal axis
	private int maxColumns;
	// the list of cards that will be displayed
	private LinkedList<Card> cards;
	
	/**
	 * Panel that displays a single card and allows it to be selected.
	 */
	private class ImagePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		/**
		 * Label that contains a picture of the card it represents.
		 */
		public JLabel imageLabel;
		/**
		 * Check box that marks whether or not the card is selected.
		 */
		public JCheckBox selectedCheckBox;
		
		/**
		 * Creates a new ImagePanel panel.
		 * @param image the label containing the picture of a card
		 */
		public ImagePanel(JLabel image) {
			super();
			
			imageLabel = image;
			selectedCheckBox = new JCheckBox();
			selectedCheckBox.setSelected(false);
			// update the main selected info text box when selected/deselected
			selectedCheckBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					setCheckedInfoText();
				}
			});
			
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(0, 0, 0, 0);
			c.gridx = 0;
			c.gridy = 0;
			add(imageLabel, c);
			
			c.gridy++;
			c.insets.top = 2;
			add(selectedCheckBox, c);
		}
	}
	
	/**
	 * Creates a new MultiSelectCardPanel Panel.
	 * @param newCards list of cards to be displayed for viewing/selection
	 * @param columns maximum number of cards that will be displayed on a horizontal axis
	 * @param actionBtn button that will perform a preset function on the selected card
	 * @param doneBtn button that will perform a preset function to finish with the card panel
	 */
	public MultiSelectCardPanel(LinkedList<Card> newCards, int columns, JButton actionBtn, JButton doneBtn) {
		super();
	
		cards = newCards;
		maxColumns = columns;
		actionButton = actionBtn;
		doneButton = doneBtn;
		
		initializeCardDisplay();
		initializePanel();
	}
	
	/**
	 * Adds a new image panel (card) to the display.
	 * @param image the label with the card picture that will be added
	 * @return the newly created image panel
	 */
	private ImagePanel addNewImagePanel(Image image) {		
		Image resizedImage = image.getScaledInstance(66, 103, Image.SCALE_SMOOTH);
		final JLabel imageLabel = new JLabel(new ImageIcon(resizedImage));
		
		final ImagePanel imagePanel = new ImagePanel(imageLabel);
		
		imageLabel.addMouseListener(new MouseListener() {
			private boolean isInside = false;
			
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) { isInside = true; }
			public void mouseExited(MouseEvent e) { isInside = false; }
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON1 || !isInside)
					return;
				
				actionButton.requestFocus();
				setSelectedImagePanel(imagePanel);
			}						
		});
		
		cardImagePanels.add(imagePanel);
		
		return imagePanel;
	}
	
	/**
	 * Initializes and displays the card image labels and the image label displaying the card that currently has the focus.
	 */
	private void initializeCardDisplay() {
		cardImagePanels = new LinkedList<ImagePanel>();
		
		// Determine number of card rows and columns to display
		int numCards = cards.size();
		int numRows = numCards / maxColumns;
		if (numCards % maxColumns > 0)
			numRows++;
		
		// Panel to display small versions of each card and allows user to select them
		cardsPanel = new JPanel();
		cardsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		
		try {
			Iterator<Card> cardIter = cards.iterator();
			
			for (int row = 0; true; row++) {
				c.gridx = 0;
				for (int column = 0; column < maxColumns; column++) {
					Card card = cardIter.next();
					
					ImagePanel imagePanel = addNewImagePanel(card.getPicture().getImage());
					
					cardsPanel.add(imagePanel, c);	
					c.gridx++;
				}
				c.gridy++;
			}
		}
		catch (NoSuchElementException ex) {}
		
		// displays full-sized picture of the card/image panel with the current focus 
		mainImageLabel = new JLabel();
		if (cards.isEmpty()) {
			actionButton.setEnabled(false);
			mainImageLabel.setIcon(new ImageIcon(GUI.class.getResource("images/blank.jpg")));
		}
		else
			setSelectedImagePanel(cardImagePanels.getFirst());
	}
	
	/**
	 * Initializes and displays GUI controls.
	 */
	private void initializePanel() {
		// constants for the height and width of card columns/rows
		final int columnHeight = HEIGHT_PADDING + (MAX_ROWS * CARD_SPACING) + (MAX_ROWS * CARD_PANEL_HEIGHT);
		final int rowWidth = SCROLL_BAR_SPACING + (maxColumns * CARD_SPACING) + (maxColumns * CARD_WIDTH);		
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 10, 0 , 10);
		c.gridwidth = 3;
		checkedInfoLabel = new JLabel(" ");
		add(checkedInfoLabel, c);
		c.gridwidth = 1;
		
		c.gridy++;
		c.insets = new Insets(10, 10, 10, 5);
		c.gridheight = 2;
		c.anchor = GridBagConstraints.NORTH;
		JScrollPane scrollPane = new JScrollPane(cardsPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getViewport().setPreferredSize(new Dimension(rowWidth, columnHeight));
		add(scrollPane, c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridheight = 1;
		
		c.insets = new Insets(10, 5, 10, 10);
		c.gridx++;
		if (doneButton != null)
			c.gridwidth = 2;
		add(mainImageLabel, c);
		c.gridwidth = 1;
		
		c.insets.top = 0;
		c.gridy++;
		// allows user to use arrow keys on keyboard to traverse/select cards
		actionButton.addKeyListener(new java.awt.event.KeyListener() {
			public void keyPressed(KeyEvent e) {
				int newIndex = -1;
				if (e.getKeyCode() == KeyEvent.VK_UP)
					newIndex = cardImagePanels.indexOf(selectedImagePanel) - maxColumns;
				else if (e.getKeyCode() == KeyEvent.VK_DOWN)
					newIndex = cardImagePanels.indexOf(selectedImagePanel) + maxColumns;
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					newIndex = cardImagePanels.indexOf(selectedImagePanel) + 1;
				else if (e.getKeyCode() == KeyEvent.VK_LEFT)
					newIndex = cardImagePanels.indexOf(selectedImagePanel) - 1;
				
				if (newIndex >= 0 && newIndex < cardImagePanels.size())
					setSelectedImagePanel(cardImagePanels.get(newIndex));
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});
		add(actionButton, c);
		
		c.gridx++;
		c.anchor = GridBagConstraints.EAST;
		if (doneButton != null)
			add(doneButton, c);
	}
	
	/**
	 * Returns the card from the image panel that currently has the focus.
	 * @return the card that currently has the focus
	 */
	private Card getSelectedCard() {
		int selectedCardIndex = cardImagePanels.indexOf(selectedImagePanel);
		return cards.get(selectedCardIndex);
	}
	
	/**
	 * Sets the current focus to the given image panel (card). 
	 * @param imagePanel the image panel (card) to get the focus.
	 */
	private void setSelectedImagePanel(ImagePanel imagePanel) {
		if (selectedImagePanel != null)
			selectedImagePanel.setBorder(null);
		
		imagePanel.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
		selectedImagePanel = imagePanel;
		mainImageLabel.setIcon(getSelectedCard().getPicture());
		
		// test
		//mainImageLabel.setText(getSelectedCard().getName());
		//mainImageLabel.setVerticalTextPosition(JLabel.BOTTOM);
		//mainImageLabel.setHorizontalTextPosition(JLabel.CENTER);
	}
	
	/**
	 * Abstract class that will define what is displayed when a card is selected/deselected.
	 * Can be used to display info or totals for all selected cards.
	 */
	public abstract void setCheckedInfoText();
	
	/**
	 * Returns a list of all selected/checked cards.
	 * @return a list of all selected/checked cards
	 */
	public LinkedList<Card> getCheckedCards() {
		LinkedList<Card> checkedCards = new LinkedList<Card>();
		for (int index = 0; index < cardImagePanels.size(); index++) {
			ImagePanel imagePanel = cardImagePanels.get(index);
			if (imagePanel.selectedCheckBox.isSelected())
				checkedCards.add(cards.get(index));
		}
		
		return checkedCards;
	}
}
