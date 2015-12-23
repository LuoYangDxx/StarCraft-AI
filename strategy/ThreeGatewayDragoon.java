package strategy;

import bwapi.Color;
import bwapi.Position;
import bwapi.Race;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwapi.UpgradeType;
import bwapi.Utils;
import main.Bot;

public class ThreeGatewayDragoon extends Strategy {

	double displayR;
	
	public ThreeGatewayDragoon(Bot r) {
		super(r);
		displayR = 0;
	}
	
	
	
	@Override
	public void onFrame() {
		
		if(root.info.getReadyBases() == 1)
		{
			if(root.util.countUnit(UnitType.Protoss_Zealot, false, false, true) >= 1)
				root.goal.setGoalAtleast(UnitType.Protoss_Gateway, 2);
			
			root.goal.setGoalAtleast(UnitType.Protoss_Gateway, 3);
			
			root.goal.setGoalAtleast(UnitType.Protoss_Zealot, Math.min(12, root.enemyInfo.getEnemyUnitByType(UnitType.Protoss_Zealot).size() + 1));
			
			if(root.enemy.getRace() == Race.Protoss)
			{
				if(root.util.countUnit(UnitType.Protoss_Zealot, false, false, true) >= root.enemyInfo.getEnemyUnitByType(UnitType.Protoss_Zealot).size())
				{
					root.goal.setGoalAtleast(UnitType.Protoss_Dragoon, 50);
					root.goal.setGoal(UpgradeType.Singularity_Charge);
				}
			}
			else
			{
				root.goal.setGoalAtleast(UnitType.Protoss_Dragoon, 50);
				root.goal.setGoal(UpgradeType.Singularity_Charge);
			}
		}
		
		if(root.info.getReadyBases() >= 2)
		{
			root.goal.setGoalAtleast(UnitType.Protoss_Zealot, 20);
			root.goal.setGoalAtleast(UnitType.Protoss_Dragoon, 20);
			root.goal.setGoal(UpgradeType.Leg_Enhancements);
			root.goal.setGoalAtleast(UnitType.Protoss_Gateway, 3 * root.info.getReadyBases());
		}
		
		// Ending game
		if(root.self.supplyUsed() >= 50 * 2 && root.enemyInfo.getEnemyHaveBase() == false && root.enemyInfo.visitedStartPoint == true)
		{
			root.blackboard.setIsEndingGame(true);
		}
		else
		{
			root.blackboard.setIsEndingGame(false);
		}
		
		
		if(root.info.bases[root.info.bases.length-1].pathFromMyBase == null)
			return;
		TilePosition tp = root.info.bases[root.info.bases.length-1].pathFromMyBase.distToPosition(root.blackboard.getFront());
		Position p = new Position(tp.getX()*32 + 16, tp.getY()*32 + 16);
		
		
		double powerMe = root.util.computePower(root.info.getMyFinishedCombatUnits());
		double powerEnemy = root.util.computePowerEnemy(root.enemyInfo.getEnemyCombatUnits());
		root.guiManager.addDebugInfo(Utils.formatText(powerMe + " v.s. " + powerEnemy, Utils.Green) + "  attack = " + root.blackboard.getIsAttacking());
		
		double powerMeInRange = root.util.computePower(root.util.filterInRange(root.info.getMyFinishedCombatUnits(), p, 32 * 16));
		double powerEnemyInRange = root.util.computePowerEnemy(root.util.filterInRangeEnemy(root.enemyInfo.getEnemyCombatUnits(), p, 32*20));
		
		if(root.blackboard.getIsAttacking())
			displayR += 3;
		else
			displayR -= 3;
		if(displayR < 0)
			displayR += 32 * 10;
		if(displayR > 32 * 10)
			displayR -= 32 * 10;
		root.game.drawCircleMap(p.getX(), p.getY(), (int) displayR, new Color(255, 0, 0));
		root.game.drawCircleMap(p.getX(), p.getY(), (int) 32, new Color(255, 255, 0));
		root.game.drawCircleMap(p.getX(), p.getY(), (int) 40, new Color(255, 255, 0));
		
		
		if(root.blackboard.getIsAttacking())
		{
			if(powerMeInRange < powerEnemyInRange * 1.1)
			{
				root.blackboard.setIsAttacking(false);
			}
		}
		else
		{
			if(powerMeInRange > powerEnemy * 1.2)
			{
				root.blackboard.setIsAttacking(true);
			}
		}
		
		if(root.blackboard.getIsAttacking() && powerMeInRange > powerEnemy && powerEnemyInRange < powerMeInRange / 2)
		{
			root.blackboard.setFront(Math.min(root.blackboard.getFront() + 20, root.info.bases[root.info.bases.length-1].pathFromMyBase.totalDistance));
		}
		
		if(root.blackboard.getIsAttacking() == false && powerMeInRange < powerEnemyInRange * 1.5)
		{
			root.blackboard.setFront(Math.max(10, root.blackboard.getFront() - 20));
		}
	
		
			
	}

	@Override
	public boolean ended() {
		
		return false;
	}

	@Override
	public String getName() {
		
		return "ThreeGatewayDragoon";
	}

}
