package gui;
import java.util.ArrayList;
import java.util.List;

import bwapi.*;
import main.*;

public class GUIManager {
	
	main.Bot root;
	
	boolean needInit;
	public PositionOnMap attackPosition;
	
	List <PositionOnMap> listOfPositionOnMap;
	PositionOnMap currentSelect;
	
	List <String> debugInfo;
	
	public GUIManager(Bot r) {
		root = r;
		listOfPositionOnMap = new ArrayList<PositionOnMap>();
		debugInfo = new ArrayList<String>();
		
		attackPosition = new PositionOnMap(r);
		listOfPositionOnMap.add(attackPosition);
		
		needInit = true;
	}
	
	public void addDebugInfo(String s)
	{
		debugInfo.add(s);
	}
	
	public void onFrameStart()
	{
		debugInfo.clear();
	}
	
	
	public void onFrameEnd()
	{
    	root.game.setTextSize(6);
        int startX = 12;
        int startY = 12;
        for (String s : debugInfo)
        {
        	root.game.drawTextScreen(startX, startY, s);
        	startY += 15;
        	
        }    
        
		if(needInit)
		{
			needInit = false;
			attackPosition.depth = 0;
			attackPosition.name = "Attack Point";
			attackPosition.enabled = true;
		}
		
		double mouseOnMapX = root.game.getScreenPosition().getX() + root.game.getMousePosition().getX();
		double mouseOnMapY = root.game.getScreenPosition().getY() + root.game.getMousePosition().getY();
		
		
		if(root.game.getMouseState(MouseButton.M_LEFT))
		{
			PositionOnMap bestPos = null;
			int highestDepth = -1000000;
			for(PositionOnMap p : listOfPositionOnMap)
			{
				if(p.enabled == false) continue;
				int x = p.x;
				int y = p.y;
				double d = (mouseOnMapX - x) * (mouseOnMapX - x) + (mouseOnMapY - y) * (mouseOnMapY - y);
				d = Math.sqrt(d);
				if(d <= 15)
				{
					if(p.depth > highestDepth)
					{
						highestDepth = p.depth;
						bestPos = p;
					}
				}
			}
			
			if(bestPos == null)
			{
				if(currentSelect != null)
					currentSelect.selected = false;
				currentSelect = null;
			}
			else
			{
				if(currentSelect != null)
					currentSelect.selected = false;
				currentSelect = bestPos;
				bestPos.selected = true;
			}
			
		}
		
		if(root.game.getMouseState(MouseButton.M_RIGHT))
		{
			if(currentSelect != null)
			{
				currentSelect.x = (int)mouseOnMapX;
				currentSelect.y = (int)mouseOnMapY;
				
				root.blackboard.setAttackPosition(new Position(currentSelect.x, currentSelect.y));
				
			}
		}
		
		attackPosition.onFrame();
		
		
		
	}
}
