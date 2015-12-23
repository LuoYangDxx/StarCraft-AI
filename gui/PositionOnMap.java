package gui;
import bwapi.Color;
import main.Bot;

public class PositionOnMap { 
	
	Bot root;
	
	public int x, y;
	
	int depth;
	String name;
	boolean selected;
	public boolean enabled;
	
	public PositionOnMap(Bot r) {
		root = r;
		selected = false;
		enabled = false;
	}
	
	void onFrame()
	{
		if(!enabled) return;
		root.game.drawCircleMap(x, y, 15, new Color(255, 0, 0), true);
		if(selected)
		{
			root.game.drawCircleMap(x, y, 10, new Color(255, 255, 255), true);
			root.game.drawCircleMap(x, y, 5, new Color(255, 0, 0), true);
		}
	}

}
