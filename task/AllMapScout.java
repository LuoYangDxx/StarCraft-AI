package task;

import java.util.ArrayList;
import java.util.List;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import main.Bot;

public class AllMapScout extends Task {
	
	Unit ob;
	int prevX, prevY;
	int targetX, targetY;
	
	public AllMapScout(Bot r) {
		super(r);
	}

	@Override
	public String getName() {
		return "AllMapScout";
	}

	@Override
	public void onFrame() {
		
		if(ob == null)
		{
			for(Unit u : root.info.getMyUnitsByType(UnitType.Protoss_Observer))
			{
				if(root.info.canStartNewTask(u))
				{
					ob = u;
					root.info.setTask(ob, this);
					prevX = u.getPosition().getX() / 410;
					prevY = u.getPosition().getY() / 410;
					targetX = prevX;
					targetY = prevY;
				}
			}
		}
		
		if(ob == null)
			return;
		
		if(root.allMapInfo.isDanger[targetX][targetY])
		{
			targetX = prevX;
			targetY = prevY;
		}
		
		Position targetP = root.allMapInfo.getPosition(targetX, targetY);
		ob.move(targetP);
		if(ob.getPosition().getDistance(targetP) < 16)
		{
			root.allMapInfo.visit(targetX, targetY);
			prevX = targetX;
			prevY = targetY;
			Position t = root.allMapInfo.getNext(prevX, prevY);
			targetX = t.getX();
			targetY = t.getY();
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
		if(u == UnitType.Protoss_Observer)
			return 1;
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
		List <Unit> ret = new ArrayList<Unit>(); 
		return ret;
	}

}
