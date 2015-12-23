package task;

import java.util.ArrayList;
import java.util.List;

import task.Task.TaskState;
import main.Bot;

public class TaskManager {
	
	Bot root;
	
	public TaskManager(Bot r)
	{
		root = r;
	}
	
	public void onFrameStart()
	{
		List <Task> nextList = new ArrayList<Task>();
		for(Task t : root.listOfTasks)
		{
			t.checkEnd();
			if(t.state == TaskState.ACTIVE)
				nextList.add(t);
		}
		root.listOfTasks = nextList;
	}
	
}
