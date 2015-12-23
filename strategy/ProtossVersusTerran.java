package strategy;

import bwapi.UnitType;
import main.Bot;

public class ProtossVersusTerran extends Strategy {

	public ProtossVersusTerran(Bot r) {
		super(r);
	}
	
	Strategy currentOpening;
	
	@Override
	public void onFrame() {
		
		if(currentOpening == null)
			currentOpening = new CyberneticsCoreOpening(root);
		
		if(currentOpening.ended())
		{
			if(currentOpening.getName() == "CyberneticsCoreOpening")
			{
				currentOpening = new ThreeGatewayDragoon(root);
			}
			
			
		}
		else
		{
			currentOpening.onFrame();
		}
		
		if(root.self.supplyUsed() >= 60 * 2)
		{
			root.goal.setGoal(UnitType.Protoss_Observer, 2);
		}
		
		if(root.self.supplyUsed() >= 50 * 2)
			root.blackboard.setNumberOfBaseAtLesst(2);
		if(root.self.supplyUsed() >= 100 * 2)
			root.blackboard.setNumberOfBaseAtLesst(3);
		if(root.self.supplyUsed() >= 150 * 2)
			root.blackboard.setNumberOfBaseAtLesst(4);
		
	}

	@Override
	public boolean ended() {
		
		return false;
	}

	@Override
	public String getName() {
		return "ProtossVersusTerran";
	}

}
