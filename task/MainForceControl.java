package task;

import information.PathInfo;

import java.util.ArrayList;
import java.util.List;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import main.Bot;

public class MainForceControl extends Task {

	List <Unit> soldier;
	
	public MainForceControl(Bot r) {
		super(r);
		soldier = new ArrayList<Unit>();
		
	}
	
	@Override
	public String getName() {
		
		return "MainForceControl";
	}
	
	
	
	@Override
	public void onFrame() {
		root.guiManager.addDebugInfo("Attack!");
		root.game.drawCircleMap(root.blackboard.getAttackPosition().getX(), root.blackboard.getAttackPosition().getY(), (int)information.GlobalConstant.Squad_Radius, new Color(255, 0, 0));
		
		// remove dead soldier
		ArrayList<Unit> t = new ArrayList<Unit>();
		for(Unit u : soldier)
		{
			if(root.info.getUnitInfo(u).destroy)
				continue;
			t.add(u);
		}
		soldier = t;
		
		// get new soldier
		for(UnitType ut : root.goal.armyUnit)
		{
			if(root.util.isCombatUnit(ut) == false) continue;
			if(root.info.myUnits.get(ut) != null)
				for(Unit u : root.info.myUnits.get(ut))
					if(root.info.canStartNewTask(u))
					{
						root.info.getUnitInfo(u).currentTask = this;
						soldier.add(u);
					}
		}
		
		// ending game
		if(root.blackboard.getIsEndingGame())
		{
			for(int i = 0; i < soldier.size(); i++)
			{
				Position p = null;
				if(root.enemyInfo.getEnemyBuildings().size() > 0)
				{
					p = root.enemyInfo.getEnemyBuildings().get(i % root.enemyInfo.getEnemyBuildings().size()).lastPosition;
				}
				else
				{
					p = root.info.bases[i % root.info.bases.length].position;
				}
				if(root.game.getFrameCount() - root.info.getUnitInfo(soldier.get(i)).lastCommandFrame > 50)
				{
					root.info.getUnitInfo(soldier.get(i)).lastCommandFrame = root.game.getFrameCount();
					soldier.get(i).attack(p);
				}
			}
		}
		else
		{
			for(Unit u : soldier)
			{
				if(root.info.bases[root.info.bases.length-1].pathFromMyBase == null)
					continue;
				PathInfo pi = root.info.bases[root.info.bases.length-1].pathFromMyBase;
				Position p = new Position(pi.distToPosition(root.blackboard.getFront()).getX() * 32 + 16, pi.distToPosition(root.blackboard.getFront()).getY() * 32 + 16);
				
				int x = p.getX();
				int y = p.getY();
				
				if(u.getDistance(new Position(x,  y)) > 4 * 32 && root.blackboard.getIsAttacking() == false)
				{
					if(root.game.getFrameCount() % 40 == 0)
						u.move(new Position(x, y));
				}
				else
				{
					if(root.game.getFrameCount() - root.info.getUnitInfo(u).lastCommandFrame > 50)
					{
						List <Unit> opponentInRange = new ArrayList<Unit>();
						for(Unit e : root.enemyInfo.enemies)
						{
							if(root.util.isCombatUnit(e.getType()))
								if(e.getPosition().getDistance(new Position(x, y)) < information.GlobalConstant.Squad_Radius)
								{
									opponentInRange.add(e);
								}
						}
						if(opponentInRange.size() == 0)
						{
							u.attack(new Position(x, y));
						}
						else
						{
							Unit near = opponentInRange.get(0);
							for(Unit e : opponentInRange)
							{
								if(e.getPosition().getDistance(new Position(x, y)) < near.getPosition().getDistance(new Position(x, y)))
									near = e;
							}
							u.attack(near.getPosition());
						}
						root.info.getUnitInfo(u).lastCommandFrame = root.game.getFrameCount();
					}
				}
			}
		}
		
	}

	@Override
	public boolean checkPossible() {
		return true;
	}

	@Override
	public void init() {
		state = TaskState.ACTIVE;
	}

	@Override
	public void checkEnd() {
		
	}

	@Override
	public int needMinerials() {
		
		return 0;
	}

	@Override
	public int needGas() {
		
		return 0;
	}

	@Override
	public int needSupply() {
		
		return 0;
	}

	@Override
	public int needUnit(UnitType u) {
		
		return 0;
	}

	@Override
	public int provideUnit(UnitType u) {
		
		return 0;
	}

	@Override
	public int provideSupply() {
		
		return 0;
	}

	@Override
	public List<Unit> requestUnit(UnitType requestUnit, int numberNeeded) {
		List<Unit> ret = new ArrayList<Unit>();
		List <Unit> nextsoldier = new ArrayList<Unit>();
		for(int i = 0; i < soldier.size(); i++)
		{
			if(numberNeeded > 0 && soldier.get(i).getType() == requestUnit)
			{
				numberNeeded --;
				ret.add(soldier.get(i));
				root.info.setTask(soldier.get(i), null);
			}
			else
				nextsoldier.add(soldier.get(i));
		}
		soldier = nextsoldier;
		return ret;
	}
	
}
