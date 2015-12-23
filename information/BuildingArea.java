package information;

import java.util.ArrayList;
import java.util.List;

import bwapi.Position;
import bwapi.TilePosition;
import bwta.BWTA;
import main.Bot;

public class BuildingArea {
	
	Bot root;
	
	BaseInfo baseInfo;
	
	int startX, startY;
	int nX, nY;
	boolean[][] valid;
	boolean[][] visited;
	int[][] cntValid;
	
	int[] dx = {1, -1, 0, 0};
	int[] dy = {0, 0, 1, -1};
	
	List<TilePosition> slots4by3;
	List<TilePosition> slotsPylon;
	List<Integer> needPylon;
	
	int usedSlots4by3;
	int usedSlotsPylon;
	
	void dfs(int x, int y)
	{
		if(visited[x][y]) return;
		visited[x][y] = true;
		if(x == 0 || y == 0 || x == nX - 1 || y == nY - 1) return;
		for(int d = 0; d < 4; d++)
		{
			int nx = x + dx[d];
			int ny = y + dy[d];
			double dist = BWTA.getGroundDistance(new TilePosition(startX + x, startY + y), new TilePosition(startX + nx, startY + ny));
			if(dist != 32)
				return;
		}
		valid[x][y] = true;
		for(int d = 0; d < 4; d++)
		{
			int nx = x + dx[d];
			int ny = y + dy[d];
			dfs(nx, ny);
		}
	}
	
	int baseMinX;
	int baseMaxX;
	int baseMinY;
	int baseMaxY;
	
	void updateBaseMinMax(int x, int y)
	{
		baseMinX = Math.min(baseMinX, x);
		baseMaxX = Math.max(baseMaxX, x);
		baseMinY = Math.min(baseMinY, y);
		baseMaxY = Math.max(baseMaxY, y);
	}
	
	boolean valid5by1(int x, int y)
	{
		for(int i = 0; i < 5; i++)
			if(valid[x+i][y] == false)
				return false;
		return true;
	}
	
