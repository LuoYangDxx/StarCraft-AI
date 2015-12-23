package main;
import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import gui.GUIManager;
import headquarter.EconomyHQ;
import headquarter.HQ;
import headquarter.StrategyHQ;
import information.AllMapInfo;
import information.EnemyInfo;
import information.GameInfo;
import information.GlobalVariables;
import information.Goal;
import information.PathInfo;

import java.io.PrintWriter;
import java.util.*;

import javax.swing.RootPaneContainer;

import strategy.Strategy;
import task.Task;
import task.TaskManager;
import component.ArmyControlManager;
import component.ArmyProductionManager;
import component.BaseManager;
import component.Component;
import component.ExpansionManager;
import component.ScoutManager;
import component.SupplyManager;
import component.TechBuildingManager;
import component.UpgradeManager;
import computation.General;

public class Bot extends DefaultBWListener {
	
    private Mirror mirror = new Mirror();
    public Game game;
    public Player self, enemy;
    
    boolean isFirstFrame;
    
    public computation.General util;
    public gui.GUIManager guiManager;
    public task.TaskManager taskManager;
    public information.GameInfo info;
    public information.EnemyInfo enemyInfo;
    public information.Goal goal;
    public Strategy strategy;
    public GlobalVariables blackboard;
    public AllMapInfo allMapInfo;
    
    // HQ
    public List <HQ> listOfHQ;
    public EconomyHQ economyHQ;
    public StrategyHQ strategyHQ;
    
    // Component
    public List <Component> listOfComponents;
    public BaseManager baseManager;
    public ArmyControlManager armyControlManager;
    public ArmyProductionManager armyProductionManager;
    public SupplyManager supplyManager;
    public TechBuildingManager techBuildingManager;
    public UpgradeManager upgradeManager;
    public ScoutManager scoutManager;
    public ExpansionManager expansionManager;
    
    // Task
    public List <Task> listOfTasks;
    
    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }
    
    @Override
    public void onUnitDiscover(Unit unit)
    {
    	enemyInfo.onUnitCreate(unit);
    }
    
    @Override
    public void onUnitMorph(Unit unit)
    {
    	info.onUnitCreate(unit);
    	
    	//System.out.println("New unit " + unit.getType());
    }
    
    @Override
    public void onUnitCreate(Unit unit)
    {
    	info.onUnitCreate(unit);
    	enemyInfo.onUnitCreate(unit);
        //System.out.println("New unit " + unit.getType());
    }
    
    @Override
    public void onUnitDestroy(Unit unit) 
    {
    	//System.out.println("destory unit " + unit.getType());
    	info.onUnitDestroy(unit);
    	enemyInfo.onUnitDestroy(unit);
    };
    
    @Override
    public void onStart() {
    	
    	isFirstFrame = true;
    	
        game = mirror.getGame();
        self = game.self();
        enemy = game.enemies().get(0);
        
        game.setLocalSpeed(0);
        game.enableFlag(1);
        
        
        util = new General(this);
        info = new GameInfo(this);
        enemyInfo = new EnemyInfo(this);
        guiManager = new GUIManager(this);
        taskManager = new TaskManager(this);
        goal = new Goal(this);
        blackboard = new GlobalVariables(this);
        allMapInfo = new AllMapInfo(this);
        
        listOfHQ = new ArrayList<HQ>();
        listOfComponents = new ArrayList<Component>();
        listOfTasks = new ArrayList<Task>();
        
        // HQ
        economyHQ = new EconomyHQ(this);
        listOfHQ.add(economyHQ);
        strategyHQ = new StrategyHQ(this);
        listOfHQ.add(strategyHQ);
        
        
        // Component
        baseManager = new BaseManager(this);
        armyControlManager = new ArmyControlManager(this);
        armyProductionManager = new ArmyProductionManager(this);
        supplyManager = new SupplyManager(this);
        techBuildingManager = new TechBuildingManager(this);
        upgradeManager = new UpgradeManager(this);
        scoutManager = new ScoutManager(this);
        expansionManager = new ExpansionManager(this);
        
        listOfComponents.add(baseManager);
        listOfComponents.add(armyControlManager);
        listOfComponents.add(armyProductionManager);
        listOfComponents.add(supplyManager);
        listOfComponents.add(techBuildingManager);
        listOfComponents.add(upgradeManager);
        listOfComponents.add(scoutManager);
        listOfComponents.add(expansionManager);
        
        
        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready");
    }
    
    
    
    
    
    @Override
    public void onFrame() {
    	
    	/*
    	 * TODO:
    	 * 1. Re analyze map when find enemy
    	 * 2. RepairManager
    	 * 3. Scout for enemy bases
    	 * 4. Map analyze
    	 * 5. Army Control
    	 * 6. Add micro
    	 * 7. Reaver, high-templar
    	 */
    	
    	guiManager.onFrameStart();
    	
    	if(isFirstFrame)
    	{
    		isFirstFrame = false;
    		info.onFirstFrame();
    		enemyInfo.onFirstFrame();
    		blackboard.onFirstFrame();
    		allMapInfo.onFirstFrame();
    	}
    	
    	info.onFrameStart();
    	enemyInfo.onFrame();
    	blackboard.onFrame();
    	allMapInfo.onFrame();
    	
    	taskManager.onFrameStart();
    	for(Component c : listOfComponents)
    		c.onFrameStart();
    	
    	guiManager.addDebugInfo("#Tasks = " + listOfTasks.size());
    	for(Task t : listOfTasks)
    	{
    		guiManager.addDebugInfo("(" + t.needMinerials() + ") " + t.getName());
    	}
    	guiManager.addDebugInfo("#BaseLocations = " + bwta.BWTA.getBaseLocations().size());
    	
    	
    	strategyHQ.onFrame();
    	economyHQ.onFrame();
    	
    	
    	for(Task t : listOfTasks)
			t.onFrame();
    	
    	guiManager.onFrameEnd();
    	
    	for(Unit u : self.getUnits())
    	{
    		if(info.getTask(u) != null)
    			if(info.getTask(u).creator != null)
    			{
    				game.setTextSize(8);
    				game.drawTextMap(u.getPosition().getX() + 10, u.getPosition().getY() + 10, info.getTask(u).getName());
    				game.drawTextMap(u.getPosition().getX() + 10, u.getPosition().getY() + 25, info.getTask(u).creator.getName());
    			}
    	}
    	
    	int x = (game.getMousePosition().getX() + game.getScreenPosition().getX()) / 32;
    	int y = (game.getMousePosition().getY() + game.getScreenPosition().getY()) / 32;
    	
    	if(info.bases[info.bases.length-1].pathFromMyBase != null)
    	{
    		info.bases[info.bases.length-1].pathFromMyBase.display();
    	}
    	
    	
    	
    }
    
    public static void main(String[] args) {
    	//try{
    		new Bot().run();
    	/*}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		
    		try{
    			PrintWriter writer = new PrintWriter((new Date(System.currentTimeMillis())).toString().replace(' ', '_').replace(':', '_') +  ".txt");
    			e.printStackTrace(writer);
    			writer.close();
    		} catch (FileNotFoundException e1) {
				
				e1.printStackTrace();
			}
    	}*/
    }
}
