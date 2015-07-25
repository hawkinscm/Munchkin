
package gui;

import java.util.LinkedList;

import exceptions.EndGameException;
import model.CardPlayManager;
import model.GM;
import model.Player;
import model.PlayerType;
import model.UnitTest;
import model.card.Card;
import model.card.CurseCard;
import model.card.EquipmentCard;
import model.card.ItemCard;
import model.card.EquipmentCard.EquipmentType;

public class CurseDialogTest extends UnitTest {
	private static final long serialVersionUID = 1L;
			
	public int testAll() {
		testCurseDialog();
		
		return errorCount;
	}
	
	private void testCurseDialog() {
		Player medium = new Player(new MockGUI(0), "medium", true, PlayerType.COMPUTER_MEDIUM);
		CurseCard curse = new CurseCard(Card.CU_LOSE_1_LEVEL_2, "Lose 1 Level(1)") {
			public void addEffects(Player player) {
				player.goDownLevel();
			}
		};
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(medium);
		GM.newTestGame(new MockGUI(0), players);
		
		medium.goUpLevel(false);
		assertEquals(medium.getLevel(), 2);
		(new CurseDialog(medium, curse, true)).setVisible(true);
		assertEquals(medium.getLevel(), 1);
		
		medium.goUpLevel(false);
		medium.getEquippedItems().add(new EquipmentCard(Card.E_SANDALS_OF_PROTECTION, "Sandals of Protection", 700, 0, EquipmentType.FOOTGEAR, false, true, false));
		assertEquals(medium.getLevel(), 2);
		(new CurseDialog(medium, curse, false)).setVisible(true);
		assertEquals(medium.getLevel(), 1);
		
		medium.goUpLevel(false);
		assertEquals(medium.getLevel(), 2);
		(new CurseDialog(medium, curse, true)).setVisible(true);
		assertEquals(medium.getLevel(), 2);
		
		medium.addCard(new ItemCard(Card.I_WISHING_RING_1, "Wishing Ring", 100));
		(new CurseDialog(medium, curse, false)).setVisible(true);
		assertEquals(medium.getLevel(), 1);
		
		try {
			assertTrue(CardPlayManager.playCard(medium, medium.getHandCards().getFirst()));
		} 
		catch (EndGameException e) { fail("Is Not Game End"); }
		assertEquals(medium.getCarriedItems().size(), 1);
		medium.goUpLevel(false);
		assertEquals(medium.getLevel(), 2);
		(new CurseDialog(medium, curse, false)).setVisible(true);
		assertEquals(medium.getLevel(), 2);
		assertEquals(medium.getCarriedItems().size(), 0);
	}
}
