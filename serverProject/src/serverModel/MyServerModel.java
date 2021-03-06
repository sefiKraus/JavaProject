package serverModel;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.eclipse.swt.SWT;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Maze3dGenerator;
import algorithms.mazeGenerators.MyMaze3dGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.mazeGenerators.SimpleMaze3dGenerator;
import algorithms.search.Astar;
import algorithms.search.BFS;
import algorithms.search.Heuristic;
import algorithms.search.MazeAirDis;
import algorithms.search.MazeManDis;
import algorithms.search.Solution;
import algorithms.search.searchableMaze3d;
import io.MyCompressorOutputStream;
import io.MyDecompressorInputStream;

import singletonexplicitpack.Properties;
import sun.management.ManagementFactoryHelper;

/**
 *
 * 
 * 
 * 
 * 
 * <h1>ServerModel</h1> this class represent my controller part of the project,
 * it is suitable for a Maze3d problem.
 * 
 * 
 * <p>
 * <b>Notes:</b>
 *
 * @author Lior Shachar
 * @version 1.0
 * @since 2015-12-17
 */

public class MyServerModel extends Observable  implements Observer{

	private HashMap<String, Object> notifications;

	int port;
	
	ServerSocket server;

	Properties prop;

	private ExecutorService threadPool;
	Thread mainServerThread;

	
	ArrayList<Socket> SocketHolder;
	ClientHandler clientHandler;

	

	volatile boolean stop;

	int numOfClients = 0;
	int clientsHandled = 0;

	public MyServerModel() {

		notifications = new HashMap<String, Object>();
		threadPool = Executors.newCachedThreadPool();

	}

	void scno(String type, Object data) {
		notifications.put(type, data);
		setChanged();
		notifyObservers(type);

	}

	public Object getData(String string) {
		return notifications.get(string);
	}

	public void close() {
		// TODO check if needs anything to add
		try {
			
			scno("msg", "Shutting down...");
			this.threadPool.shutdown();
			this.threadPool.awaitTermination(10, TimeUnit.SECONDS);

		} catch (InterruptedException e) {
			scno("error", "InterruptedException");

		}

	}

	public void start() {
		handleLoadProperties();
		port = prop.getServer_port();
		
		ArrayList<Socket> SocketHolder= new ArrayList<Socket>();
		this.threadPool = Executors.newFixedThreadPool(prop.getClientsCapacity());
		// TODO
																					// EDIT
																					// THE
																					// PROPERTIES
																					// ACCORDINGLY

		scno("modelReady", "");

	}

	public void runServer() {
		clientHandler= new MyMaze3dClientHandler();
		port = prop.getServer_port();
		
		
		try {
			this.server=new ServerSocket(this.port);
			mainServerThread=new Thread(new Runnable() {	// we listen inside a thread		
				@Override
				public void run() {
					while(!stop){
						try {
							final Socket someClient=server.accept(); //  get the client socket
							//SocketHolder.add(someClient);
							//scno("newClient",SocketHolder.size()); // saves the socket and sends the index
							if(someClient!=null){
								threadPool.execute(new Runnable() {									
									@Override
									public void run() {
										try{										
											clientsHandled++;
											System.out.println("\thandling client "+clientsHandled);
											clientHandler.handleClient(someClient.getInputStream(), someClient.getOutputStream()); //create the right client handler
											 
											someClient.close(); 
											System.out.println("\tdone handling client "+clientsHandled);										
										}catch(IOException e){
											e.printStackTrace();
										}									
									}
								});								
							}
						}
						catch (SocketTimeoutException e){
							System.out.println("no clinet connected...");
						} 
						catch (IOException e) {
							e.printStackTrace();
						}
					}
					System.out.println("done accepting new clients.");
				} // end of the mainServerThread task
			});
			
			mainServerThread.start();
			
			
			
			
		} catch (IOException e) {
			System.out.println("cant create server socket");
			e.printStackTrace();
		}
		
		
	}

	public void handleSaveProperties(Properties p, String path) {
		try {
			XMLproperties.writeProperties(p, path);
			scno("msg", "settings saved successfuly");
		} catch (FileNotFoundException e) {
			scno("error", "File Not Found Exception");

		}

	}

	public void handleLoadProperties() {
		try {
			prop = XMLproperties.getMyPropertiesInstance();

		} catch (FileNotFoundException e) {
			try {

				XMLproperties.writeProperties(new Properties(), "resources/properties.xml");
				System.out.println("error : no xml profile, creating a default one");
				prop = XMLproperties.getMyPropertiesInstance();
			} catch (FileNotFoundException e1) {
				System.out.println("fatal error: system cant write or load settings");
			}

		}
	}
	@Override
	public void update(Observable o, Object arg) {
		String note = (String)arg;
		System.out.println(note);
		if(o==this.clientHandler){
			//clientHandler.getData((String) arg);
		}
		
	}

}
