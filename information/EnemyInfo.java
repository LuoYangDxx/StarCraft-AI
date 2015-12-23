package information;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import computation.StringUtil;
import bwapi.Color;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.Utils;
import bwta.BWTA;
import main.Bot;

public class EnemyInfo {
	
	Bot root;
	
	public class EnemyUnitInfo
	{
		public Unit unit;
		public UnitType unitType;
		public int lastFrame;
		public Position lastPosition;
		public boolean destroy;
	}
	
	public List<Unit> enemies;
	EnemyUnitInfo[] unitInfo;
	
	HashMap<UnitType, List<Unit>> enemiesByType;
	HashMap<UnitType, Integer> enemyDeadUnit;
	
	HashSet<UnitType> allUnitType;
	
	public Position startPoint;
	public Position[] possibleStartPositions;
	public boolean visitedStartPoint;
	
	
	public int getKilledUnitByType(UnitType ut)
	{
		if(enemyDeadUnit.containsKey(ut))
			return enemyDeadUnit.get(ut);
		return 0;
	}
	
	public List<Unit> getEnemyUnitByType(UnitType ut)
	{
		if(enemiesByType.containsKey(ut))
			return enemiesByType.get(ut);
		return new ArrayList<Unit>();
	}
	
	public void onUnitDestroy(Unit u) {
		
		
		if(u.getPlayer().getID() == root.enemy.getID())
		{
			getUnitInfo(u).destroy = true;
			if(enemyDeadUnit.get(u.getType()) == null)
				enemyDeadUnit.put(u.getType(), 0);
			enemyDeadUnit.put(u.getType(), enemyDeadUnit.get(u.getType()) + 1);
		}
	}

	public void onUnitCreate(Unit u) {
		
		
		
		if(u.getPlayer().getID() == root.enemy.getID())
		{
			
			if(unitInfo[u.getID()] == null)
			{
				enemies.add(u);
			}
			
			EnemyUnitInfo ui = getUnitInfo(u);
			ui.unit = u;
			ui.lastFrame = root.game.getFrameCount();
			ui.lastPosition = u.getPosition();
			ui.destroy = false;
			ui.unitType = u.getType();
			if(allUnitType.contains(u.getType()) == false)
				allUnitType.add(u.getType());
		}
	}
	
	public EnemyUnitInfo getUnitInfo(Unit u)
	{
		int id = u.getID();
		if(unitInfo[id] == null)
			unitInfo[id] = new EnemyUnitInfo();
		return unitInfo[id];
	}
	
	public EnemyInfo(Bot r) {
		root = r;
		enemies = new ArrayList<Unit>();
		unitInfo = new EnemyUnitInfo[information.GlobalConstant.MAX_UNIT];
		enemyDeadUnit = new HashMap<UnitType, Integer>();
		allUnitType = new HashSet<UnitType>();
		visitedStartPoint = false;
	}
	
	int n;
	double[][] dist;
	double[][] dp;
	int[][] option;
	
	
	double dfs(int current, int mask)
	{
		//System.out.println("dp " + current + " " + mask);
		if(mask == (1<<n)-1)
			return 0;
		double ret = 1000000000;
		if(option[current][mask] > 0)
			return dp[current][mask];
		int alreadyVisitedNodes = 0;
		for(int i = 0; i < n; i++)
			if((mask & (1<<i)) > 0)
				alreadyVisitedNodes ++;
		for(int i = 0; i < n; i++)
			if((mask & (1<<i)) == 0)
			{
				double value = dist[current][i];
				value += (1.0 - 1.0 / (n - alreadyVisitedNodes)) * dfs(i, mask | (1<<i));
				if(value < ret)
				{
					ret = value;
					option[current][mask] = i;
				}
			}
		//System.out.println(current + " " + mask + " : " + option[current][mask]);
		dp[current][mask] = ret;
		return ret;
	}
	
	public List<EnemyUnitInfo> getEnemyCombatUnits()
	{
		List <EnemyUnitInfo> ret = new ArrayList<EnemyUnitInfo>();
		for(Unit u : enemies)
		{
			if(root.util.isCombatUnit(getUnitInfo(u).unitType))
				ret.add(getUnitInfo(u));
		}
		return ret;
	}
	
	public boolean getEnemyHaveBase()
	{
		for(Unit u : enemies)
			if(root.util.isBase(getUnitInfo(u).unitType))
				return true;
		return false;
	}
	
