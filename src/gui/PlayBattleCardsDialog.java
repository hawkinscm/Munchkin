
package gui;

import java.util.LinkedList;

import javax.swing.JLabel;

import exceptions.EndGameException;
import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.CustomDialog;

import model.CardPlayManager;
import model.Player;
import model.card.Card;
import model.card.ItemCard;
import model.card.MonsterCard;

/**
 * Dialog that allows the playing of Item Cards in a battle.
 */
public class PlayBattleCardsDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// GUI controls
	private CardPanel cardPanel;
	private BattlePanel battlePanel;
	private RunDialog runDialog;
	
	// Player whose cards are displayed for playing
	private Player player;
	// the current monster chasing the fleeing Player, if currently running away
	private MonsterCard pursuingMonster;
	// List of Cards available to Player to play in battle
	private LinkedList<Card> cards;
	
	// whether or not a card has been played
	private boolean playedCard;
	// whether or not the game has been won
	private boolean isGameOver;
		
	/**
	 * Creates a new PlayBattleCardsDialog dialog for when player(s) are fighting a battle.
	 * @param panel reference to the Battle Panel for the current battle
	 * @param p player who is playing a Card
	 */
	public PlayBattleCardsDialog(BattlePanel panel, Player p) {
		super(p.getName() + "'s Battle Items");
		battlePanel = panel;
		runDialog = null;
		player = p;
		pursuingMonster = null;
		initialize();
	}
	
	/**
	 * Creates a new PlayBattleCardsDialog dialog for when player(s) are currently running away.
	 * @param dialog reference to the Run Dialog for the currently running players
	 * @param p the player who is playing a Card
	 * @param pursuer the monster who is currently pursing the player
	 */
	public PlayBattleCardsDialog(RunDialog dialog, Player p, MonsterCard pursuer) {
		super(p.getName() + "'s Playable Items");
		battlePanel = null;
		runDialog = dialog;
		player = p;
		pursuingMonster = pursuer;
		initialize();
	}
	
	/**
	 * Initialize variables and display GUI controls.
	 */
	private void initialize() {		
		playedCard = false;
		isGameOver = false;
		
		cards = new LinkedList<Card>();
		cards.addAll(player.getCarriedItems());
		for (Card card : player.getHandCards())
			if (card instanceof ItemCard)
				cards.add(card);
		
		if (player.isComputer())
			return;
		
		// if player has no cards to play, inform him and display only Button to close dialog
		if (cards.isEmpty()) {
			JLabel infoLabel = new JLabel("You have no cards to play.");
			getContentPane().add(infoLabel, c);
			
			c.gridy++;
			CustomButton okButton = new CustomButton("OK") {
				private static final long serialVersionUID = 1L;
				
				public void buttonPressed() {
					dispose();
				}
			};
			getContentPane().add(okButton, c);
			
			setJMenuBar(null);
			refresh();
			return;
		}
		
		// Button that allows user to play a card for or against the player(s) in the battle
		CustomButton playButton = new CustomButton("Play") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				Card card = cardPanel.getSelectedCard();
				try {
					// handle card playing for battling players
					if (runDialog == null) {
						if (CardPlayManager.playCard(player, card, battlePanel.getBattle())) {
							cards.remove(card);
							battlePanel.updateDisplay();
							playedCard = true;
							
							if (cards.isEmpty())
								dispose();
							
							cardPanel.removeSelectedImage();
						}
					}
					// handle card playing for running players
					else if (CardPlayManager.playCard(player, card, pursuingMonster, runDialog)) {
						cards.remove(card);
						playedCard = true;
						
						if (cards.isEmpty())
							dispose();
						
						cardPanel.removeSelectedImage();
					}
				}
				catch (EndGameException ex) {
					isGameOver = true;
					dispose();
				}
			}
		};
		
		// Button that signals the player is done playing cards and closes the dialog
		CustomButton doneButton = new CustomButton("Done") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				dispose();
			}
		};

		cardPanel = new CardPanel(cards, 2, playButton, doneButton);
		getContentPane().add(cardPanel, c);
		
		refresh();
	}
	
	@Override
	public void setVisible(boolean b) {
		if (!player.isComputer())
			super.setVisible(b);
	}
	
	/**
	 * Returns whether or not a card was played.
	 * @return true if a card was played; false otherwise
	 * @throws EndGameException if a player has won the game and it's game over
	 */
	public boolean playedCard() throws EndGameException {
		if (isGameOver)
			throw new EndGameException();
		
		return playedCard;
	}
}
