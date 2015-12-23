package component;

import task.AllMapScout;
import task.FirstTimeScout;
import main.Bot;

public class ScoutManager extends Component {
	
	boolean firstTimeScout;
	boolean allMapScout;
	
	public ScoutManager(Bot r) {
		super(r);
		
		firstTimeScout = false;
		allMapScout = false;
	}
	
	@Override
	public int getResourcePriority() {
		
		return 0;
	}
	
	@Override
	public String getName() {
		return "ScoutManager";
	}
	
	
	
	@Override
	public void onFrame() {
		
		if(firstTimeScout == false && root.blackboard.getFirstTimeScout())
		{
			FirstTimeScout task = new FirstTimeScout(root);
			if(makeProposal(task))
				firstTimeScout = true;
		}
		
		if(allMapScout == false)
		{
			AllMapScout task = new AllMapScout(root);
			if(makeProposal(task))
				allMapScout = true;
		}
		
	}
	
}
