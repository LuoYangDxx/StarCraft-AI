package component;

import task.BuildStruct;
import task.Task;
import bwapi.Unit;
import bwapi.UnitType;
import main.Bot;

public class SupplyManager extends Component {

	public SupplyManager(Bot r) {
		super(r);
	}

	@Override
	public int getResourcePriority() {
		return 400;
	}

	@Override
	public String getName() {
		return "SupplyManager";
	}

	@Override
	public void onFrame() {
		
		int usedSupply = root.self.supplyUsed();
		int haveSupply = root.self.supplyTotal();
		for(Task t : root.listOfTasks)
		{
			usedSupply += t.needSupply();
			haveSupply += t.provideSupply();
		}
		if(root.info.myUnits.get(UnitType.Protoss_Pylon) != null)
			for(Unit u : root.info.myUnits.get(UnitType.Protoss_Pylon))
				if(u.isBeingConstructed())
					haveSupply += u.getType().supplyProvided();
		
		if(root.info.myUnits.get(UnitType.Protoss_Gateway) != null)
			for(Unit u : root.info.myUnits.get(UnitType.Protoss_Gateway))
				if(u.isBeingConstructed() == false)
					usedSupply += 2;
		
		
		
		boolean needNewSupply = false;
		if(haveSupply <= 10 * 2)
			needNewSupply = (haveSupply - usedSupply <= 2 * 2);
		else if(haveSupply <= 20 * 2)
			needNewSupply = (haveSupply - usedSupply <= 2 * 2);
		else if(haveSupply <= 30 * 2)
			needNewSupply = (haveSupply - usedSupply <= 4 * 2);
		else
			needNewSupply = (haveSupply - usedSupply <= 8 * 2);
		
		if(haveSupply >= 200 * 2)
			needNewSupply = false;
		
		
		root.guiManager.addDebugInfo((usedSupply/2) + "/" + (haveSupply/2) + " -> " + needNewSupply);
		
		if(needNewSupply)
		{
			BuildStruct task = new BuildStruct(root, root.info.bases[0], UnitType.Protoss_Pylon);
			makeProposal(task);
		}
		
		
	}

}
