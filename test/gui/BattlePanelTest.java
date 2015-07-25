
package gui;

import java.util.LinkedList;

import model.Battle;
import model.GM;
import model.Player;
import model.PlayerType;
import model.UnitTest;
import model.card.ItemCard;
import model.card.MonsterCard;

public class BattlePanelTest extends UnitTest {
	private static final long serialVersionUID = 1L;
		
	private BattlePanel panel;
	private Battle battle;
	private Player easy;
	private Player medium;
	private Player hard;
	private Player human;
	
	public int testAll() {
		initializeObjects();

		testUpdateDisplay();
		
		return errorCount;
	}
	
	private void initializeObjects() {
		GUI mockGUI = new MockGUI(0);
		
		easy = new Player(mockGUI, "easy", true, PlayerType.COMPUTER_EASY);
		medium = new Player(mockGUI, "medium", false, PlayerType.COMPUTER_MEDIUM);
		hard = new Player(mockGUI, "hard", true, PlayerType.COMPUTER_HARD);
		human = new Player(mockGUI, "human", false, PlayerType.HUMAN);
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(easy);
		players.add(medium);
		players.add(hard);
		players.add(human);
		GM.newTestGame(mockGUI, players);
		
		GM.moveToBattlePhase();
		battle = new Battle(easy, new MonsterCard(1, "test", 3, 1, 1, false) {
			public void doBadStuff(Player player) {}
		});
		
		panel = new BattlePanel(mockGUI, battle);
		panel.setVisible(false);
	}
	
	private void testUpdateDisplay() {
		easy.goUpLevel(false);
		
		assertTrue(panel.getBattle() == battle);
		assertFalse(GM.isAIThinking());
		panel.updateDisplay();
		assertTrue(panel.getBattle() == battle);
		assertFalse(GM.isAIThinking());
		
		ItemCard item = new ItemCard(2, "item", 100, 2);
		easy.getCarriedItems().add(item);
		panel.updateDisplay();
		assertTrue(panel.getBattle() == battle);
		assertFalse(GM.isAIThinking());
		assertTrue(easy.getCarriedItems().isEmpty());
		assertTrue(battle.getPlayerItemCards().remove(item));
		
		easy.getCarriedItems().add(item);
		hard.getCarriedItems().add(item);
		panel.updateDisplay();
		assertFalse(GM.isAIThinking());
		assertTrue(panel.getBattle() == battle);
		assertTrue(easy.getCarriedItems().isEmpty());
		assertTrue(hard.getCarriedItems().isEmpty());
		assertEquals(battle.getPlayerItemCards().size(), 1);
		assertEquals(battle.getMonsterItemCards().size(), 1);
		
		easy.getCarriedItems().add(item);
		human.getCarriedItems().add(item);
		panel.updateDisplay();
		assertFalse(GM.isAIThinking());
		assertTrue(easy.getCarriedItems().isEmpty());
		assertEquals(human.getCarriedItems().size(), 1);
		assertEquals(battle.getPlayerItemCards().size(), 2);
		assertEquals(battle.getMonsterItemCards().size(), 1);
		
		easy.goDownLevel();
		battle.getPlayerItemCards().clear();
		battle.getMonsterItemCards().clear();
		
		battle.addHelper(medium);
		LinkedList<Player> order = new LinkedList<Player>();
		order.add(medium);
		battle.setTakeTreasurePlayerOrder(order);
		medium.getCarriedItems().add(item);
		panel.updateDisplay();
		assertTrue(panel.getBattle() == battle);
		assertFalse(GM.isAIThinking());
		assertTrue(medium.getCarriedItems().isEmpty());
		assertTrue(battle.getPlayerItemCards().remove(item));
		
		medium.getCarriedItems().add(item);
		hard.getCarriedItems().add(item);
		panel.updateDisplay();
		assertFalse(GM.isAIThinking());
		assertTrue(panel.getBattle() == battle);
		assertTrue(medium.getCarriedItems().isEmpty());
		assertTrue(hard.getCarriedItems().isEmpty());
		assertEquals(battle.getPlayerItemCards().size(), 1);
		assertEquals(battle.getMonsterItemCards().size(), 1);
		assertEquals(human.getCarriedItems().size(), 1);
		
		GM.moveToOtherPhase();
		assertTrue(panel.getBattle() == battle);
		panel.updateDisplay();
		assertNull(panel.getBattle());
		assertFalse(GM.isAIThinking());
	}
}