	public List<EnemyUnitInfo> getEnemyBase()
	{
		List <EnemyUnitInfo> ret = new ArrayList<EnemyInfo.EnemyUnitInfo>();
		for(Unit u : enemies)
			if(root.util.isBase(getUnitInfo(u).unitType))
				ret.add(getUnitInfo(u));
		return ret;
	}
	
	public List<EnemyUnitInfo> getEnemyBuildings()
	{
		List <EnemyUnitInfo> ret = new ArrayList<EnemyInfo.EnemyUnitInfo>();
		for(Unit u : enemies)
			if(getUnitInfo(u).unitType.isBuilding())
				ret.add(getUnitInfo(u));
		return ret;
	}
	
	public void onFirstFrame()
	{
		/*
		 * Calculate the best order of finding opponent
		 * Definition: minimize E[time]
		 * Algorithm: Dynamic programming
		 */
		n = BWTA.getStartLocations().size();
		possibleStartPositions = new Position[n];
		for(int i = 0; i < n; i++)
			possibleStartPositions[i] = BWTA.getStartLocations().get(i).getPosition();
		int whichIsMine = 0;
		for(int i = 0; i < n; i++)
		{
			double dI = BWTA.getGroundDistance(root.util.getNearestTilePosition(root.util.getMyFirstBasePosition()), root.util.getNearestTilePosition(possibleStartPositions[i]));
			double dWhich = BWTA.getGroundDistance(root.util.getNearestTilePosition(root.util.getMyFirstBasePosition()), root.util.getNearestTilePosition(possibleStartPositions[whichIsMine]));
			if(dI < dWhich)
				whichIsMine = i;
		}
		Position t = possibleStartPositions[whichIsMine];
		possibleStartPositions[whichIsMine] = possibleStartPositions[0];
		possibleStartPositions[0] = t;
		
		dist = new double[n][n];
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
				dist[i][j] = BWTA.getGroundDistance(root.util.getNearestTilePosition(possibleStartPositions[i]), root.util.getNearestTilePosition(possibleStartPositions[j]));
		
		dp = new double[n][1<<n];
		option = new int[n][1<<n];
		
		dfs(0, 1);
		
		Position[] t1 = new Position[n];
		int now = 0;
		int mask = 1;
		t1[0] = possibleStartPositions[0];
		
		//System.out.println("n = " + n);
		for(int i = 1; i < n; i++)
		{
			now = option[now][mask];
			//System.out.println(now);
			mask |= (1<<now);
			t1[i] = possibleStartPositions[now];
		}
		possibleStartPositions = t1;
		
	}
	
	public void onFrame()
	{
		List<Unit> nextEnemies = new ArrayList<Unit>();
		for(Unit u : enemies)
		{
			if(getUnitInfo(u).destroy)
				continue;
			nextEnemies.add(u);
		}
		enemies = nextEnemies;
		
		enemiesByType = new HashMap<UnitType, List<Unit>>();
		
		for(Unit u : enemies)
		{
			UnitType ut = getUnitInfo(u).unitType;
			if(enemiesByType.get(ut) == null)
				enemiesByType.put(ut, new ArrayList<Unit>());
			enemiesByType.get(ut).add(u);
		}
		
		for(UnitType ut : allUnitType)
		{
			String s = "";
			s += StringUtil.putLeft(ut + "", 25);
			s += StringUtil.putRight(getEnemyUnitByType(ut).size() + "", 4);
			s += StringUtil.putRight(getKilledUnitByType(ut) + "", 4);
			if(root.util.isCombatUnit(ut))
				s = Utils.formatText(s, Utils.Red);
			else
				s = Utils.formatText(s, Utils.White);
			root.guiManager.addDebugInfo(s);
		}
		
		if(visitedStartPoint == false && startPoint != null)
		{
			for(Unit u : root.info.getMyUnits())
			{
				if(u.getDistance(startPoint) < 32 * 7)
					visitedStartPoint = true;
			}
		}
		
		for(int i = 0; i < n-1; i++)
		{
			//root.game.drawLineMap(possibleStartPositions[i].getX(), possibleStartPositions[i].getY(), possibleStartPositions[i+1].getX(), possibleStartPositions[i+1].getY(), new Color(0, 255, 0));
		}
	}
}
