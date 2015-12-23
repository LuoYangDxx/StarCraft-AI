package task;

import java.util.ArrayList;
import java.util.List;

import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import main.Bot;

public class Upgrade extends Task {
	
	Unit building;
	UpgradeType upgradeType;
	
	public Upgrade(Bot r, Unit b, UpgradeType ut) {
		super(r);
		building = b;
		upgradeType = ut;
	}

	@Override
	public String getName()
	{
		return "Upgrade " + upgradeType;
	}

	@Override
	public void onFrame() {
		building.upgrade(upgradeType);
		
	}

	@Override
	public boolean checkPossible() {
		if(root.game.canUpgrade(building, upgradeType) == false)
			return false;
		return true;
	}

	@Override
	public void init() {
		state = TaskState.ACTIVE;
		root.info.setTask(building, this);
		root.goal.upgrading(upgradeType);
	}

	@Override
	public void checkEnd() {
		if(building.isUpgrading())
		{
			state = TaskState.FINISHED;
			root.info.setTask(building, null);
		}
		
	}

	@Override
	public int needMinerials() {
		return upgradeType.mineralPrice();
	}

	@Override
	public int needGas() {
		return upgradeType.gasPrice();
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
		return ret;
	}
}
