package component;

import task.Upgrade;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import main.Bot;

public class UpgradeManager extends Component {
	
	public UpgradeManager(Bot r) {
		super(r);
	}
	
	@Override
	public int getResourcePriority() {
		
		return 600;
	}
	
	@Override
	public String getName() {
		
		return "UpgradeManager";
	}
	
	@Override
	public void onFrame() {
		
		
		for(UpgradeType ut : root.goal.upgrades)
		{
			if(root.goal.finishAllPrerequests(ut) == false) continue;
			//System.out.println(root.goal.getGoal(ut) + " / " + root.goal.getNow(ut));
			if(root.goal.getNow(ut) < root.goal.getGoal(ut))
			{
				UnitType b = root.goal.getBuilding(ut);
				for(Unit u : root.info.getMyUnitsByType(b))
				{
					if(root.info.canStartNewTask(u))
					{
						Upgrade task = new Upgrade(root, u, ut);
						if(makeProposal(task))
							break;
					}
				}
			}
		}
	}
	
}
