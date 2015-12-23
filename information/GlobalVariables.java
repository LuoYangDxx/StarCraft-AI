package information;
import bwapi.Position;
import main.*;

public class GlobalVariables {
	
	Bot root;
	
	Position attackPosition;
	
	boolean firstTimeScout;
	boolean isAttacking;
	boolean haveGasBuilding;
	
	int numberOfBase;
	boolean isEndingGame;
	
	double front;
	double front_want;
	
	public double getFront()
	{
		return front;
	}
	
	public void setFront(double t)
	{
		front_want = t;
	}
	
	public boolean getIsEndingGame()
	{
		return isEndingGame;
	}
	
	public void setIsEndingGame(boolean b)
	{
		isEndingGame = b;
	}
	
	public boolean getHaveGasBuilding()
	{
		return haveGasBuilding;
	}
	
	public void setHaveGasBuilding(boolean b)
	{
		haveGasBuilding = b;
	}
	
	public int getNumberOfBase()
	{
		int badBases = 0;
		for(int i = 0; i < root.info.bases.length; i++)
			if(root.info.bases[i].gatherResourceTask != null)
				if(root.info.bases[i].avaliableMinerals <= 4)
					badBases ++;
		
		return Math.min(numberOfBase + badBases, root.info.bases.length);
	}
	
	public void setNumberOfBase(int x)
	{
		numberOfBase = x;
	}
	
	public void setNumberOfBaseAtLesst(int x)
	{
		numberOfBase = Math.max(x, numberOfBase);
	}
	
	
	public void setIsAttacking(boolean t)
	{
		isAttacking = t;
	}
	
	public boolean getIsAttacking()
	{
		return isAttacking;
	}
	
	public void setFirstTimeScout()
	{
		firstTimeScout = true;
	}
	
	public boolean getFirstTimeScout()
	{
		return firstTimeScout;
	}
	
	public void setAttackPosition(Position p)
	{
		root.guiManager.attackPosition.x = p.getX();
		root.guiManager.attackPosition.y = p.getY();
		attackPosition = p;
	}
	
	public Position getAttackPosition()
	{
		return attackPosition;
	}
	
	public GlobalVariables(Bot r) {
		root = r;
	}
	
	public void onFirstFrame()
	{
		setAttackPosition(root.info.bases[0].position);
		numberOfBase = 1;
		firstTimeScout = false;
		isAttacking = false;
		haveGasBuilding = false;
		isEndingGame = false;
		front = 40;
		front_want = 40;
	}
	
	public void onFrame()
	{
		if(front < front_want)
			front = Math.min(front + 0.1, front_want);
		if(front > front_want)
			front = Math.max(front - 0.2, front_want);
		
	}
	
}
