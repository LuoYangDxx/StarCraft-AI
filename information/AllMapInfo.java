package information;

import java.util.ArrayList;
import java.util.List;

import bwapi.Color;
import bwapi.Position;
import main.Bot;

public class AllMapInfo {
	
	Bot root;
	
	public int n, m;
	public int[][] lastVisitedTime;
	public boolean[][] isDanger;
	
	public Position getPosition(int i, int j)
	{
		int x = 410 * i + 410 / 2;
		int y = 410 * j + 410 / 2;
		x = Math.min(x, root.game.mapWidth() * 32 - 1);
		y = Math.min(y, root.game.mapHeight() * 32 - 1);
		Position ret = new Position(x, y);
		return ret;
	}
	
	public AllMapInfo(Bot r) {
		root = r;
	}
	
	public void onFirstFrame()
	{
		n = (root.game.mapWidth() * 32 - 1) / 410 + 1;
		m = (root.game.mapWidth() * 32 - 1) / 410 + 1;
		lastVisitedTime = new int[n][m];
		isDanger = new boolean[n][m];
	}
	
	public void onFrame()
	{
		for(int i = 0; i < n; i++)
			for(int j = 0; j < m; j++)
			{
				int x1 = i * 410 + 10;
				int y1 = j * 410 + 10;
				int x2 = Math.min((i+1) * 410 - 1, root.game.mapWidth() * 32 - 1) - 10;
				int y2 = Math.min((j+1) * 410 - 1, root.game.mapHeight() * 32 - 1) - 10;
				//root.game.drawBoxMap(x1, y1, x2, y2, new Color(0, 255, 0));
			}
	}

	public void visit(int targetX, int targetY) {
		lastVisitedTime[targetX][targetY] = root.game.getFrameCount();
		
	}

	public Position getNext(int prevX, int prevY) {
		Position ret = new Position(prevX, prevY);
		boolean[][] v = new boolean[n][m];
		Position[][] prevPosition = new Position[n][m];
		
		List <Position> queue = new ArrayList<Position>();
		int p = 0;
		queue.add(new Position(prevX, prevY));
		v[prevX][prevY] = true;
		
		while(p < queue.size())
		{
			Position now = queue.get(p);
			for(int dx = -1; dx <= 1; dx ++)
				for(int dy = -1; dy <= 1; dy ++)
				{
					if(dx == 0 && dy == 0) continue;
					int nx = now.getX() + dx;
					int ny = now.getY() + dy;
					if(nx < 0 || nx >= n) continue;
					if(ny < 0 || ny >= m) continue;
					if(v[nx][ny]) continue;
					if(isDanger[nx][ny]) continue;
					
					v[nx][ny] = true;
					queue.add(new Position(nx, ny));
					prevPosition[nx][ny] = now;
					if(lastVisitedTime[nx][ny] < lastVisitedTime[ret.getX()][ret.getY()])
					{
						ret = new Position(nx, ny);
					}
				}
			++ p;
		}
		
		while(prevPosition[ret.getX()][ret.getY()] != null)
		{
			Position t = prevPosition[ret.getX()][ret.getY()];
			if(prevPosition[t.getX()][t.getY()] == null)
				break;
			ret = t;
			
		}
		
		return ret;
	}
	

}