	void compute()
	{
		int w = root.game.mapWidth();
		int h = root.game.mapHeight();
		if(startX < 0) startX = 0;
		if(startY < 0) startY = 0;
		if(startX + nX > w) nX = w - startX;
		if(startY + nY > h) nY = h - startY;
		
		valid = new boolean[nX][nY];
		visited = new boolean[nX][nY];
		
		for(int i = 0; i < nX; i++)
			for(int j = 0; j < nY; j++)
			{
				valid[i][j] = false;
				visited[i][j] = false;
			}
		
		int cx = root.util.getNearestTilePosition(baseInfo.position).getX() - startX;
		int cy = root.util.getNearestTilePosition(baseInfo.position).getY() - startY;
		
		dfs(cx, cy);
		
		cntValid = new int[nX][nY];
		
		for(int i = 0; i < nX; i++)
			for(int j = 0; j < nY; j++)
			{
				if(valid[i][j])
					cntValid[i][j] = 1;
				if(i > 0)
					cntValid[i][j] += cntValid[i-1][j];
				if(j > 0)
					cntValid[i][j] += cntValid[i][j-1];
				if(i > 0 && j > 0)
					cntValid[i][j] -= cntValid[i-1][j-1];
			}
		
		/*
		for(int i = 0; i < nX; i++)
			for(int j = 0; j < nY; j++)
				if(valid[i][j])
				{
					int xMin = Math.min(i, cx);
					int xMax = Math.max(i, cx);
					int yMin = Math.min(j, cy);
					int yMax = Math.max(j, cy);
					int need = (xMax - xMin + 1) * (yMax - yMin + 1);
					int have = cntValid[xMax][yMax] - cntValid[xMax][yMin-1] - cntValid[xMin-1][yMax] + cntValid[xMin-1][yMin-1];
					if(have != need)
						valid[i][j] = false;
				}
		*/
		
	
		baseMinX = 1000;
		baseMaxX = 0;
		baseMinY = 1000;
		baseMaxY = 0;
		
		updateBaseMinMax(baseInfo.baseLocation.getTilePosition().getX(), baseInfo.baseLocation.getTilePosition().getY());
		updateBaseMinMax(baseInfo.baseLocation.getTilePosition().getX() + 4 - 1, baseInfo.baseLocation.getTilePosition().getY() + 3 - 1);
		for(int i = 0; i < baseInfo.minerals.size(); i++)
		{
			updateBaseMinMax(baseInfo.minerals.get(i).getTilePosition().getX(), baseInfo.minerals.get(i).getTilePosition().getY());
			updateBaseMinMax(baseInfo.minerals.get(i).getTilePosition().getX() + 2 - 1, baseInfo.minerals.get(i).getTilePosition().getY());
		}
		for(int i = 0; i < baseInfo.gas.size(); i++)
		{
			updateBaseMinMax(baseInfo.gas.get(i).getTilePosition().getX() , baseInfo.gas.get(i).getTilePosition().getY());
			updateBaseMinMax(baseInfo.gas.get(i).getTilePosition().getX() + 4 - 1 , baseInfo.gas.get(i).getTilePosition().getY() + 2 - 1);
		}
		
		for(int i = 0; i < nX; i++)
			for(int j = 0; j < nY; j++)
				if(valid[i][j])
				{
					int x = startX + i;
					int y = startY + j;
					if(baseMinX <= x && x <= baseMaxX)
						if(baseMinY <= y && y <= baseMaxY)
							valid[i][j] = false;
				}
		
		valid[cx][cy] = true;
		
		int validMinX = 1000;
		int validMaxX = 0;
		int validMinY = 1000;
		int validMaxY = 0;
		for(int i = 0; i < nX; i++)
			for(int j = 0; j < nY; j++)
				if(valid[i][j])
				{
					int x = startX + i;
					int y = startY + j;
					validMinX = Math.min(validMinX, x);
					validMaxX = Math.max(validMaxX, x);
					validMinY = Math.min(validMinY, y);
					validMaxY = Math.max(validMaxY, y);
				}
		
		nX = validMaxX - validMinX;
		nY = validMaxY - validMinY;
		boolean[][] newValid = new boolean[nX][nY];
		for(int i = 0; i < nX; i++)
			for(int j = 0; j < nY; j++)
				newValid[i][j] = valid[i + validMinX - startX][j + validMinY - startY];
		
		valid = newValid;
		startX = validMinX;
		startY = validMinY;
		
		cntValid = new int[nX][nY];
		
		for(int i = 0; i < nX; i++)
			for(int j = 0; j < nY; j++)
			{
				if(valid[i][j])
					cntValid[i][j] = 1;
				if(i > 0)
					cntValid[i][j] += cntValid[i-1][j];
				if(j > 0)
					cntValid[i][j] += cntValid[i][j-1];
				if(i > 0 && j > 0)
					cntValid[i][j] -= cntValid[i-1][j-1];
			}
		
		int whichOffset = 0;
		int bestTotal = 0;
		
		for(int offset = 0; offset < 5; offset ++)
		{
			int total4by3Slots = 0;
			
			for(int i = offset; i + 5 <= nX; i+=5)
			{
				for(int j = 0; j < nY; j++)
					if(valid5by1(i, j))
					{
						int till = j;
						while(till + 1 < nY && valid5by1(i, till+1))
							till ++;
						
						int size = till - j + 1;
						
						if(size >= 5)
						{
							int type8 = size / 8;
							int total = type8 * 2 + (size - type8 * 8) / 5;
							total4by3Slots += total;
						}
						
						j = till;
					}
			}
			if(total4by3Slots > bestTotal)
			{
				bestTotal = total4by3Slots;
				whichOffset = offset;
			}
		}
		
		List<TilePosition> size8, size5, size2;
		size8 = new ArrayList<TilePosition>();
		size5 = new ArrayList<TilePosition>();
		size2 = new ArrayList<TilePosition>();
		
		for(int i = whichOffset; i + 5 <= nX; i+=5)
		{
			for(int j = 0; j < nY; j++)
				if(valid5by1(i, j))
				{
					int till = j;
					while(till + 1 < nY && valid5by1(i, till+1))
						till ++;
					
					int size = till - j + 1;
					
					if(size >= 5)
					{
						int type8 = size / 8;
						int p = j;
						for(int iteration = 1; iteration <= type8; iteration ++)
						{
							size8.add(new TilePosition(i, p));
							p += 8;
							size -= 8;
						}
						if(size >= 5)
						{
							size5.add(new TilePosition(i, p));
							p += 5;
							size -= 5;
						}
						while(size >= 2)
						{
							size2.add(new TilePosition(i, p));
							p += 2;
							size -= 2;
						}
					}
					
					j = till;
				}
		}
		
		for(int iteration = 0; iteration < size8.size(); iteration ++)
			for(int i = 0; i < size8.size() - 1; i++)
				if(betterThan(size8.get(i), size8.get(i+1), 8) == false)
				{
					TilePosition t = size8.get(i);
					size8.set(i, size8.get(i+1));
					size8.set(i+1, t);
				}
		
		/*
		for(int i = 0; i < size8.size(); i++)
		{
			TilePosition t = root.util.getNearestTilePosition(baseInfo.baseLocation.getPosition());
			System.out.println(size8.get(i).getDistance(t));
		}
		*/
		
		for(int iteration = 0; iteration < size5.size(); iteration ++)
			for(int i = 0; i < size5.size() - 1; i++)
				if(betterThan(size5.get(i), size5.get(i+1), 5) == false)
				{
					TilePosition t = size5.get(i);
					size5.set(i, size5.get(i+1));
					size5.set(i+1, t);
				}
		
		for(int iteration = 0; iteration < size2.size(); iteration ++)
			for(int i = 0; i < size2.size() - 1; i++)
				if(betterThan(size2.get(i), size2.get(i+1), 2) == false)
				{
					TilePosition t = size2.get(i);
					size2.set(i, size2.get(i+1));
					size2.set(i+1, t);
				}
		
		List<TilePosition> tempPylon = new ArrayList<TilePosition>();
		
		for(int i = 0; i < size8.size(); i++)
		{
			int x = size8.get(i).getX();
			int y = size8.get(i).getY();
			slotsPylon.add(new TilePosition(x + startX, y + startY + 3));
			slots4by3.add(new TilePosition(x + startX, y + startY));
			slots4by3.add(new TilePosition(x + startX, y + startY + 5));
			needPylon.add(new Integer(slotsPylon.size()));
			tempPylon.add(new TilePosition(x + startX + 2, y + startY + 3));
		}
		
		for(int i = 0; i < size5.size(); i++)
		{
			int x = size5.get(i).getX();
			int y = size5.get(i).getY();
			slotsPylon.add(new TilePosition(x + startX, y + startY + 3));
			slots4by3.add(new TilePosition(x + startX, y + startY));
			needPylon.add(new Integer(slotsPylon.size()));
			tempPylon.add(new TilePosition(x + startX + 2, y + startY + 3));
		}
		
		for(int i = 0; i < size2.size(); i++)
		{
			int x = size2.get(i).getX();
			int y = size2.get(i).getY();
			tempPylon.add(new TilePosition(x + startX, y + startY));
			tempPylon.add(new TilePosition(x + startX + 2, y + startY));
		}
		
		for(TilePosition t : tempPylon)
			slotsPylon.add(t);
		
		
		//System.out.println("Total 4x3 slots = " + slots4by3.size());
		//System.out.println("Total Pylon slots = " + slotsPylon.size());
		
		
	}
	
	private boolean betterThan(TilePosition a, TilePosition b, int sz) {
		Position p1 = new Position((a.getX() + startX) * 32 + 2 * 32, (a.getY() + startY) * 32 + sz * 16);
		Position p2 = new Position((b.getX() + startX) * 32 + 2 * 32, (b.getY() + startY) * 32 + sz * 16);
		return p1.getDistance(baseInfo.baseLocation.getPosition()) < p2.getDistance(baseInfo.baseLocation.getPosition());
	}
	
	public BuildingArea(Bot r, BaseInfo b, int sx, int sy, int nx, int ny) {
		root = r;
		startX = sx;
		startY = sy;
		nX = nx;
		nY = ny;
		baseInfo = b;
		slots4by3 = new ArrayList<TilePosition>();
		slotsPylon = new ArrayList<TilePosition>();
		needPylon = new ArrayList<Integer>();
		usedSlots4by3 = 0;
		usedSlotsPylon = 0;
		
		compute();
	}
	
}
