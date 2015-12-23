package component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import task.Task;
import task.TrainUnit;
import bwapi.Unit;
import bwapi.UnitType;
import main.Bot;

public class ArmyProductionManager extends Component {

	public ArmyProductionManager(Bot r) {
		super(r);
	}
	
	@Override
	public int getResourcePriority() {
		return 100;
	}
	
	@Override
	public String getName() {
		return "ArmyProductionManager";
	}
	
	@Override
	public void onFrame() {
		
		
		//root.goal.setGoal(UnitType.Protoss_Gateway, 2);
		//root.goal.setGoal(UnitType.Protoss_Robotics_Facility, 2);
		//root.goal.setGoal(UnitType.Protoss_Stargate, 2);
		
		List <Unit> freeGateway = new ArrayList<Unit>();
		List <Unit> freeRoboticsFacility = new ArrayList<Unit>();
		List <Unit> freeStargate = new ArrayList<Unit>();
		HashMap<UnitType, List<Unit>> freeBuilding = new HashMap<UnitType, List<Unit>>();
		freeBuilding.put(UnitType.Protoss_Gateway, freeGateway);
		freeBuilding.put(UnitType.Protoss_Robotics_Facility, freeRoboticsFacility);
		freeBuilding.put(UnitType.Protoss_Stargate, freeStargate);
		
		for(Unit u : root.info.getMyUnitsByType(UnitType.Protoss_Gateway))
			if(root.info.canStartNewTask(u))
				freeGateway.add(u);
		for(Unit u : root.info.getMyUnitsByType(UnitType.Protoss_Robotics_Facility))
			if(root.info.canStartNewTask(u))
				freeRoboticsFacility.add(u);
		for(Unit u : root.info.getMyUnitsByType(UnitType.Protoss_Stargate))
			if(root.info.canStartNewTask(u))
				freeStargate.add(u);
		
		
		// deal with 'require'
		//for(UnitType ut : root.goal.armyUnit)
		//	root.goal.setGoal(ut);
		
		for(UnitType ut : root.goal.armyUnit)
		{
			if(root.goal.getBuilding(ut) == null)
				continue;
			if(root.goal.finishAllPrerequests(ut) == false)
				continue;
			int need = root.goal.getGoal(ut) - root.util.countUnit(ut, true, true, true);
			if(need <= 0)
				continue;
			List<Unit> buildings = freeBuilding.get(root.goal.getBuilding(ut));
			
			//System.out.println(ut + " needs " + need + " .. haveFreeBuildiing = " + freeGateway.size());
			
			for(int i = 0; i < need; i++)
			{
				if(buildings.size() == 0)
					break;
				Unit b = buildings.get(buildings.size()-1);
				TrainUnit task = new TrainUnit(root, b, ut);
				//System.out.println("buingds = " + buildings.size());
				if(makeProposal(task))
					buildings.remove(buildings.size()-1);
			}
		}
		
		
	}

}
