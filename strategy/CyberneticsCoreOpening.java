package strategy;

import bwapi.UnitType;
import main.Bot;

public class CyberneticsCoreOpening extends Strategy {

	public CyberneticsCoreOpening(Bot r) {
		super(r);
	}
	
	@Override
	public void onFrame() {
		
		int probe = root.util.countUnit(UnitType.Protoss_Probe, false, true, true);
		
		if(probe >= 8)
		{
			root.goal.setGoal(UnitType.Protoss_Gateway);
		}
		
		if(probe >= 10)
		{
			root.blackboard.setHaveGasBuilding(true);
			root.goal.setGoal(UnitType.Protoss_Cybernetics_Core);
			root.blackboard.setFirstTimeScout();
		}
		
	}
	
	@Override
	public boolean ended() {
		return root.util.countUnit(UnitType.Protoss_Cybernetics_Core, false, false, true) >= 1;
	}
	
	@Override
	public String getName() {
		return "CyberneticsCoreOpening";
	}

}
