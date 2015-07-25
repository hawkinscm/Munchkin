
package model;

import model.card.CardTest;
import model.card.EquipmentCardTest;
import ai.AIBattleManagerTest;
import ai.AICardEvaluatorTest;
import ai.AIManagerTest;
import ai.BattleActionTest;
import ai.BattleMonsterInfoTest;
import gui.AskHelpDialogTest;
import gui.BattlePanelTest;
import gui.CharityDialogTest;
import gui.CurseDialogTest;
import gui.GUI;
import gui.HelpMeOutDialogTest;
import gui.LoseCardsDialogTest;
import gui.LoseGPDialogTest;
import gui.ResurrectionDialogTest;
import gui.RunDialogTest;
import gui.SellItemsDialogTest;
import gui.StealItemDialogTest;
import gui.TakeCardDialogTest;
import gui.TakeTreasuresPanelTest;
import gui.TradeItemsDialogTest;

public class MunchkinUnitTester extends UnitTester {

	public MunchkinUnitTester() {
		GUI.isTestRun = true;
		GUI.isDebug = false;
		GUI.isCheating = false;
		
		addUnitTest(new CardTest());
		addUnitTest(new PlayerTest());
		addUnitTest(new EquipmentCardTest());
		addUnitTest(new GMTest());
		addUnitTest(new CardPlayManagerTest());
		addUnitTest(new DoorDeckTest());
		addUnitTest(new DoorDeckFactoryTest());
		addUnitTest(new TreasureDeckTest());
		addUnitTest(new TreasureDeckFactoryTest());		
		addUnitTest(new BattleTest());
		
		addUnitTest(new AskHelpDialogTest());
		addUnitTest(new BattlePanelTest());
		addUnitTest(new CharityDialogTest());
		addUnitTest(new CurseDialogTest());
		addUnitTest(new HelpMeOutDialogTest());
		addUnitTest(new LoseCardsDialogTest());
		addUnitTest(new LoseGPDialogTest());
		addUnitTest(new ResurrectionDialogTest());
		addUnitTest(new RunDialogTest());
		addUnitTest(new SellItemsDialogTest());
		addUnitTest(new StealItemDialogTest());
		addUnitTest(new TakeCardDialogTest());
		addUnitTest(new TakeTreasuresPanelTest());
		addUnitTest(new TradeItemsDialogTest());
		
		addUnitTest(new AICardEvaluatorTest());
		addUnitTest(new AIManagerTest());
		addUnitTest(new BattleActionTest());
		addUnitTest(new BattleMonsterInfoTest());
		addUnitTest(new AIBattleManagerTest());		
		
		testAll();
	}
	
	public static void main(String args[]) {
		new MunchkinUnitTester();
		System.exit(0);
	}
}
