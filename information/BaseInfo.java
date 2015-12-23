package information;

import java.util.ArrayList;
import java.util.List;

import bwapi.*;
import bwta.BaseLocation;
import task.GatherResource;
import main.Bot;


public class BaseInfo {
	
	public int baseID;
	public Bot root;
	public Position position;
	public Unit myBase;
	public GatherResource gatherResourceTask;
	public BaseLocation baseLocation;
	public boolean canWalkTo;
	public double distToMe;
	public double distToEnemy;
	public BuildingArea buildingArea;
	
	public List<Unit> minerals; // existing even destroy
	public List<Unit> gas;
	public List<Unit> gasStation;
	int gasUsed;
	
	public int avaliableMinerals;
	public Unit[] avaliableMineralsUnit;
	boolean reservedBase;
	
	public PathInfo pathFromMyBase;
	
	public BaseInfo(Bot r) {
		root = r;
		minerals = new ArrayList<Unit>();
		gas = new ArrayList<Unit>();
		gasStation = new ArrayList<Unit>();
		gasUsed = 0;
		canWalkTo = true;
		reservedBase = false;
	}
	
	
	public void getBuildingArea(int xLen, int yLen)
	{
		int centerX = (position.getX() + 16) / 32;
		int centerY = (position.getY() + 16) / 32;
		int x0 = centerX - xLen / 2;
		int y0 = centerY - yLen / 2;
		
		buildingArea = new BuildingArea(root, this, x0, y0, xLen, yLen);
	}
	
	void onFirstFrame()
	{
		
		for(int iteration = 0; iteration < minerals.size(); iteration ++)
			for(int i = 0; i < minerals.size() - 1; i++)
			{
				double d1 = minerals.get(i).getDistance(position);
				double d2 = minerals.get(i+1).getDistance(position);
				if(d1 > d2)
				{
					Unit t = minerals.get(i);
					minerals.set(i, minerals.get(i+1));
					minerals.set(i+1, t);
				}
			}
		
		for(int iteration = 0; iteration < gas.size(); iteration ++)
			for(int i = 0; i < gas.size() - 1; i++)
			{
				double d1 = gas.get(i).getDistance(position);
				double d2 = gas.get(i+1).getDistance(position);
				if(d1 > d2)
				{
					Unit t = gas.get(i);
					gas.set(i, gas.get(i+1));
					gas.set(i+1, t);
				}
			}
		
		avaliableMinerals = minerals.size();
		

		
	}
	
	void onFrame()
	{
		avaliableMinerals = 0;
		for(int i = 0; i < minerals.size(); i++)
		{
			if(root.info.getUnitInfo(minerals.get(i)).destroy)
				continue;
			avaliableMinerals ++;
			/*
			root.game.drawTextMap(minerals.get(i).getX(), minerals.get(i).getY(), "#" + i);
			*/
		}
		avaliableMineralsUnit = new Unit[avaliableMinerals];
		int t = 0;
		for(int i = 0; i < minerals.size(); i++)
		{
			if(root.info.getUnitInfo(minerals.get(i)).destroy)
				continue;
			avaliableMineralsUnit[t] = minerals.get(i);
			t ++;
		}
		
		if(buildingArea != null)
		{
			
			/*
			for(int i = 0; i < buildingArea.nX; i++)
				for(int j = 0; j < buildingArea.nY; j++)
				{
					int x0 = (buildingArea.startX + i) * 32;
					int y0 = (buildingArea.startY + j) * 32;
					if(buildingArea.valid[i][j])
						root.game.drawBoxMap(x0 + 5, y0 + 5, x0 + 27, y0 + 27, new Color(0, 255, 0));
					else
						root.game.drawBoxMap(x0 + 5, y0 + 5, x0 + 27, y0 + 27, new Color(255, 0, 0));
				}
			*/
			
			for(int i = 0; i < buildingArea.slots4by3.size(); i++)
			{
				int x = buildingArea.slots4by3.get(i).getX();
				int y = buildingArea.slots4by3.get(i).getY();
				root.game.drawBoxMap(x * 32 + 2, y * 32 + 2, (x + 4) * 32 - 2, (y + 3) * 32 - 2, new Color(255, 255, 0));
				root.game.drawTextMap(x * 32 + 2 + 10, y * 32 + 2 + 10, "S#" + i);
			}
			
			for(int i = 0; i < buildingArea.slotsPylon.size(); i++)
			{
				int x = buildingArea.slotsPylon.get(i).getX();
				int y = buildingArea.slotsPylon.get(i).getY();
				root.game.drawBoxMap(x * 32 + 2, y * 32 + 2, (x + 2) * 32 - 2, (y + 2) * 32 - 2, new Color(0, 255, 0));
				root.game.drawTextMap(x * 32 + 2 + 10, y * 32 + 2 + 10, "P#" + i);
			}
			
			
			root.game.drawCircleMap(position.getX() , position.getY(), 10, new Color(255, 0, 0));
		}
		
	}

	public TilePosition whereToBuild(UnitType targetStruct, boolean reserveThatSlot) {
		
		if(buildingArea == null)
			return null;
		
		if(root.util.isGasBuilding(targetStruct))
		{
			if(gasUsed < gas.size())
			{	
				TilePosition ret = gas.get(gasUsed).getTilePosition();
				if(reserveThatSlot)
					gasUsed ++;
				return ret;
			}
		}
		if(root.util.isBase(targetStruct))
		{
			if(reservedBase || myBase != null)
				return null;
			if(reserveThatSlot)
				reservedBase = true;
			return baseLocation.getTilePosition();
		}
		else
		{
			if(targetStruct == UnitType.Protoss_Pylon)
			{
				if(buildingArea.usedSlotsPylon < buildingArea.slotsPylon.size())
				{
					TilePosition ret = buildingArea.slotsPylon.get(buildingArea.usedSlotsPylon);
					if(reserveThatSlot)
						buildingArea.usedSlotsPylon ++;
					return ret;
				}
			}
			else
			{
				if(buildingArea.usedSlots4by3 < buildingArea.slots4by3.size())
				{
					TilePosition ret = buildingArea.slots4by3.get(buildingArea.usedSlots4by3);
					if(reserveThatSlot)
						buildingArea.usedSlots4by3 ++;
					return ret;
				}
			}
		}
		
		return null;
	}
	
}
