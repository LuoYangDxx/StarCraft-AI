package information;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bwapi.UnitType;
import bwapi.UpgradeType;
import main.Bot;

public class Goal {
	
	public Bot root;
	
	public List <UnitType> techBuildings;
	public List <UnitType> armyUnit;
	
	HashMap<UnitType, Integer> want;
	HashMap<UnitType, List<UnitType>> prerequests;
	
	public List <UpgradeType> upgrades;
	HashMap<UpgradeType, Integer> wantUpgrades;
	HashMap<UpgradeType, Integer> myUpgrades;
	HashMap<UpgradeType, List<UnitType>> prerequestOfUpgrades;
	
	public void upgrading(UpgradeType ut)
	{
		myUpgrades.put(ut, myUpgrades.get(ut) + 1);
	}
	
	public boolean finishAllPrerequests(UnitType u)
	{
		for(UnitType t : prerequests.get(u))
			if(root.util.countUnit(t , false, false, true) == 0)
				return false;
		
		return true;
	}
	
	public boolean finishAllPrerequests(UpgradeType u)
	{
		for(UnitType t : prerequestOfUpgrades.get(u))
			if(root.util.countUnit(t , false, false, true) == 0)
				return false;
		return true;
	}
	
	void addPrerequest(UnitType a, UnitType b)
	{
		prerequests.get(a).add(b);
	}
	
	void addPrerequestOfUpgrade(UpgradeType a, UnitType b)
	{
		prerequestOfUpgrades.get(a).add(b);
	}
	
	public int getGoal(UnitType u)
	{
		return want.get(u);
	}
	
	public void setGoalAtleast(UnitType u, int number)
	{
		want.put(u, new Integer(Math.max(number, want.get(u).intValue())));
		for(UnitType t : prerequests.get(u))
		{
			if(u == UnitType.Protoss_Archon || u == UnitType.Protoss_Dark_Archon)
				setGoalAtleast(t, 2);
			else
				setGoalAtleast(t, 1);
		}
	}
	
	public void setGoal(UnitType u)
	{
		setGoalAtleast(u, 1);
	}
	
	public void setGoal(UnitType u, int number)
	{
		want.put(u, new Integer(number));
		for(UnitType t : prerequests.get(u))
		{
			if(u == UnitType.Protoss_Archon || u == UnitType.Protoss_Dark_Archon)
				setGoalAtleast(t, 2);
			else
				setGoalAtleast(t, 1);
		}
	}
	
	public void setGoal(UpgradeType u)
	{
		wantUpgrades.put(u, new Integer(1));
		for(UnitType t : prerequestOfUpgrades.get(u))
		{
			setGoalAtleast(t, 1);
		}
	}
	
	public UnitType getBuilding(UnitType t)
	{
		if(t == UnitType.Protoss_Archon || t == UnitType.Protoss_Dark_Archon)
			return null;
		
		return prerequests.get(t).get(0);
	}
	
	public UnitType getBuilding(UpgradeType t)
	{
		return prerequestOfUpgrades.get(t).get(0);
	}
	
