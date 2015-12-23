package component;

import main.Bot;
import task.Task;

public abstract class Component {
	
	public Bot root;
	
	public int haveMinerals;
	public int haveGas;
	
	public boolean enabled;
	
	public Component(Bot r) {
		root = r;
		haveMinerals = 0;
		haveGas = 0;
		enabled = true;
	}
	
	abstract public int getResourcePriority();
	abstract public String getName();
	
	public void onFrameStart()
	{
		
	}
	
	public boolean makeProposal(Task t)
	{
		if(t.checkPossible() == false) return false;
		
		boolean fail = false;
		
		if(t.needMinerials() > haveMinerals)
		{
			haveMinerals = 0;
			fail = true;
		}
		if(t.needGas() > haveGas)
		{
			haveGas = 0;
			fail = true;
		}
		
		if(fail)
			return false;
		
		haveMinerals -= t.needMinerials();
		haveGas -= t.needGas();
		t.creator = this;
		t.root = root;
		t.init();
		root.listOfTasks.add(t);
		return true;
	}
	
	abstract public void onFrame();
}
