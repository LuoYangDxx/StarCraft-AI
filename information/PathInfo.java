package information;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import bwapi.Color;
import bwapi.Position;
import bwapi.TilePosition;
import bwta.BWTA;
import bwta.Chokepoint;
import main.Bot;


public class PathInfo {

	Bot root;
	
	public List <TilePosition> positionOnPath;
	public double[] positionDistance;
	public double totalDistance;
	
	boolean[][] visited;
    
    public TilePosition destination;
    
    public TilePosition distToPosition(double d)
    {
    	if(d > totalDistance)
    		return positionOnPath.get(positionOnPath.size()-1);
    	int L = 0, R = positionOnPath.size()-1, M;
    	while(R - L > 1)
    	{
    		M = (L + R) / 2;
    		if(positionDistance[M] <= d)
    			L = M;
    		else
    			R = M;
    	}
    	return positionOnPath.get(L);
    }
    
    class node implements Comparable<node>
    {
    	public int x;
    	public int y;
    	public double alreadyLength;
    	public node previousNode;
    	
    	public double predict()
    	{
    		double dx = x - destination.getX();
    		double dy = y - destination.getY();
    		return Math.sqrt(dx * dx + dy * dy);
    	}
    	
		@Override
		public int compareTo(node other) {
			double d1 = alreadyLength + predict() * 0.41;
			double d2 = other.alreadyLength + other.predict() * 0.41;
			if(d1 < d2) return -1;
			if(d1 > d2) return +1;
			return 0;
		}
    	
    }
    
    
	
	PathInfo(Bot r, TilePosition from, TilePosition to)
	{
		root = r;
		
		int x1 = from.getX();
		int y1 = from.getY();
		int x2 = to.getX();
		int y2 = to.getY();
		
		if(visited == null)
    	{
    		visited = new boolean[root.game.mapWidth()][root.game.mapHeight()];
    	}
    	
    	List <TilePosition> ret = new ArrayList<TilePosition>();
    	
    	if(x1 < 0 || x1 >= root.game.mapWidth() || y1 < 0 || y1 >= root.game.mapHeight() ||  root.info.walkable[x1][y1] == false) return ;
    	if(x2 < 0 || x2 >= root.game.mapWidth() || y2 < 0 || y2 >= root.game.mapHeight() ||  root.info.walkable[x2][y2] == false) return ;
    	
    	
    	PriorityQueue<node> pq = new PriorityQueue<node>();
    	List <node> needClear = new ArrayList<node>(); 
    	
    	destination = new TilePosition(x2, y2);
    	node start = new node();
    	start.x = x1;
    	start.y = y1;
    	start.alreadyLength = 0;
    	pq.add(start);
    	visited[x1][y1] = true;
    	needClear.add(start);
    	
    	node lastNode = null;
    	
    	
    	
    	for(int i = 1; i <= root.game.mapWidth() * root.game.mapHeight(); i++)
    	{
    		node here = pq.poll();
    		if(here == null)
    			break;
    		//ret.add(new TilePosition(here.x, here.y));
    		boolean alreadyFind = false;
    		for(int dx = -1; dx <= 1; dx ++)
    			for(int dy = -1; dy <= 1; dy ++)
    			{
    				if(dx == 0 && dy == 0) continue;
    				int nx = here.x + dx;
    				int ny = here.y + dy;
    				if(nx < 0 || nx >= root.game.mapWidth()) continue;
    				if(ny < 0 || ny >= root.game.mapHeight()) continue;
    				if(visited[nx][ny]) continue;
    				if(root.info.walkable[nx][ny] == false) continue;
    				
    				node nextNode = new node();
    				
    				if(nx == x2 && ny == y2)
    				{
    					alreadyFind = true;
    					lastNode = nextNode;
    				}
    				
    				nextNode.previousNode = here;
    				nextNode.x = nx;
    				nextNode.y = ny;
    				visited[nx][ny] = true;
    				nextNode.alreadyLength = here.alreadyLength + Math.sqrt(0.0 + dx * dx + dy * dy);
    				needClear.add(nextNode);
    				pq.add(nextNode);
    			}
    		if(alreadyFind)
    			break;
    	}
    	
    	for(node t : needClear)
    		visited[t.x][t.y] = false;
    	
    	if(lastNode == null)
    		return;
    	
    	
    	List <TilePosition> revRet = new ArrayList<TilePosition>();
    	revRet.add(new TilePosition(lastNode.x, lastNode.y));
    	
    	while(lastNode.previousNode != null)
    	{
    		lastNode = lastNode.previousNode;
    		revRet.add(new TilePosition(lastNode.x, lastNode.y));
    	}
    	
    	for(int i = revRet.size()-1; i >= 0; i--)
    		ret.add(revRet.get(i));
    	
    	positionOnPath = ret;
    	
    	positionDistance = new double[positionOnPath.size()];
    	positionDistance[0] = 0;
    	for(int i = 1; i < positionDistance.length; i++)
    	{
    		double ax = positionOnPath.get(i-1).getX();
    		double ay = positionOnPath.get(i-1).getY();
    		double bx = positionOnPath.get(i).getX();
    		double by = positionOnPath.get(i).getY();
    		positionDistance[i] = positionDistance[i-1] + Math.sqrt((ax-bx)*(ax-bx) + (ay-by)*(ay-by));
    	}
    	
    	totalDistance = positionDistance[positionDistance.length-1];
    	
	}
	
	public void display() {
		
		for(int i = 0; i < positionOnPath.size()-1; i++)
		{
			Position a = new Position(positionOnPath.get(i).getX() * 32 + 16, positionOnPath.get(i).getY() * 32 + 16);
			Position b = new Position(positionOnPath.get(i+1).getX() * 32 + 16, positionOnPath.get(i+1).getY() * 32 + 16);
			root.game.drawLineMap(a.getX(), a.getY(), b.getX(), b.getY(), new Color(255, 0, 0));
		}
		
		Chokepoint cp = BWTA.getNearestChokepoint(root.info.bases[0].position);
		root.game.drawCircleMap(cp.getX(), cp.getY(), (int)cp.getWidth(), new Color(0, 255, 255));
		
		for(double d = 0; d < totalDistance; d += 20.0)
		{
			TilePosition tp = distToPosition(d);
			int x = tp.getX() * 32 + 16;
			int y = tp.getY() * 32 + 16;
			root.game.drawCircleMap(x, y, 10, new Color(0, 255, 255));
			root.game.drawTextMap(x, y, "" + d);
		}
		
		
	}
	

}
