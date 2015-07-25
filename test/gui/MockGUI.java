
package gui;

import java.util.Observable;

import model.Battle;
import model.Player;

public class MockGUI extends GUI {	
	// required for serialization
	private static final long serialVersionUID = 1L;
	
	public MockGUI(int mockGUI) {
		super(mockGUI);
	}
	
	@Override
	public void showMainDisplay() {}
	@Override
	public void hideMainDisplay() {}
	@Override
	public void updateGameDisplay() {}
	@Override
	public void displayHand(Player player) {}
	@Override
	public void displayEquipment(Player player) {}
	@Override
	public void displayCarriedItems(Player player) {}
	@Override
	public void displayBattleItems(Player player) {}
	@Override
	public void displaySellItems(Player player) {}
	@Override
	public void displayTradeItems(Player player) {}
	@Override
	public void displayDiscardRace(Player player) {}
	@Override
	public void displayDiscardClass(Player player) {}
	@Override
	public void displayTurningAbility(Player player) {}
	@Override
	public void displayCharmMonster(Player player) {}
	@Override
	public void displayBerserkingAbility(Player player) {}
	@Override
	public void displayBackstabPlayer(Player player) {}
	@Override
	public void displayStealItems(Player player) {}
	@Override
	public void beginBattle(Battle battle) {}
	@Override
	public void update(Observable o, Object arg) {}
	@Override
	public void updatePlayerMenu(Player player) {}
	@Override
	public void updatePlayerMenus() {}
	@Override
	public void endGame() {}
}
