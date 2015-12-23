package task;

import java.util.ArrayList;
import java.util.List;

import main.Bot;
import bwapi.Unit;
import bwapi.UnitType;

public class TrainUnit extends Task {
	
	Unit building;
	UnitType wantUnit;
	
	public TrainUnit(Bot r, Unit _building, UnitType _wantUnit) {
		super(r);
		building = _building;
		wantUnit = _wantUnit;
	}
	
	@Override
	public String getName() {
		return "TrainUnit(" + wantUnit + ")";
	}
	
	@Override
	public void onFrame() {
		building.train(wantUnit);
	}
	
	@Override
	public boolean checkPossible() {
		if(root.game.canMake(building, wantUnit) == false)
			return false;
		if(building.isTraining())
			return false;
		if(root.info.getTask(building) != null)
			return false;
		return true;
	}
	
	@Override
	public void checkEnd() {
		if(building.isTraining())
		{
			state = TaskState.FINISHED;
			root.info.setTask(building, null);
		}
	}
	
	@Override
	public int needMinerials() {
		return wantUnit.mineralPrice();
	}
	
	@Override
	public int needGas() {
		return wantUnit.gasPrice();
	}
	
	@Override
	public int needSupply() {
		return wantUnit.supplyRequired();
	}

	@Override
	public int provideSupply() {
		return wantUnit.supplyProvided();
	}
	
	@Override
	public void init() {
		state = TaskState.ACTIVE;
		root.info.setTask(building, this);
	}

	@Override
	public int needUnit(UnitType u) {
		return 0;
	}

	@Override
	public int provideUnit(UnitType u) {
		if(u == wantUnit) return 1;
		return 0;
	}

	@Override
	public List<Unit> requestUnit(UnitType requestUnit, int numberNeeded) {
		List <Unit> ret = new ArrayList<Unit>(); 
		return ret;
	}
}
