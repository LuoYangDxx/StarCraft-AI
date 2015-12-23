package component;

import information.GlobalConstant;

import java.util.ArrayList;
import java.util.List;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import task.BuildStruct;
import main.Bot;

public class ExpansionManager extends Component {
	
	boolean[] alreadyBuildBase;
	boolean[] alreadyBuildGas;
	
	public ExpansionManager(Bot r) {
		super(r);
		
	}
	
	@Override
	public int getResourcePriority() {
		
		return 500;
	}
	
	@Override
	public String getName() {
		return "ExpansionManager";
	}
	
	@Override
	public void onFrame() {
		
		
		/*
		 * 1. Build Base / Gas Station
		 */
		
		if(alreadyBuildBase == null)
		{
			alreadyBuildBase = new boolean[root.info.bases.length];
			alreadyBuildBase[0] = true;
		}
		if(alreadyBuildGas == null)
			alreadyBuildGas = new boolean[root.info.bases.length];
		
		for(int i = 0; i < root.blackboard.getNumberOfBase(); i++)
		{
			if(root.info.bases[i].buildingArea == null)
				root.info.bases[i].getBuildingArea(GlobalConstant.Building_Area_X_OtherBase, GlobalConstant.Building_Area_Y_OtherBase);
			
			if(alreadyBuildBase[i] == false)
			{
				BuildStruct task = new BuildStruct(root, root.info.bases[i], UnitType.Protoss_Nexus);
				if(makeProposal(task))
					alreadyBuildBase[i] = true;
			}
			
			if(root.info.bases[i].myBase != null && root.blackboard.getHaveGasBuilding() && alreadyBuildGas[i] == false && root.info.bases[i].gas.size() > 0)
			{
				BuildStruct task = new BuildStruct(root, root.info.bases[i], UnitType.Protoss_Assimilator);
				if(makeProposal(task))
					alreadyBuildGas[i] = true;
			}
		}
		
		/*
		 * 2. Send free worker to minerals.
		 */
		
		List<Integer> aliveMineralsBases = new ArrayList<Integer>();
		
		for(int i = 0; i < root.blackboard.getNumberOfBase(); i++)
			if(root.info.bases[i].avaliableMinerals > 0 && root.info.bases[i].gatherResourceTask != null)
				aliveMineralsBases.add(i);
		
		if(aliveMineralsBases.size() > 0)
			for(Unit u : root.info.myUnits.get(UnitType.Protoss_Probe))
			{
				if(root.info.canStartNewTask(u))
				{
					int whichBase = aliveMineralsBases.get(0);
					for(Integer i : aliveMineralsBases)
					{
						double di = u.getDistance(new Position(root.info.bases[i].position.getX(), root.info.bases[i].position.getY()));
						double dWhich = u.getDistance(new Position(root.info.bases[whichBase].position.getX(), root.info.bases[whichBase].position.getY()));
						if(di < dWhich)
							whichBase = i;
					}
					root.info.bases[whichBase].gatherResourceTask.addWorkerForMinerals(u);
				}
			}
		
		/*
		 * 3. Balance minerals worker.
		 */
		
		List <Integer> need = new ArrayList<Integer>();
		List <Integer> provide = new ArrayList<Integer>();
		
		double totalMinerals = 0;
		double totalWorkers = 0;
		for(int i = 0; i < root.blackboard.getNumberOfBase(); i++)
			if(root.info.bases[i].gatherResourceTask != null)
			{
				totalMinerals += root.info.bases[i].avaliableMinerals;
				totalWorkers += root.info.bases[i].gatherResourceTask.getWorkerCountForMinerals();
			}
		double averageWorkers = 0;
		if(totalMinerals > 0)
			averageWorkers = totalWorkers / totalMinerals;
		for(int i = 0; i < root.blackboard.getNumberOfBase(); i++)
			if(root.info.bases[i].gatherResourceTask != null)
			{
				double wantWorkers = root.info.bases[i].avaliableMinerals * averageWorkers;
				double currentWorkers = root.info.bases[i].gatherResourceTask.getWorkerCountForMinerals();
				for(int j = 0; j < wantWorkers - currentWorkers; j++)
					need.add(i);
				for(int j = 0; j < currentWorkers - wantWorkers; j++)
					provide.add(i);
			}
		for(int i = 0; i < Math.min(need.size(), provide.size()); i++)
		{
			List<Unit> u = root.info.bases[provide.get(i)].gatherResourceTask.requestUnit(UnitType.Protoss_Probe, 1);
			if(u.size() > 0)
				root.info.bases[need.get(i)].gatherResourceTask.addWorkerForMinerals(u.get(0));
		}
		
	}

}
