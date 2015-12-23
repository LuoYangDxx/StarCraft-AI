package task;

import java.util.ArrayList;
import java.util.List;

import main.Bot;
import information.BaseInfo;
import bwapi.PositionOrUnit;
import bwapi.Unit;
import bwapi.UnitType;

public class GatherResource extends Task {

	BaseInfo base;
	
	List<Unit> workerForMinearals;
	List<Unit> workerForGas;
	
	@Override
	public String getName() {
		return "GatherResource";
	}
	
	public GatherResource(Bot r, BaseInfo b) {
		super(r);
		base = b;
		workerForMinearals = new ArrayList<Unit>();
		workerForGas = new ArrayList<Unit>();
	}
	
	public int getWorkerCountForMinerals()
	{
		return workerForMinearals.size();
	}
	
	public void addWorkerForMinerals(Unit worker)
	{
		workerForMinearals.add(worker);
		root.info.getUnitInfo(worker).currentTask = this;
	}
	
	@Override
	public void onFrame() {
		
		workerForGas = root.util.removeDead(workerForGas);
		workerForMinearals = root.util.removeDead(workerForMinearals);
		
		
		if(base.gasStation.size() > 0 && base.gasStation.get(0).isBeingConstructed() == false)
		{
			int nReq = 3 - workerForGas.size();
			if(nReq > 0)
			{
				List<Unit> toGas = requestUnit(UnitType.Protoss_Probe, nReq);
				for(Unit u : toGas)
				{
					workerForGas.add(u);
					root.info.setTask(u, this);
				}
				//System.out.println(workerForGas.size());
			}
			for(int i = 0; i < workerForGas.size(); i++)
			{
				Unit u = workerForGas.get(i);
				if(u.getShields() > 15)
				{
					if(workerForGas.get(i).isGatheringGas())
					{
						if(workerForGas.get(i).isCarryingGas() == false)
							if(workerForGas.get(i).getTarget() != null)
								if(workerForGas.get(i).getTarget().getID() != base.gasStation.get(0).getID())
								{
									workerForGas.get(i).gather(base.gasStation.get(0));
								}
					}
					else {
						//System.out.println(base.gasStation.get(0).getType());
						workerForGas.get(i).gather(base.gasStation.get(0));
						//root.game.drawLineMap(workerForGas.get(i).getX(), workerForGas.get(i).getY(), base.gasStation.get(0).getX(), base.gasStation.get(0).getY(), new Color(255, 0, 0));
					}
				}
				else
				{
					if(root.game.getFrameCount() - root.info.getUnitInfo(u).lastCommandFrame > 50)
					{
						u.attack(u.getPosition());
						root.info.getUnitInfo(u).lastCommandFrame = root.game.getFrameCount();
					}
				}
			}
			
		}
		
		
		if(base.avaliableMinerals > 0)
			for(int i = 0; i < workerForMinearals.size(); i++)
			{
				Unit u = workerForMinearals.get(i);
				if(u.getShields() > 15)
				{
					if(workerForMinearals.get(i).isGatheringMinerals())
					{
						if(workerForMinearals.get(i).isCarryingMinerals() == false)
							if(workerForMinearals.get(i).getTarget() != null)
								if(workerForMinearals.get(i).getTarget().getID() != base.avaliableMineralsUnit[i % base.avaliableMinerals].getID())
								{
									workerForMinearals.get(i).gather(base.avaliableMineralsUnit[i % base.avaliableMinerals]);
								}
					}
					else {
						workerForMinearals.get(i).gather(base.avaliableMineralsUnit[i % base.avaliableMinerals]);
					}
				}
				else
				{
					if(root.game.getFrameCount() - root.info.getUnitInfo(u).lastCommandFrame > 50)
					{
						u.attack(u.getPosition());
						root.info.getUnitInfo(u).lastCommandFrame = root.game.getFrameCount();
					}
				}
			}
	}
	
	@Override
	public boolean checkPossible() {
		if(base.gatherResourceTask != null) return false;
		if(base.canWalkTo == false) return false;
		if(base.myBase == null) return false;
		if(root.info.getUnitInfo(base.myBase).destroy) return false;
		
		return true;
	}

	@Override
	public void init() {
		state = TaskState.ACTIVE;
		base.gatherResourceTask = this;
		
	}

	@Override
	public void checkEnd() {
		if(base.myBase == null || root.info.getUnitInfo(base.myBase).destroy)
		{
			state = TaskState.FAILED;
			base.gatherResourceTask = null;
			for(int i = 0; i < workerForMinearals.size(); i++)
			{
				root.info.setTask(workerForMinearals.get(i), null);
			}
			workerForMinearals.clear();
		}
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
	public int provideSupply() {
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
	public List<Unit> requestUnit(UnitType requestUnit, int numberNeeded) {
		
		List <Unit> ret = new ArrayList<Unit>();
		
		if(requestUnit != UnitType.Protoss_Probe)
			return ret;
		
		for(int i = workerForMinearals.size()-1; i >= 0; i--)
			if(numberNeeded > 0)
			{
				numberNeeded -= 1;
				Unit u = workerForMinearals.get(i);
				workerForMinearals.remove(i);
				root.info.getUnitInfo(u).currentTask = null;
				ret.add(u);
			}
		return ret;
	}
}
