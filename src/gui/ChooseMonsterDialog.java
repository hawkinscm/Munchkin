
package gui;

import gui.components.CardPanel;
import gui.components.CustomButton;
import gui.components.CustomDialog;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JLabel;

import model.Battle;
import model.Player;
import model.card.Card;
import model.card.DoorCard;
import model.card.MonsterCard;
import model.card.MonsterEnhancerCard;

/**
 * Dialog that allows the player to choose a Monster Card from the monsters in a battle.
 */
public class ChooseMonsterDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	// Card Panel that displays the Monster Cards
	private CardPanel monsterPanel;
	
	// Player who needs to choose a card
	private Player player;
	// Battle that the monsters are in
	private Battle battle;
	// Monster card that is selected
	private MonsterCard selectedMonster = null;
	
	/**
	 * Creates a new ChooseMonsterDialog Dialog.
	 * @param p player that will choose a monster card
	 * @param b battle that the monsters are in
	 * @param reason text giving the reason to choose a monster card
	 */
	public ChooseMonsterDialog(Player p, Battle b, String reason) {
		super("Choose Monster");
		
		// initialize variable and display GUI controls
		player = p;
		battle = b;
		
		final JLabel infoLabel = new JLabel(player.getName() + ": " + reason);
		getContentPane().add(infoLabel, c);
		
		// Load list of Monster Cards from the battle
		LinkedList<Card> monsterCards = new LinkedList<Card>();
		for (int monsterIndex = 0; monsterIndex < battle.getMonsterCount(); monsterIndex++)
			monsterCards.add(battle.getMonster(monsterIndex));
				
		CustomButton selectButton = new CustomButton("Select") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				selectedMonster = (MonsterCard)monsterPanel.getSelectedCard();
				dispose();
			}
		};
		
		c.gridy++;
		// Displays the Monster Cards to choose from along with information about them from the battle
		monsterPanel = new CardPanel(monsterCards, 1, selectButton, null) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void setSelectedImageLabel(JLabel label) {
				super.setSelectedImageLabel(label);
				mainImageLabel.setHorizontalTextPosition(JLabel.CENTER);
				mainImageLabel.setVerticalTextPosition(JLabel.BOTTOM);
				mainImageLabel.setText(getMonsterInfo(this.getSelectedIndex()));
				
				refresh();
			}
		};
		getContentPane().add(monsterPanel, c);
		
		refresh();
	}
	
	/**
	 * Returns HTML-formatted text containing current information about the monster from the battle.
	 * @param monsterIndex the index of the monster to get information about
	 * @return HTML-formatted text containing current information about the monster from the battle
	 */
	private String getMonsterInfo(int monsterIndex) {
		MonsterCard monster = battle.getMonster(monsterIndex);
		
		int monsterLevel = monster.getLevel(battle);
		int winTreasures = monster.getTreasures();
		int winLevels = monster.getWinLevels();
		
		boolean hasMate = false;				
		Iterator<DoorCard> enhancerIter = battle.getMonsterEnhancers(monster).iterator();
		while (enhancerIter.hasNext()) {
			DoorCard card = enhancerIter.next();
			if (card.getID() == Card.OD_MATE) {
				hasMate = true;
				continue;
			}
			
			if (card.getID() == Card.ME_BABY)
				winTreasures--;
			else if (card.getID() == Card.ME_ANCIENT)
				winTreasures += 2;
			else if (card.getID() == Card.ME_ENRAGED)
				winTreasures++;
			else if (card.getID() == Card.ME_HUMONGOUS)
				winTreasures += 2;
			else if (card.getID() == Card.ME_INTELLIGENT)
				winTreasures++;
			
			monsterLevel += ((MonsterEnhancerCard)card).getBonus();
		}
		
		if (winTreasures < 1)
			winTreasures = 1;			
		if (monsterLevel < 1)
			monsterLevel = 1;
		
		String monsterInfo = "<HTML>&nbsp;Monster Level: " + monsterLevel + " &nbsp;<br>&nbsp;";
		monsterInfo += "Winnable Treasures: " + winTreasures + "&nbsp;<br>&nbsp;";
		monsterInfo += "Winnable Levels: " + winLevels + "&nbsp;";
		if (hasMate)
			monsterInfo += "<br>&nbsp;Its Mate has these same values.&nbsp;";
		monsterInfo += "</HTML>";
		
		return monsterInfo;
	}
	
	/**
	 * Returns the selected Monster Card.
	 * @return the selected Monster Card
	 */
	public MonsterCard getSelectedMonster() {
		return selectedMonster;
	}
}
