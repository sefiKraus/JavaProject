package model;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.AbstractDocument.LeafElement;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.MazeProblem;
import algorithms.mazeGenerators.MyMaze3dGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.Astar;
import algorithms.search.BFS;
import algorithms.search.MazeManDis;
import algorithms.search.Solution;
import algorithms.search.searchableMaze3d;
import controller.Presenter;
import gnu.trove.list.array.TByteArrayList;
import io.MyCompressorOutputStream;
import io.MyDecompressorInputStream;

/**
*
* 
* 
* 
* 
* <h1>MyModel</h1>
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



public class MyModel extends CommonModel  {
	
	HashMap<String, byte[]> mazes;
	HashMap<String,Solution<Position>> solutions;
	ArrayList<Thread> threads;
	String error;
	String msg;
	
	
	 public MyModel(){
	  
	   mazes = new HashMap<String, byte[]>();
	   solutions =new HashMap<String,Solution<Position>>();
	   threads= new ArrayList<Thread>();
	 }

	
	



/**
 * generate a maze based on the coordinates size given and save it on the system with the specified name
 */

	@Override
	public void handleGenerate(String name, int y, int x, int z) {
		
		msg="**generating maze**";
		setChanged();
		notifyObservers("msg");
		MyMaze3dGenerator gen = new MyMaze3dGenerator();
		Maze3d created = gen.generate(y,x,z);
		mazes.put(name,created.toByteArray());
		msg="**maze "+name+" is ready**";
		setChanged();
		notifyObservers("msg");
		
	}
	
	/**
	 * same as generateMaze only threaded
	 */
	
public void generateMazeThread(String name, int y, int x, int z) {
		threads.add(new Thread(new Runnable() {  //creates the thread
		   public void run() {
			   handleGenerate(name,y,x,z);
		   }
		 }));
		if (threads != null && !threads.isEmpty()) { // add the thread to our list and starts it
			  threads.get(threads.size()-1).start();
			}
		
		
	}




/**
 * save an array of bytes that represent a maze in the specified path
 */
@Override
public void handleSaveMaze(byte[] maze, String path) {
	try {
		MyCompressorOutputStream writer = new MyCompressorOutputStream(new FileOutputStream(path));
		writer.write(maze);
		writer.close();
		msg="**Save completed successfuly**";
		setChanged();
		notifyObservers("msg");
	} catch (IOException e) {
		error="error: could not save";
		setChanged();
		notifyObservers("err");
		e.printStackTrace();
	}
	
}



/**
 * load a maze from the path given and save it in the system with the specified name
 */

@Override
public void handleLoadMaze(String path,String name) {
	try {
		// we need to know the array size, the compressed version of the maze have a pattern of (value,number of returns)
		//so if we sum up all the values in the odd index places well get the right size
		DataInputStream reader = new DataInputStream(new FileInputStream(path));
		int val=0;
		int i=0;
		int sum=0;
		while(reader.available()>=4){
			val=reader.readInt();
			if(!(i%2==0)){
				sum+=val;
			}
				i++;
		}
		reader.close();
		
		byte[] arr= new byte[sum];
		MyDecompressorInputStream comp = new MyDecompressorInputStream(new FileInputStream(path));
		comp.read(arr);
		comp.close();
		mazes.put(name, arr);
		msg="Loading completed successfuly";
		setChanged();
		notifyObservers("msg");
		
	} catch (IOException e ) {
		error=("error: could not load from the file specified");
		setChanged();
		notifyObservers("err");
	}
	
}


/**
 * saves the maze in a temporary file in order to test its size and return it
 */

@Override
public void handleFileSize(String name) {
	handleSaveMaze(mazes.get(name),"testfile.maz");
	File test = new File("testfile.maz");
	
	msg="File Size of "+name+": "+test.length()+" Bytes";
	setChanged();
	notifyObservers("msg");
	test.delete();

	
}


/**
 * solve a maze inside a thread
 */

public void handleSolveMazeThread(String name,String algo){
	threads.add(new Thread(new Runnable() {  //creates the thread
		   public void run() {
			   handleSolveMaze(name,algo);
		   }
		 }));
		if (threads != null && !threads.isEmpty()) { // add the thread to our list and starts it
			  threads.get(threads.size()-1).start();
			}
}


/**
 * solve the maze represented by a key name with the algorithm specified in the string algo
 */

@Override
public void handleSolveMaze(String name,String algo) {
	if(mazes.containsKey(name)){
		msg="Maze name found";
		setChanged();
		notifyObservers("msg");
		if (algo.matches("[Bb][Ff][Ss]")|| algo.matches("[Aa][Ss][Tt][Aa][Rr]")){
			
			msg="algorithm found";
			setChanged();
			notifyObservers("msg");
			if ( algo.matches("[Bb][Ff][Ss]")){
				
				msg="Solving "+name+" using "+algo;
				setChanged();
				notifyObservers("msg");
				Solution<Position> sol = new BFS<Position>().search(new searchableMaze3d(new Maze3d(mazes.get(name))));
				solutions.put(name,sol);
				msg="solution for "+name+" is ready";
				setChanged();
				notifyObservers("msg");
				
			}
			else if ( algo.matches("[Aa][Ss][Tt][Aa][Rr]")){
				
				msg="Solving "+name+" using "+algo;
				setChanged();
				notifyObservers("msg");
				Solution<Position> sol = new Astar<Position>(new MazeManDis()).search(new searchableMaze3d(new Maze3d(mazes.get(name))));
				solutions.put(name,sol);
				msg="solution for "+name+" is ready";
				setChanged();
				notifyObservers("msg");
			}
				
		}
		
		else{error="algorithm not found";
		setChanged();
		notifyObservers("err");
		}
		
		
	}
	else{error="maze not found";
	setChanged();
	notifyObservers("err");}
	
			
	
	
}



/**
 * shutting down every thread that was created by this model
 */
@SuppressWarnings("deprecation")
@Override
public void handleKill() {
	for (Thread t : threads){
		if(t.isAlive())
		t.stop();
	}
	
}


// adds a background thread for testing

@Override
public void testThread() {
	
	threads.add(new Thread(new Runnable() {  //creates the thread
		   public void run() {
			  while(true){
				  System.out.println("*** test Thread running ***");
				  try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				 System.out.println("test thread interrupted");
					e.printStackTrace();
				}
			  }
		   }
		 }));
		if (threads != null && !threads.isEmpty()) { // add the thread to our list and starts it
			  threads.get(threads.size()-1).start();
			}
}






public HashMap<String, byte[]> getMazes() {
	return mazes;
}






public HashMap<String, Solution<Position>> getSolutions() {
	return solutions;
}






public String getError() {
	return error;
}






public String getMsg() {
	return msg;
}












}
