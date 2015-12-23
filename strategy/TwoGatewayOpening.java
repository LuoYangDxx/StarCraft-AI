package strategy;

import bwapi.UnitType;
import main.Bot;

public class TwoGatewayOpening extends Strategy {
	
	public TwoGatewayOpening(Bot r) {
		super(r);
	}
	
	@Override
	public void onFrame() {
		
		int probe = root.util.countUnit(UnitType.Protoss_Probe, false, true, true);
		
		if(probe >= 8)
		{
			root.goal.setGoal(UnitType.Protoss_Zealot, 2);
		}
		
		if(probe >= 10)
			root.blackboard.setFirstTimeScout();
		
		if(probe >= 11)
		{
			root.goal.setGoal(UnitType.Protoss_Gateway, 2);
			root.goal.setGoal(UnitType.Protoss_Zealot, 5);
		}
		
	}

	@Override
	public boolean ended() {
		if(root.util.countUnit(UnitType.Protoss_Gateway, false, false, true) >= 2 && root.util.countUnit(UnitType.Protoss_Zealot, true, true, true) >= 5)
			return true;
		return false;
	}

	@Override
	public String getName() {
		
		return "TwoGatewayOpening";
	}
	

}
