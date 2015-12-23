package component;

import task.BuildStruct;
import bwapi.UnitType;
import main.Bot;

public class TechBuildingManager extends Component {
	
	
	
	public TechBuildingManager(Bot r) {
		super(r);
	}

	@Override
	public int getResourcePriority() {
		return 200;
	}
	
	@Override
	public String getName() {
		return "TechBuildingManager";
	}
	
	@Override
	public void onFrame() {
		
		for(UnitType t : root.goal.techBuildings)
		{
			if(root.util.countUnit(t, true, true, true) < root.goal.getGoal(t))
			{
				if(root.util.isGasBuilding(t))
				{
					root.blackboard.setHaveGasBuilding(true);
				}
				else if(root.goal.finishAllPrerequests(t))
				{
					BuildStruct task = new BuildStruct(root, root.info.bases[0], t);
					makeProposal(task);
				}
			}
		}
		
	}

}
