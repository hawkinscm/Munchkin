
package gui.components;

import gui.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.card.Card;

/**
 * Panel that displays cards for viewing/selecting/using.
 */
public class CardPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	// Constants with pixel heights/widths of specific GUI objects
	private final int SCROLL_BAR_SPACING = 10;
	private final int HEIGHT_PADDING = 20;
	private final int CARD_SPACING = 10;
	private final int CARD_WIDTH = 66;
	private final int CARD_HEIGHT = 103;
	private final int MAX_ROWS = 3;

	// GUI controls for displaying info and getting user input
	private JScrollPane scrollPane;
	private JPanel cardImagesPanel;
	private LinkedList<JLabel> cardImageLabels;
	private JButton actionButton;
	private JButton doneButton;
	/**
	 * Label that contains a full-sized image of the selected card/image label.
	 */
	protected JLabel mainImageLabel;
	/**
	 * Label with the picture of a card that is currently selected.
	 */
	protected JLabel selectedImageLabel;
	
	// the maximum number of cards that will be displayed on the horizontal axis
	private int maxColumns;
	// the list of cards that will be displayed
	private LinkedList<Card> cards;
	
	/**
	 * Creates a new CardPanel panel.
	 * @param newCards cards that will be displayed
	 * @param columns maximum number of cards that will be displayed on a horizontal axis
	 * @param actionBtn button that will perform a preset function on the selected card; if null, button is not displayed
	 * @param doneBtn button that will perform a preset function to finish with the card panel
	 */
	public CardPanel(LinkedList<Card> newCards, int columns, JButton actionBtn, JButton doneBtn) {
		super();
	
		cards = newCards;
		maxColumns = columns;
		actionButton = actionBtn;
		if (actionButton == null) {
			actionButton = new CustomButton() {
				private static final long serialVersionUID = 1L;				
				public void buttonPressed() {};
			};
			actionButton.setVisible(false);
		}
		else {
			actionButton.setEnabled(true);
			actionButton.setVisible(true);
		}
			
		doneButton = doneBtn;
		
		initializeCardDisplay();
		initializePanel();
		
		// if the number of cards is small, there is no need for a scroll pane, since all will be visible
		if (cards.size() <= 1) {
			scrollPane.setVisible(false);
			validate();
			repaint();
		}
	}
	
	/**
	 * Initializes and displays GUI controls for selecting/displaying the cards.
	 */
	private void initializeCardDisplay() {
		// list of all picture cards to display
		cardImageLabels = new LinkedList<JLabel>();
		
		// Determine number of card rows and columns to display
		int numCards = cards.size();
		int numRows = numCards / maxColumns;
		if (numCards % maxColumns > 0)
			numRows++;
		
		// Panel to display small versions of each card and allows user to select one
		cardImagesPanel = new JPanel();
		cardImagesPanel.setLayout(new GridBagLayout());
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
					
					JLabel imageLabel = addNewImageLabel(card);					
					cardImagesPanel.add(imageLabel, c);					
					c.gridx++;
				}
				c.gridy++;
			}
		}
		catch (NoSuchElementException ex) {}
		
		// displays full-size version of selected card
		mainImageLabel = new JLabel();
		if (cards.isEmpty()) {
			actionButton.setEnabled(false);
				mainImageLabel.setIcon(new ImageIcon(GUI.class.getResource("images/blank.jpg")));
		}
		else
			setSelectedImageLabel(cardImageLabels.getFirst());
	}
	
	/**
	 * Initializes and displays GUI controls.
	 */
	private void initializePanel() {
		// constants for the height and width of card columns/rows
		final int columnHeight = HEIGHT_PADDING + (MAX_ROWS * CARD_SPACING) + (MAX_ROWS * CARD_HEIGHT);
		final int rowWidth = SCROLL_BAR_SPACING + (maxColumns * CARD_SPACING) + (maxColumns * CARD_WIDTH);		
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		
		c.insets = new Insets(10, 10, 10, 0);
		c.gridheight = 2;
		c.anchor = GridBagConstraints.NORTH;
		scrollPane = new JScrollPane(cardImagesPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getViewport().setPreferredSize(new Dimension(rowWidth, columnHeight));
		add(scrollPane, c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridheight = 1;
		
		c.insets = new Insets(10, 10, 10, 10);
		c.gridx++;
		if (doneButton != null)
			c.gridwidth = 2;
		add(mainImageLabel, c);
		c.gridwidth = 1;
		
		c.insets.top = 0;
		c.gridy++;
		// allows user to use arrow keys on keyboard to traverse/select cards
		actionButton.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				int newIndex = -1;
				if (e.getKeyCode() == KeyEvent.VK_UP)
					newIndex = cardImageLabels.indexOf(selectedImageLabel) - maxColumns;
				else if (e.getKeyCode() == KeyEvent.VK_DOWN)
					newIndex = cardImageLabels.indexOf(selectedImageLabel) + maxColumns;
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					newIndex = cardImageLabels.indexOf(selectedImageLabel) + 1;
				else if (e.getKeyCode() == KeyEvent.VK_LEFT)
					newIndex = cardImageLabels.indexOf(selectedImageLabel) - 1;
				
				if (newIndex >= 0 && newIndex < cardImageLabels.size())
					setSelectedImageLabel(cardImageLabels.get(newIndex));
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});
		add(actionButton, c);
		
		if (doneButton != null) {
			c.gridx++;
			c.anchor = GridBagConstraints.EAST;
			// allows user to use arrow keys on keyboard to traverse/select cards			
			doneButton.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {
					int newIndex = -1;
					if (e.getKeyCode() == KeyEvent.VK_UP)
						newIndex = cardImageLabels.indexOf(selectedImageLabel) - maxColumns;
					else if (e.getKeyCode() == KeyEvent.VK_DOWN)
						newIndex = cardImageLabels.indexOf(selectedImageLabel) + maxColumns;
					else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
						newIndex = cardImageLabels.indexOf(selectedImageLabel) + 1;
					else if (e.getKeyCode() == KeyEvent.VK_LEFT)
						newIndex = cardImageLabels.indexOf(selectedImageLabel) - 1;
					
					if (newIndex >= 0 && newIndex < cardImageLabels.size())
						setSelectedImageLabel(cardImageLabels.get(newIndex));
				}
				public void keyReleased(KeyEvent e) {}
				public void keyTyped(KeyEvent e) {}
			});
			add(doneButton, c);
		}
	}
	
	/**
	 * Adds a new picture card to the display.
	 * @param card card that is to be displayed
	 * @return a new label containing the picture on the card
	 */
	protected JLabel addNewImageLabel(Card card) {
		return addNewImageLabel(card.getPicture().getImage());
	}
	
	/**
	 * Adds a new picture from a card to the display.
	 * @param image picture that is to be displayed
	 * @return a new label containing the picture
	 */
	protected JLabel addNewImageLabel(Image image) {
		// shrink the image to fit cards better on the screen
		Image resizedImage = image.getScaledInstance(66, 103, Image.SCALE_SMOOTH);
		final JLabel imageLabel = new JLabel(new ImageIcon(resizedImage));
		// allow the user to click on and select the card
		imageLabel.addMouseListener(new MouseListener() {
			private boolean isInside = false;
			
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) { isInside = true; }
			public void mouseExited(MouseEvent e) { isInside = false; }
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON1 || !isInside)
					return;
				
				setSelectedImageLabel(imageLabel);
			}						
		});					
		cardImageLabels.add(imageLabel);
		
		return imageLabel;
	}
		
	/**
	 * Returns the index of the selected card.
	 * @return the index of the selected card
	 */
	protected int getSelectedIndex() {
		return cardImageLabels.indexOf(selectedImageLabel);
	}
	
	/**
	 * Sets the selected label containing the picture of a card.
	 * @param imageLabel the label containing a picture of a card
	 */
	protected void setSelectedImageLabel(JLabel imageLabel) {
		if (selectedImageLabel != null)
			selectedImageLabel.setBorder(null);
		
		// draws a border around the label to show that it is selected
		imageLabel.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
		selectedImageLabel = imageLabel;
		Card selectedCard = getSelectedCard();
		if (selectedCard == null)
			mainImageLabel.setIcon(new ImageIcon(GUI.class.getResource("images/blank.jpg")));
		else
			mainImageLabel.setIcon(getSelectedCard().getPicture());
	}
	
	/**
	 * Sets the selected label to the one containing the picture of the given card.
	 * @param card the card to select the image label for
	 */
	public void setSelectedImageLabel(Card card) {
		int cardIdx = cards.indexOf(card);
		setSelectedImageLabel(cardImageLabels.get(cardIdx));		
	}
	
	/**
	 * Returns the selected card.
	 * @return the selected card
	 */
	public Card getSelectedCard() {
		int selectedCardIndex = cardImageLabels.indexOf(selectedImageLabel);
		if (selectedCardIndex == -1)
			return null;
		
		return cards.get(selectedCardIndex);
	}
	
	/**
	 * Adds a card to the list of cards available for display/selection.
	 * @param card card to add to the card display/selection list.
	 */
	public void addCard(Card card) {
		cards.add(card);
		
		JLabel newImageLabel = addNewImageLabel(card);
		
		int newImageIndex = cardImageLabels.size() - 1;
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = newImageIndex % maxColumns;
		c.gridy = newImageIndex / maxColumns;
		cardImagesPanel.add(newImageLabel, c);
		
		setSelectedImageLabel(newImageLabel);
	
		if (cards.size() > 1)
			scrollPane.setVisible(true);
		
		validate();
		repaint();
		
		actionButton.setEnabled(true);
	}
	
	/**
	 * Removes the selected label which contains a picture of a card.
	 */
	public void removeSelectedImage() {
		int selectedCardIndex = cardImageLabels.indexOf(selectedImageLabel);
		
		// remove the selected picture label and reloads the display panels
		if (cardImageLabels.size() != cards.size() + 1) {
			cardImagesPanel.removeAll();
			cardImageLabels.clear();
			
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(5, 5, 5, 5);
			c.gridx = 0;
			c.gridy = 0;
			try {
				Iterator<Card> cardIter = cards.iterator();
				
				for (int row = 0; true; row++) {
					c.gridx = 0;
					for (int column = 0; column < maxColumns; column++) {
						JLabel imageLabel = addNewImageLabel(cardIter.next());						
						cardImagesPanel.add(imageLabel, c);			
						c.gridx++;
					}
					c.gridy++;
				}
			}
			catch (NoSuchElementException ex) {}
		}
		else {
			for (int imageIndex = selectedCardIndex; imageIndex < cards.size(); imageIndex++)
				cardImageLabels.get(imageIndex).setIcon(cardImageLabels.get(imageIndex + 1).getIcon());
			
			cardImagesPanel.remove(cardImageLabels.removeLast());
		}
		
		if (cards.isEmpty()) { 
			actionButton.setEnabled(false);
			mainImageLabel.setIcon(new ImageIcon(GUI.class.getResource("images/blank.jpg")));
		}
		else if (selectedCardIndex >= cardImageLabels.size())
			setSelectedImageLabel(cardImageLabels.getLast());
		else
			setSelectedImageLabel(cardImageLabels.get(selectedCardIndex));
		
		if (cards.size() <= 1)
			scrollPane.setVisible(false);
		
		validate();
		repaint();
	}
	
	/**
	 * Reloads the card displays while keeping the currently selected image label/card selected. 
	 */
	public void refreshMainImage() {
		setSelectedImageLabel(selectedImageLabel);
		
	}
}
