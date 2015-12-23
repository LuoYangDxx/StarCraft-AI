package headquarter;

import task.Task;
import component.Component;
import main.*;

public class EconomyHQ implements HQ{
	
	Bot root;
	
	public EconomyHQ(Bot r)
	{
		root = r;
	}
	
	@Override
	public void onFrame() {
		
		/* 
		 * 1. Give resource to components by their ResourcePriority
		 * 2. execute onFrame() of each component
		 */
		
		int minerals = root.self.minerals();
		int gas = root.self.gas();
		
		for(Task t : root.listOfTasks)
		{
			minerals -= t.needMinerials();
			gas -= t.needGas();
		}
		
		int nComponent = root.listOfComponents.size();
		Component[] lis = new Component[nComponent];
		for(int i = 0; i < nComponent; i++)
			lis[i] = root.listOfComponents.get(i);
		for(int iteration = 1; iteration < nComponent; iteration++)
			for(int i = 0; i < nComponent-1; i++)
				if(lis[i].getResourcePriority() < lis[i+1].getResourcePriority())
				{
					Component t = lis[i];
					lis[i] = lis[i+1];
					lis[i+1] = t;
				}
		
		for(int i = 0; i < nComponent; i++)
		{
			Component c = lis[i];
			
			c.haveMinerals = minerals;
			c.haveGas = gas;
			
			c.onFrame();
			
			minerals = c.haveMinerals;
			gas = c.haveGas;
		}
		
	}
	
}
