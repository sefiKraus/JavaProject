package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import controller.Command;

public class CLI extends Thread {

	private HashMap<String, Command> hmap;
	private BufferedReader in;
	private PrintWriter out;
	

	
	
	 public PrintWriter getOut() {
		return out;
	}





	public HashMap<String, Command> getHmap() {
		return hmap;
	}





	public void setHmap(HashMap<String, Command> hmap) {
		this.hmap = hmap;
	}





	public BufferedReader getIn() {
		return in;
	}





	public void setIn(BufferedReader in) {
		this.in = in;
	}





	public void setOut(PrintWriter out) {
		this.out = out;
	}





	public CLI(HashMap<String, Command> hmap, BufferedReader in, PrintWriter out) {
		super();
		this.hmap = hmap;
		this.in = in;
		this.out = out;
	}



	 
	 
	@Override
	public void run() {
		String input;
		Set<String> keys=hmap.keySet();
		Command c;
		System.out.println("Hello there please enter your desired command: ");
		try {
			while (!(input=in.readLine()).matches("exit")){
				System.out.println("your order is: "+input);
				for (String s : keys){
					if (input.matches(s)){
						String[] args = input.split(" ");
						c = hmap.get(s);
						c.doCommand(args);
					}
				}
					
			}
			System.out.println("Shutting down...");
			c=hmap.get("exit");
			c.doCommand(null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

	 
	 


	
	
}
