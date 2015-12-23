package task;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import main.Bot;
import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class ShuttleReaverControl extends Task {

	public ShuttleReaverControl(Bot r) {
		super(r);
	}
	
	@Override
	public String getName() {
		return "ShuttleReaverControl";
	}
	
	Unit shuttle;
	Unit reaver;
	
	@Override
	public void onFrame() {
		
		// check if our shuttle and reaver still exist.
		if(shuttle != null && root.info.getUnitInfo(shuttle).destroy) shuttle = null;
		if(reaver != null && root.info.getUnitInfo(reaver).destroy) reaver = null;
		
		if(shuttle == null)
		{
			for(Unit u : root.info.getMyUnitsByType(UnitType.Protoss_Shuttle))
			{
				if(root.info.canStartNewTask(u))
				{
					root.info.setTask(u, this);
					shuttle = u;
					break;
				}
			}
		}
		
		if(reaver == null)
		{
			for(Unit u : root.info.getMyUnitsByType(UnitType.Protoss_Reaver))
			{
				if(root.info.canStartNewTask(u))
				{
					root.info.setTask(u, this);
					reaver = u;
					break;
				}
			}
		}
		
		if(shuttle == null || reaver == null) return;
		
		
		// make sure it has some scarab
		if(reaver.getScarabCount() <= 5)
		{
			reaver.train(UnitType.Protoss_Scarab);
		}
 		
		if(root.info.bases[root.info.bases.length-1].pathFromMyBase != null)
		{
			TilePosition tp = root.info.bases[root.info.bases.length-1].pathFromMyBase.distToPosition(root.blackboard.getFront());
			Position p = new Position(tp.getX() * 32 + 16, tp.getY() * 32 + 16);
			
			if(reaver.isLoaded())
			{
				if(shuttle.getDistance(p) > 32 * 5)
				{
					if(root.game.getFrameCount() % 30 == 0)
						shuttle.move(p);
				}
				else{
					if(root.game.getFrameCount() % 30 == 0)
						shuttle.unloadAll();
				}
			}
			else
			{
				
				
				if(shuttle.getDistance(p) > 32 * 8)
				{
					if(root.game.getFrameCount() % 30 == 0)
					{
						
						System.out.println("load!");
						shuttle.load(reaver);
					}
				}
				else
				{
					if(root.game.getFrameCount() % 30 == 0)
						shuttle.move(reaver.getPosition());
				}
				
				if(root.game.getFrameCount() % 30 == 0)
					reaver.attack(reaver.getPosition());
				
			}
		}
		
	}

	@Override
	public boolean checkPossible() {
		return true;
	}

	@Override
	public void init() {
		state = TaskState.ACTIVE;
	}

	@Override
	public void checkEnd() {
		
		
	}

	@Override
	public int needMinerials() {
		
		return 0;
	}

	@Override
	public int needGas() {
		
		return 0;
	}

	@Override
	public int needSupply() {
		
		return 0;
	}

	@Override
	public int needUnit(UnitType u) {
		
		return 0;
	}

	@Override
	public int provideUnit(UnitType u) {
		
		return 0;
	}

	@Override
	public int provideSupply() {
		
		return 0;
	}

	@Override
	public List<Unit> requestUnit(UnitType requestUnit, int numberNeeded) {
		List <Unit> ret = new ArrayList<Unit>();
		return ret;
	}

}
