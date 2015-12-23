package component;

import task.GatherResource;
import task.TrainUnit;
import main.Bot;
import bwapi.Unit;
import bwapi.UnitType;

public class BaseManager extends Component {
	
	public BaseManager(Bot r)
	{
		super(r);
	}
	
	@Override
	public int getResourcePriority() {
		return 300;
	}
	
	@Override
	public String getName() {
		return "BaseManager";
	}
	
	@Override
	public void onFrame() {
		
		//System.out.println("BaseManager have minerals = " + haveMinerals);
		
		/* Tasks:
		 * 1. Train worker
		 * 2. Gather Resource
		 */
		
		for(int i = 0; i < root.blackboard.getNumberOfBase(); i++)
		{
			Unit b = root.info.bases[i].myBase;
			
			if(b == null || root.info.getUnitInfo(b).destroy || b.isBeingConstructed())
				continue;
			
			int myProbes = root.util.countUnit(UnitType.Protoss_Probe, true, true, true);
			
			int myProbesNeed = Math.min(80, root.blackboard.getNumberOfBase() * 27 + 10) ;
			
			if(b.isTraining() == false && myProbes < myProbesNeed)
			{
				TrainUnit task = new TrainUnit(root, b, UnitType.Protoss_Probe);
				makeProposal(task);
			}
			
			if(root.info.bases[i].gatherResourceTask == null)
			{
				GatherResource task = new GatherResource(root, root.info.bases[i]);
				makeProposal(task);
			}
			
		}
		
	}

}
