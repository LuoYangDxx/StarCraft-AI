package headquarter;

import strategy.ProtossVersusProtoss;
import strategy.ProtossVersusTerran;
import strategy.ProtossVersusZerg;
import bwapi.Race;
import main.Bot;

public class StrategyHQ implements HQ {
	
	Bot root;
	
	public StrategyHQ(Bot r) {
		root = r;
	}
	
	@Override
	public void onFrame() {
		
		if(root.strategy == null)
		{
			if(root.enemy.getRace() == Race.Protoss)
				root.strategy = new ProtossVersusProtoss(root);			
			else if(root.enemy.getRace() == Race.Terran)
				root.strategy = new ProtossVersusTerran(root);
			else
				root.strategy = new ProtossVersusZerg(root);
		}
		
		if(root.strategy != null)
			root.strategy.onFrame();
		
	}

}
