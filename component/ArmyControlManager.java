package component;

import java.util.ArrayList;
import java.util.List;

import bwapi.UnitType;
import task.MainForceControl;
import task.ShuttleReaverControl;
import task.Task;
import main.Bot;

public class ArmyControlManager extends Component {

	public Task mainForceControlTask;
	public List<Task> shuttleReaverControlTasks;
	
	public ArmyControlManager(Bot r) {
		super(r);
		shuttleReaverControlTasks = new ArrayList<Task>();
	}
	
	@Override
	public int getResourcePriority() {
		return 700;
	}

	@Override
	public String getName() {
		return "ArmyControlManager";
	}

	@Override
	public void onFrame() {
		
		if(mainForceControlTask == null)
		{
			mainForceControlTask = new MainForceControl(root);
			makeProposal(mainForceControlTask);
		}
		
		int reaver = root.util.countUnit(UnitType.Protoss_Reaver, true, true, true);
		int shuttle = root.util.countUnit(UnitType.Protoss_Shuttle, true, true, true);
		int reaverShuttlePairs = Math.min(reaver, shuttle);
		
		while(shuttleReaverControlTasks.size() <= reaverShuttlePairs)
		{
			Task t = new ShuttleReaverControl(root);
			makeProposal(t);
			shuttleReaverControlTasks.add(t);
		}
	}

}
