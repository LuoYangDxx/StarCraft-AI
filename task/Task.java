package task;

import java.util.List;

import main.Bot;
import component.Component;
import bwapi.Unit;
import bwapi.UnitType;

public abstract class Task {
	
	public Bot root;
	
	public int createFrame;
	public int expireTime;
	
	public Component creator;
	
	public enum TaskState
	{
		CREATED,
		ACTIVE,
		FINISHED,
		FAILED
	}
	
	public TaskState state;
	
	public Task(Bot r) {
		state = TaskState.CREATED;
		root = r;
	}
	
	abstract public String getName();
	
	abstract public void onFrame();
	abstract public boolean checkPossible();
	abstract public void init();
	abstract public void checkEnd();
	
	abstract public int needMinerials();
	abstract public int needGas();
	abstract public int needSupply();
	abstract public int needUnit(UnitType u);
	abstract public int provideUnit(UnitType u);
	abstract public int provideSupply();
	abstract public List<Unit> requestUnit(UnitType requestUnit, int numberNeeded);
	
}