	public Goal(Bot r) {
		root = r;
		
		techBuildings = new ArrayList<UnitType>();
		techBuildings.add(UnitType.Protoss_Gateway);
		techBuildings.add(UnitType.Protoss_Assimilator);
		techBuildings.add(UnitType.Protoss_Forge);
		techBuildings.add(UnitType.Protoss_Cybernetics_Core);
		techBuildings.add(UnitType.Protoss_Citadel_of_Adun);
		techBuildings.add(UnitType.Protoss_Templar_Archives);
		techBuildings.add(UnitType.Protoss_Robotics_Facility);
		techBuildings.add(UnitType.Protoss_Observatory);
		techBuildings.add(UnitType.Protoss_Robotics_Support_Bay);
		techBuildings.add(UnitType.Protoss_Stargate);
		techBuildings.add(UnitType.Protoss_Fleet_Beacon);
		techBuildings.add(UnitType.Protoss_Arbiter_Tribunal);
		
		armyUnit = new ArrayList<UnitType>();
		armyUnit.add(UnitType.Protoss_Zealot);
		armyUnit.add(UnitType.Protoss_Dragoon);
		armyUnit.add(UnitType.Protoss_High_Templar);
		armyUnit.add(UnitType.Protoss_Dark_Templar);
		armyUnit.add(UnitType.Protoss_Archon);
		armyUnit.add(UnitType.Protoss_Dark_Archon);
		armyUnit.add(UnitType.Protoss_Scout);
		armyUnit.add(UnitType.Protoss_Corsair);
		armyUnit.add(UnitType.Protoss_Carrier);
		armyUnit.add(UnitType.Protoss_Arbiter);
		armyUnit.add(UnitType.Protoss_Shuttle);
		armyUnit.add(UnitType.Protoss_Observer);
		armyUnit.add(UnitType.Protoss_Reaver);
		
		prerequests = new HashMap<UnitType, List<UnitType>>();
		want = new HashMap<UnitType, Integer>();
		
		for(UnitType u : techBuildings)
		{
			want.put(u, new Integer(0));
			prerequests.put(u, new ArrayList<UnitType>());
		}
		
		for(UnitType u : armyUnit)
		{
			want.put(u, new Integer(0));
			prerequests.put(u, new ArrayList<UnitType>());
		}
		
		
		addPrerequest(UnitType.Protoss_Cybernetics_Core, UnitType.Protoss_Gateway);
		addPrerequest(UnitType.Protoss_Cybernetics_Core, UnitType.Protoss_Assimilator);
		addPrerequest(UnitType.Protoss_Citadel_of_Adun, UnitType.Protoss_Cybernetics_Core);
		addPrerequest(UnitType.Protoss_Templar_Archives, UnitType.Protoss_Citadel_of_Adun);
		addPrerequest(UnitType.Protoss_Robotics_Facility, UnitType.Protoss_Cybernetics_Core);
		addPrerequest(UnitType.Protoss_Observatory, UnitType.Protoss_Robotics_Facility);
		addPrerequest(UnitType.Protoss_Robotics_Support_Bay, UnitType.Protoss_Robotics_Facility);
		addPrerequest(UnitType.Protoss_Stargate, UnitType.Protoss_Cybernetics_Core);
		addPrerequest(UnitType.Protoss_Fleet_Beacon, UnitType.Protoss_Stargate);
		addPrerequest(UnitType.Protoss_Arbiter_Tribunal, UnitType.Protoss_Stargate);
		addPrerequest(UnitType.Protoss_Arbiter_Tribunal, UnitType.Protoss_Templar_Archives);
		
		addPrerequest(UnitType.Protoss_Zealot, UnitType.Protoss_Gateway);
		addPrerequest(UnitType.Protoss_Dragoon, UnitType.Protoss_Gateway);
		addPrerequest(UnitType.Protoss_Dragoon, UnitType.Protoss_Cybernetics_Core);
		addPrerequest(UnitType.Protoss_High_Templar, UnitType.Protoss_Gateway);
		addPrerequest(UnitType.Protoss_High_Templar, UnitType.Protoss_Templar_Archives);
		addPrerequest(UnitType.Protoss_Dark_Templar, UnitType.Protoss_Gateway);
		addPrerequest(UnitType.Protoss_Dark_Templar, UnitType.Protoss_Templar_Archives);
		addPrerequest(UnitType.Protoss_Archon, UnitType.Protoss_High_Templar);
		addPrerequest(UnitType.Protoss_Dark_Archon, UnitType.Protoss_Dark_Templar);
		addPrerequest(UnitType.Protoss_Scout, UnitType.Protoss_Stargate);
		addPrerequest(UnitType.Protoss_Corsair, UnitType.Protoss_Stargate);
		addPrerequest(UnitType.Protoss_Carrier, UnitType.Protoss_Stargate);
		addPrerequest(UnitType.Protoss_Carrier, UnitType.Protoss_Fleet_Beacon);
		addPrerequest(UnitType.Protoss_Arbiter, UnitType.Protoss_Stargate);
		addPrerequest(UnitType.Protoss_Arbiter, UnitType.Protoss_Arbiter_Tribunal);
		addPrerequest(UnitType.Protoss_Shuttle, UnitType.Protoss_Robotics_Facility);
		addPrerequest(UnitType.Protoss_Observer, UnitType.Protoss_Robotics_Facility);
		addPrerequest(UnitType.Protoss_Observer, UnitType.Protoss_Observatory);
		addPrerequest(UnitType.Protoss_Reaver, UnitType.Protoss_Robotics_Facility);
		addPrerequest(UnitType.Protoss_Reaver, UnitType.Protoss_Robotics_Support_Bay);
		
		
		//setGoal(UnitType.Protoss_Arbiter);
		//setGoal(UnitType.Protoss_Observer);
		//setGoal(UnitType.Protoss_Carrier);
		
		upgrades = new ArrayList<UpgradeType>();
		
		// TODO: Add all Protoss upgrade!
		upgrades.add(UpgradeType.Singularity_Charge);
		upgrades.add(UpgradeType.Leg_Enhancements);
		upgrades.add(UpgradeType.Gravitic_Drive);
		
		wantUpgrades = new HashMap<UpgradeType, Integer>();
		myUpgrades = new HashMap<UpgradeType, Integer>();
		prerequestOfUpgrades = new HashMap<UpgradeType, List<UnitType>>();
		for(UpgradeType ut : upgrades)
		{
			wantUpgrades.put(ut, 0);
			myUpgrades.put(ut, 0);
			prerequestOfUpgrades.put(ut, new ArrayList<UnitType>());
		}
		
		addPrerequestOfUpgrade(UpgradeType.Singularity_Charge, UnitType.Protoss_Cybernetics_Core);
		addPrerequestOfUpgrade(UpgradeType.Leg_Enhancements, UnitType.Protoss_Citadel_of_Adun);
		addPrerequestOfUpgrade(UpgradeType.Gravitic_Drive, UnitType.Protoss_Robotics_Support_Bay);
	}

	public int getGoal(UpgradeType ut)
	{
		return wantUpgrades.get(ut);
	}
	
	public int getNow(UpgradeType ut)
	{
		return myUpgrades.get(ut);
	}
	
}
