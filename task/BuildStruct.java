package task;

import java.util.ArrayList;
import java.util.List;

import information.BaseInfo;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import main.Bot;

public class BuildStruct extends Task {
	
	BaseInfo aroundBase;
	UnitType targetStruct;
	Unit worker;
	TilePosition buildPosition;
	int lastMoveFrame;
	int lastMoveFrameForSoldier;
	int lastBuildFrame;
	Unit soldier;
	boolean soldierArrived;
	
	public BuildStruct(Bot r, BaseInfo b, UnitType target) {
		super(r);
		aroundBase = b;
		targetStruct = target;
		lastMoveFrame = 0;
		lastMoveFrameForSoldier = 0;
		lastBuildFrame = 0;
		soldierArrived = false;
	}
	
	@Override
	public String getName() {
		
		return "BuildStruct " + targetStruct;
	}

	@Override
	public void onFrame() {
		
		/*
		 * Special instruction for put new building (against mine)
		 * 1. Put a zealot into there
		 * 2. keep d(zealot, base) - d(probe, base) > 32 * 5
		 * 3. when d(zealot, base) < 32 * 2 -> zealot move back
		 */
		
		
		// 1. get a worker
		
		if(worker != null && root.info.getUnitInfo(worker).destroy)
			worker = null;
		
		if(soldier != null && root.info.getUnitInfo(soldier).destroy)
			soldier = null;
		
		if(worker == null)
		{
			if(aroundBase.gatherResourceTask != null && aroundBase.gatherResourceTask.state == TaskState.ACTIVE)
			{
				List <Unit> t = aroundBase.gatherResourceTask.requestUnit(UnitType.Protoss_Probe, 1);
				if(t.size() > 0)
				{
					worker = t.get(0);
					root.info.setTask(worker, this);
				}
			}
			if(worker == null)
				for(int i = 0; i < root.blackboard.getNumberOfBase(); i++)
				{
					BaseInfo thisBase = root.info.bases[i];
					if(thisBase.gatherResourceTask != null && thisBase.gatherResourceTask.state == TaskState.ACTIVE)
					{
						List <Unit> t = thisBase.gatherResourceTask.requestUnit(UnitType.Protoss_Probe, 1);
						if(t.size() > 0 && worker == null)
						{
							worker = t.get(0);
							root.info.setTask(worker, this);
						}
					}
				}
			
			if(worker != null)
				root.info.getUnitInfo(worker).currentTask = this;
			else
				return;
		}
		
		// 2. get a zealot (if no then a dragoon) if we want to build a base
		
		if(root.util.isBase(targetStruct) && soldier == null)
		{
			if(root.armyControlManager.mainForceControlTask == null)
				return;
			List <Unit> t = root.armyControlManager.mainForceControlTask.requestUnit(UnitType.Protoss_Zealot, 1);
			if(t.size() != 0)
				soldier = t.get(0);
			t = root.armyControlManager.mainForceControlTask.requestUnit(UnitType.Protoss_Dragoon, 1);
			if(t.size() != 0)
				soldier = t.get(0);
			root.info.setTask(soldier, this);
			if(soldier == null)
				return;
		}
		
		if(root.util.isBase(targetStruct))
		{
			if(soldierArrived)
			{
				if(worker.getDistance(root.util.toBuildingCenter(buildPosition, targetStruct)) > information.GlobalConstant.Building_Worker_distance)
				{
					if(root.game.getFrameCount() - lastMoveFrame > 30)
					{
						worker.move(root.util.toBuildingCenter(buildPosition, targetStruct));
						lastMoveFrame = root.game.getFrameCount();
					}	
				}
				else
				{
					if(root.game.getFrameCount() - lastBuildFrame > 30)
					{
						lastBuildFrame = root.game.getFrameCount();
						worker.build(buildPosition, targetStruct);
					}
				}
				
				if(root.game.getFrameCount() - lastMoveFrameForSoldier > 30)
				{
					soldier.move(root.util.getMyFirstBasePosition());
					lastMoveFrameForSoldier = root.game.getFrameCount();
				}
			}
			else
			{
				if(root.game.getFrameCount() - lastMoveFrameForSoldier > 30)
				{
					soldier.move(root.util.toBuildingCenter(buildPosition, targetStruct));
					lastMoveFrameForSoldier = root.game.getFrameCount();
				}
				
				if(soldier.getDistance(root.util.toBuildingCenter(buildPosition, targetStruct)) < 2 * 32)
					soldierArrived = true;
				
				double dWorkerBase = worker.getDistance(root.util.toBuildingCenter(buildPosition, targetStruct));
				double dSoldierBase = soldier.getDistance(root.util.toBuildingCenter(buildPosition, targetStruct));
				
				if(dWorkerBase < dSoldierBase + 5 * 32)
				{
					if(root.game.getFrameCount() - lastMoveFrame > 30)
					{
						worker.move(root.util.getMyFirstBasePosition());
						lastMoveFrame = root.game.getFrameCount();
					}	
				}
				else
				{
					if(root.game.getFrameCount() - lastBuildFrame > 30)
					{
						worker.move(root.util.toBuildingCenter(buildPosition, targetStruct));
						lastMoveFrame = root.game.getFrameCount();
					}
				}
				
			}
		}
		else
		{
			if(worker.getDistance(root.util.toBuildingCenter(buildPosition, targetStruct)) > information.GlobalConstant.Building_Worker_distance)
			{
				if(root.game.getFrameCount() - lastMoveFrame > 30)
				{
					worker.move(root.util.toBuildingCenter(buildPosition, targetStruct));
					lastMoveFrame = root.game.getFrameCount();
				}	
			}
			else
			{
				if(root.game.getFrameCount() - lastBuildFrame > 30)
				{
					lastBuildFrame = root.game.getFrameCount();
					worker.build(buildPosition, targetStruct);
				}
			}
		}
		

	}

	@Override
	public boolean checkPossible() {
		return (aroundBase.whereToBuild(targetStruct, false) != null);
	}

	@Override
	public void init() {
		state = TaskState.ACTIVE;
		buildPosition = aroundBase.whereToBuild(targetStruct, true);
		
	}

	@Override
	public void checkEnd() {
		
		if(root.info.myUnits.get(targetStruct) != null)
			for(Unit u : root.info.myUnits.get(targetStruct))
			{
				if(u.getTilePosition().getDistance(buildPosition) == 0)
				{
					state = TaskState.FINISHED;
					root.info.setTask(worker, null);
					root.info.setTask(soldier, null);
					if(root.util.isGasBuilding(targetStruct))
					{
						aroundBase.gasStation.add(u);
					}
					if(root.util.isBase(targetStruct))
					{
						aroundBase.myBase = u;
					}
				}
			}
		
	}

	@Override
	public int needMinerials() {
		
		return targetStruct.mineralPrice();
	}

	@Override
	public int needGas() {
		
		return targetStruct.gasPrice();
	}

	@Override
	public int needSupply() {
		
		return 0;
	}

	@Override
	public int provideSupply() {
		
		return targetStruct.supplyProvided();
	}

	@Override
	public int needUnit(UnitType u) {
		
		return 0;
	}

	@Override
	public int provideUnit(UnitType u) {
		if(u == targetStruct) return 1;
		return 0;
	}

	@Override
	public List<Unit> requestUnit(UnitType requestUnit, int numberNeeded) {
		List <Unit> ret = new ArrayList<Unit>(); 
		return ret;
	}
	
}
