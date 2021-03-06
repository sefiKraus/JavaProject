package view;

import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;

/**
*
* 
* 
* 
* 
* <h1>Model</h1>
* The interface fa�ade of the View section in our MVC architectural pattern
* the model is responsible for presenting the elements and data without "knowing" it.
* 
* 
* 
* <p>
* <b>Notes:</b> 
*
* @author  Lior Shachar
* @version 1.0
* @since   2015-12-17
*/

public interface View {
	   void start();
	void list(String string);
	void printMsg(String s);
	
	void displayMaze(byte[] arr);
	void displayCross(byte[] arr,String by,int i);
	void displaySolution(Solution<Position> s);

}
