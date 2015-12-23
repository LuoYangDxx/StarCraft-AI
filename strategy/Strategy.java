package strategy;

import main.Bot;

public abstract class Strategy {
	
	Bot root;
	
	public Strategy(Bot r) {
		root = r;
	}
	
	public abstract void onFrame();
	public abstract boolean ended();
	public abstract String getName();
	
}
