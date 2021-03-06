package controller;

import java.util.HashMap;

import algorithms.mazeGenerators.Maze3d;
import model.Model;
import view.View;

/**
*
* 
* 
* 
* 
* <h1>MyController</h1>
* this class represent my controller part of the project, it is suitable for a Maze3d problem.
* 
* 
* <p>
* <b>Notes:</b> 
*
* @author  Lior Shachar
* @version 1.0
* @since   2015-12-17
*/

public class MyController implements Controller {
	
	private Model m;
	private View v;
	HashMap<String, Command> commandCreator; // our command map
	

	
	public MyController(Model m, View v) {
		super();
		this.m = m;
		this.v = v;
		commandCreator = new HashMap<String, Command>();
		fillMap(commandCreator);
	}
	public MyController() {
		super();
		commandCreator = new HashMap<String, Command>();
		fillMap(commandCreator);
	}


/**
 * this method puts a regEx string as a key value for our generated Commands inside our command map.
 * that way, the CLI is able to distinct whether the input from the user matches the correct pattern
 *  for the right command. since some of the commands have similar words or multiple different parameters
 *  regEx patterns make sure we get the right syntax. 
 *  <p>
 *  <b>Notes:</b>
 *  
 *  a very helpful website which helps build the right regEx syntax  
 *   {@link regexr.com http://regexr.com/} 
 * @param 
 * 
 */
	public void fillMap(HashMap<String, Command> map){
		
		
		//dir <directory/path>
		map.put("dir [^\n\r]+",new Command(){

			@Override
			public void doCommand(String[] args) {
				v.list(args[1]);
			}});
		
		//generate 3d maze <name> <x size (rows)> <y size (levels)> <z size(columns)>
		map.put("generate 3d maze [^\n\r]+ [0-9]+ [0-9]+ [0-9]+",new Command(){

			@Override
			public void doCommand(String[] args) {
				m.generateMazeThread(args[3],Integer.parseInt(args[5]),Integer.parseInt(args[4]),Integer.parseInt(args[6]));
				
				
			}});
		
		//display <name>
		map.put("display (?!cross section by)(?!solution)[^\n\r]+",new Command(){

			@Override
			public void doCommand(String[] args) {
				v.displayMaze(m.getMazes().get(args[1]));
				
			}});
		
		
		//display cross section by {X,Y,Z} <index> for <name>
		map.put("display cross section by [XYZxyz] [0-9]+ for [^\n\r]+",new Command(){

			@Override
			public void doCommand(String[] args) {
				v.displayCross(m.getMazes().get(args[7]),args[4],Integer.parseInt(args[5]));
				
			}});
		//save maze <name> <file name>
		map.put("save maze [^\n\r]+ [^\n\r]+",new Command(){

			@Override
			public void doCommand(String[] args) {
				m.saveMaze(m.getMazes().get(args[2]),args[3]);
				
			}});
		
//load maze <file name> <name>
		map.put("load maze [^\n\r]+ [^\n\r]+",new Command(){

			@Override
			public void doCommand(String[] args) {
			 m.loadMaze(args[2],args[3]);
				
			}});
		//maze size <name>
		map.put("maze size [^\n\r]+",new Command(){

			@Override
			public void doCommand(String[] args) {
				v.printMsg("Maze size of "+args[2]+" is: "+m.getMazes().get(args[2]).length+" Bytes");
				
			}});
		//file size <name>
		map.put("file size [^\n\r]+",new Command(){

			@Override
			public void doCommand(String[] args) {
				m.fileSize(args[2]);
				
			}});
		//solve <name> <algorithm>
		map.put("solve [^\n\r]+ [^\n\r]+",new Command(){

			@Override
			public void doCommand(String[] args) {
				m.solveMazeThread(args[1],args[2]);
				
			}});
		
		//display solution <name>
		map.put("display solution [^\n\r]+",new Command(){

			@Override
			public void doCommand(String[] args) {
				
				if (m.getSolutions().containsKey(args[2])){
					v.displaySolution(m.getSolutions().get(args[2]));
				}
				else{
					toView("no solution found for this maze");
				}
				
			}});
		
		map.put("exit",new Command(){

			@Override
			public void doCommand(String[] args) {
				m.kill();
				
			}});
		
		
		
		///////////////////////////////////////////////////// commands for testing
		
		
		map.put("[^\n\r]+ equals [^\n\r]+",new Command(){

			@Override
			public void doCommand(String[] args) {
				
				System.out.println((new Maze3d(m.getMazes().get(args[0])).equals(new Maze3d(m.getMazes().get(args[2])))));
					
			}});	
		
		//////////////////////////////////////////////// adds a test thread to run in background and print until stopped
		map.put("runtestthread",new Command(){

			@Override
			public void doCommand(String[] args) {
				
				m.testThread();
					
			}});		
		
		///////////////////////////////////////////////////// list the commands available to the user
		map.put("help",new Command(){

			@Override
			public void doCommand(String[] args) {
				toView("*************************************************************************************************");
				toView("dir <directory/path>");
				toView("generate 3d maze <name of the maze> <x size (rows)> <y size (levels)> <z size(columns)>");
				toView("display <name of the maze>");
				toView("display cross section by <X/Y/Z> <index> for <name of the maze>");
				toView("save maze <name of the maze> <file name / path to file name>");
				toView("load maze <file name / path to file name> <name of the maze>");
				toView("maze size <name of the maze>");
				toView("file size <name of the maze>");
				toView("solve <name of the maze> <BFS/Astar>");
				toView("display solution <name of the maze>");
				toView("*************************************************************************************************");
					
			}});	
	}



public Model getModel() {
	return m;
}



public void setModel(Model m) {
	this.m = m;
}



public View getView() {
	return v;
}



public void setView(View v) {
	this.v = v;
}



public HashMap<String, Command> getCommandCreator() {
	return commandCreator;
}



public void setCommandCreator(HashMap<String, Command> commandCreator) {

	this.commandCreator = commandCreator;
}

/**
 * pass a string to the View section, which uses the right output to print it
 */
@Override

public void toView(String s) {
	v.printMsg(s);
	
}

	
	
	
	
	}
	

