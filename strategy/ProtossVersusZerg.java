package strategy;

import main.Bot;

public class ProtossVersusZerg extends Strategy {

	public ProtossVersusZerg(Bot r) {
		super(r);
	}
	
	Strategy currentOpening;

	@Override
	public void onFrame() {
		
		if(currentOpening == null)
			currentOpening = new TwoGatewayOpening(root);
		if(currentOpening.ended())
		{
			if(currentOpening.getName() == "TwoGatewayOpening")
			{
				currentOpening = new OneBaseReaver(root);
				
				
				
			}
			
			
		}
		else
		{
			currentOpening.onFrame();
		}
		
		/*
		if(root.self.supplyUsed() >= 50 * 2)
			root.blackboard.setNumberOfBaseAtLesst(2);
		if(root.self.supplyUsed() >= 100 * 2)
			root.blackboard.setNumberOfBaseAtLesst(3);
		if(root.self.supplyUsed() >= 150 * 2)
			root.blackboard.setNumberOfBaseAtLesst(4);*/
		
	}

	@Override
	public boolean ended() {
		
		return false;
	}

	@Override
	public String getName() {
		return "ProtossVersusZerg";
	}

}
