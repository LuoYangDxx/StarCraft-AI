package task;

import java.util.ArrayList;
import java.util.List;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import main.Bot;

public class FirstTimeScout extends Task{

	public Unit worker;
	
	public FirstTimeScout(Bot r) {
		super(r);
	}

	@Override
	public String getName() {
		return "FirstTimeScout";
	}
	
	int currentIndex;
	
	@Override
	public void onFrame() {
		
		if(root.enemyInfo.startPoint == null)
		{
			for(Unit u : root.enemyInfo.enemies)
			{
				if(root.enemyInfo.getUnitInfo(u).unitType.isBuilding())
				{
					root.enemyInfo.startPoint = root.enemyInfo.possibleStartPositions[currentIndex];
					return;
				}
			}
			
			if(currentIndex >= root.enemyInfo.possibleStartPositions.length)
				return;
			
			Position p = root.enemyInfo.possibleStartPositions[currentIndex];
			if(worker.getTargetPosition() != null && worker.getTargetPosition().getDistance(new Position(p.getX(), p.getY())) > 0)
			{
				worker.move(p);
			}
			if(worker.getDistance(new Position(p.getX(), p.getY())) < 32 * 5)
			{
				currentIndex ++;
				if(currentIndex >= root.enemyInfo.possibleStartPositions.length)
					return;
				worker.move(root.enemyInfo.possibleStartPositions[currentIndex]);
			}
		}
		else
		{
			
			double cx = root.enemyInfo.startPoint.getX();
			double cy = root.enemyInfo.startPoint.getY();
			double T = 23 * 30, R = 32 * 10;
			double ang = root.game.getFrameCount() / T * Math.PI * 2;
			double x = cx + Math.cos(ang) * R;
			double y = cy + Math.sin(ang) * R;
			worker.move(new Position((int)x, (int)y));			
			
		}
	}

	@Override
	public boolean checkPossible() {
		if(root.info.bases[0].gatherResourceTask != null)
			if(root.info.bases[0].gatherResourceTask.workerForMinearals.size() > 0)
				return true;
		return false;
	}

	@Override
	public void init() {
		List<Unit> lis = root.info.bases[0].gatherResourceTask.requestUnit(UnitType.Protoss_Probe, 1);
		worker = lis.get(0);
		root.info.setTask(worker, this);
		state = TaskState.ACTIVE;
		currentIndex = 1;
		
	}

	@Override
	public void checkEnd() {
		
		if(currentIndex >= root.enemyInfo.possibleStartPositions.length)
		{
			state = TaskState.FAILED;
			root.info.setTask(worker, null);
		}
		
		if(root.info.getUnitInfo(worker).destroy)
		{
			if(root.enemyInfo.startPoint == null)
				root.enemyInfo.startPoint = root.enemyInfo.possibleStartPositions[currentIndex];
			state = TaskState.FAILED;
			root.info.setTask(worker, null);
		}
		
		
		
		
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
		if(u == UnitType.Protoss_Probe)
			return 1;
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
		List<Unit> ret = new ArrayList<Unit>();
		return ret;
	}

}
