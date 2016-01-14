package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import algorithms.search.State;
import widgets.GameCharacter;
import widgets.Maze2D;
import widgets.MazeDisplayer;
import widgets.MyMazeWidget;

public class GuiWindowView extends BasicWindow implements View{
	
	
	MyMazeWidget mazeWin; 
	
	
	HashMap<String, Object> notifications;
	HashMap<String, Listener> listeners;
	KeyListener keys;

	List l;
	 Text nametxt,heighttxt,widthtxt,levelstxt;
	 Label genlbl,mazelistlbl,namelbl,heightlbl,widthlbl,levelslbl;
	 Button playButton,genButton,solveButton;
	 String temp;
	 Timer timer;
	 TimerTask task;
	 /////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 public GuiWindowView(String title, int width, int height) {
		super(title, width, height);
		listeners = new HashMap<String, Listener>();
		notifications = new HashMap<String, Object>();
	}

	 
	 
	@Override
	void initWidgets() {
		initListeners();

		shell.setLayout(new GridLayout(5,false));
		//***************************************************************** MAIN MENU BAR
        Menu menuBar = new Menu(shell, SWT.BAR);
      //***************************************************************** File Cascade
        MenuItem cascadeFileMenu = new MenuItem(menuBar, SWT.CASCADE); // file is a  top tier menu item and menu bar is its parent
        cascadeFileMenu.setText("&File");
      //*****************************************************************// turn the file menu to a drop down menu
        Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
        cascadeFileMenu.setMenu(fileMenu);
      //*****************************************************************// 
        MenuItem xmlItem = new MenuItem(fileMenu, SWT.PUSH);
        xmlItem.setText("Open properties");
      //*****************************************************************// 
        MenuItem loadItem = new MenuItem(fileMenu, SWT.PUSH);
        loadItem.setText("Load");
        loadItem.addListener(SWT.Selection ,listeners.get("loadmazefile"));
      //*****************************************************************
        MenuItem saveItem = new MenuItem(fileMenu, SWT.PUSH);
        saveItem.setText("Save");
        saveItem.addListener(SWT.Selection ,listeners.get("SaveToFile"));
      //*****************************************************************  
        MenuItem exitItem = new MenuItem(fileMenu, SWT.PUSH);
        exitItem.setText("&Exit");
      //*****************************************************************//
        
        
      //*****************************************************************// Options Cascade
        MenuItem cascadeOptionsMenu = new MenuItem(menuBar, SWT.CASCADE);  
        cascadeOptionsMenu.setText("Options");
        Menu OptionsMenu = new Menu(shell, SWT.DROP_DOWN);
        cascadeOptionsMenu.setMenu(OptionsMenu);
      //*****************************************************************
        MenuItem fileSizeItem = new MenuItem(OptionsMenu, SWT.PUSH);
        fileSizeItem.setText("Maze file size details");
        fileSizeItem.addListener(SWT.Selection, listeners.get("FileSize"));
      //*****************************************************************
        MenuItem mazeSizeItem = new MenuItem(OptionsMenu, SWT.PUSH);
        mazeSizeItem.setText("Maze memory size details");
        mazeSizeItem.addListener(SWT.Selection, listeners.get("MazeSize"));
      //*****************************************************************
        
        
      //*****************************************************************
        MenuItem cascadeHelpMenu = new MenuItem(menuBar, SWT.CASCADE); // Help Cascade
        cascadeHelpMenu.setText("Help");
        Menu HelpMenu = new Menu(shell, SWT.DROP_DOWN);
        cascadeHelpMenu.setMenu(HelpMenu);
      //*****************************************************************
        MenuItem aboutItem = new MenuItem(HelpMenu, SWT.PUSH);
        aboutItem.setText("About");
        aboutItem.addListener(SWT.Selection ,listeners.get("about"));
      //*****************************************************************
        
     
        
      //*****************************************************************//"generate a maze" label
        genlbl = new Label(shell,SWT.None);
        genlbl.setText("Generate a new maze");
        genlbl.setBounds(shell.getClientArea());
        genlbl.setLayoutData(new GridData(SWT.None, SWT.None, true,false, 2, 1));
        
      //*****************************************************************//
        
        
      //*****************************************************************//"maze list" label
        mazelistlbl = new Label(shell,SWT.None);
        mazelistlbl.setText("Maze list");
        mazelistlbl.setLayoutData(new GridData(SWT.None, SWT.None, false,false, 3, 1));
        //*****************************************************************//
        
        //*****************************************************************//"name" label
        namelbl = new Label(shell,SWT.BORDER);
        namelbl.setText("Maze name");
        namelbl.setLayoutData(new GridData(SWT.None, SWT.None, false,false, 1, 1));
        
      //*****************************************************************//
        
        //*****************************************************************//"maze name" text
       nametxt = new Text(shell, SWT.SINGLE | SWT.BORDER);
       nametxt.setLayoutData(new GridData(SWT.None, SWT.None, false,false, 1, 1));
       
        //*****************************************************************//

       //*****************************************************************// Maze List
 		l = new List(shell, SWT.MULTI | SWT.BORDER  );
 	    l.setLayoutData(new GridData(SWT.None, SWT.None, false,true, 1, 4));
       //*****************************************************************
       
      //*****************************************************************//Play Button
        playButton=new Button(shell, SWT.PUSH);
		playButton.setText("Play");
		playButton.setLayoutData(new GridData(SWT.None, SWT.None, false,false, 2, 4));
		playButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				solveButton.setEnabled(true);
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
		});
		playButton.addListener(SWT.Selection,listeners.get("mazewindow"));
      //*****************************************************************
		
		//*****************************************************************//"height" label
        heightlbl = new Label(shell,SWT.BORDER);
        heightlbl.setText("Height(in cells)");
        heightlbl.setLayoutData(new GridData(SWT.None, SWT.None, false,false, 1, 1));
        
      //*****************************************************************//
        
        //*****************************************************************//"height" text
        heighttxt = new Text(shell, SWT.SINGLE | SWT.BORDER);
       heighttxt.setLayoutData(new GridData(SWT.None, SWT.None, false,false, 1, 1));
       
        //*****************************************************************//
       
     //*****************************************************************//width label
       widthlbl = new Label(shell,SWT.BORDER);
       widthlbl.setText("Width(in cells)");
       widthlbl.setLayoutData(new GridData(SWT.None, SWT.None, false,false, 1, 1));
       
     //*****************************************************************//
       
       //*****************************************************************//width text
       widthtxt = new Text(shell, SWT.SINGLE | SWT.BORDER);
       widthtxt.setLayoutData(new GridData(SWT.None, SWT.None, false,false, 1, 1));
      
       //*****************************************************************//
	    
     //*****************************************************************//levels label
       levelslbl = new Label(shell,SWT.BORDER);
       levelslbl.setText("Levels");
       levelslbl.setLayoutData(new GridData(SWT.None, SWT.None, false,false, 1, 1));
       
     //*****************************************************************//
       
       //*****************************************************************//levels text
       levelstxt = new Text(shell, SWT.SINGLE | SWT.BORDER);
       levelstxt.setLayoutData(new GridData(SWT.None, SWT.None, false,false, 1, 1));
      
       //*****************************************************************//
        
     //*****************************************************************//Generate Button
       genButton=new Button(shell, SWT.PUSH);
		genButton.setText("Generate!");
		genButton.setLayoutData(new GridData(SWT.None, SWT.None, false,false, 1, 1));
		genButton.addListener(SWT.Selection,listeners.get("generateButton"));
		
     //*****************************************************************
		
	     //*****************************************************************//Solve Button
	       solveButton=new Button(shell, SWT.PUSH);
			solveButton.setText("Solve the maze for me!");
			solveButton.setLayoutData(new GridData(SWT.None, SWT.None, false,false, 1, 1));
			solveButton.addListener(SWT.Selection,listeners.get("solveButton"));
			solveButton.setEnabled(false); // only when there's a playable maze the option should be enabled
	     //*****************************************************************
		
		
        

        shell.setMenuBar(menuBar);
        shell.pack();
        
		
		
	}

	 
	public void initListeners() {

		// **************************************{ KEY LISTENER
		// }***********************************
		this.keys = new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_UP) {
					mazeWin.moveLeft();
				} else if (e.keyCode == SWT.ARROW_DOWN) {
					mazeWin.moveRight();
				} else if (e.keyCode == SWT.ARROW_LEFT) {
					mazeWin.moveBackward();
				} else if (e.keyCode == SWT.ARROW_RIGHT) {
					mazeWin.moveForward();
				} else if (e.keyCode == SWT.PAGE_UP) {
					mazeWin.moveUp();
				} else if (e.keyCode == SWT.PAGE_DOWN) {
					mazeWin.moveDown();
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		};

		// ***************************************************************************************************************
		listeners.put("about", new Listener() {
			public void handleEvent(Event event) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION);
				messageBox.setMessage("Made By Lior Shachar 2016, all rights reserved.");
				messageBox.setText("About my java algorithmic project");
				messageBox.open();

			}
		});
		// ***************************************************************************************************************
		listeners.put("loadmazefile", new Listener() {
			public void handleEvent(Event event) {

				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Please choose a file to load from");
				fd.setFilterPath("C:/");
				String[] filterExt = { "*.maz", "*.*" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();

				Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

				dialog.setLayout(new GridLayout(3, false));
				dialog.setSize(400, 100);

				Label messageLabel = new Label(dialog, SWT.BORDER);
				messageLabel.setText("Please Choose a name for the loaded maze");
				messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
				Text txt = new Text(dialog, SWT.CENTER | SWT.BORDER);
				Button okButton = new Button(dialog, SWT.PUSH);
				okButton.setText("Ok");
				okButton.setLayoutData(new GridData(SWT.None, SWT.None, false, false, 1, 1));

				okButton.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						temp = txt.getText();
						if (temp.matches("([A-Za-z0-9])\\w+")) {
							String args[] = { selected, temp };
							scno("loadfrom", args);
							dialog.close();
						} else
							showError("Invalid name");

					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {

					}
				});
				if (selected != null)
					dialog.open();

			}
		});

		// ***************************************************************************************************************

		listeners.put("mazewindowsthread", new Listener() {
			public void handleEvent(Event event) {
				/*
				 * new Thread(new Runnable() {
				 * 
				 * @Override public void run() { ChildWindow another= new
				 * ChildWindow("Maze Game", 800, 500,listeners,keylis);
				 * another.run();
				 * 
				 * } }).start();
				 */
			}
		});
		// ***********************************{opens the maze widget}****************************************************

		listeners.put("mazewindow", new Listener() {
			public void handleEvent(Event event) {
				if (l.getSelection().length > 0) {
					String selected = l.getSelection()[0];
					scno("initMazeWidgetRequest", selected);
				} else {
					scno("error", "no maze selected");
					solveButton.setEnabled(false);
				}
			}
		});
		// ***************************************************************************************************************
		listeners.put("generateButton", new Listener() {
			public void handleEvent(Event event) {
				if (heighttxt.getText().matches("[1-9]\\d*")
						&& widthtxt.getText().matches("[1-9]\\d*")
						&& levelstxt.getText().matches("[1-9]\\d*")
						&& nametxt.getText() != null) {
					String param[] = { nametxt.getText(), levelstxt.getText(),
							heighttxt.getText(), widthtxt.getText() };
					scno("generateDetails", param);
				} else
					showError("Invalid Values");

			}
		});

		// ***************************************************************************************************************
		listeners.put("MazeSize", new Listener() {
			public void handleEvent(Event event) {
				if (l.getSelection().length > 0) {
					String selected = l.getSelection()[0];
					scno("MazeSizeRequest", selected);
				} else
					scno("error", "no maze selected");
			}
		});
		// ***************************************************************************************************************
		listeners.put("FileSize", new Listener() {
			public void handleEvent(Event event) {
				if (l.getSelection().length > 0) {
					String selected = l.getSelection()[0];
					scno("FileSizeRequest", selected);
				} else
					scno("error", "no maze selected");
			}
		});
		// ***************************************************************************************************************
		listeners.put("SaveToFile", new Listener() {
			public void handleEvent(Event event) {
				FileDialog savedialog = new FileDialog(shell, SWT.SAVE);
				savedialog.setFilterNames(new String[] { "Maze Files", "All Files (*.*)" });
				savedialog.setFilterExtensions(new String[] { "*.maz", "*.*" }); // Windows
				// wild
				// cards
				savedialog.setFilterPath("c:\\"); // Windows path
				savedialog.setFileName("newMaze.maz");
				if (l.getSelection().length > 0) {
					String filepath = savedialog.open();
					if (filepath != null) {
						String saveparams[] = { l.getSelection()[0], filepath };
						scno("PathToSaveMaze", saveparams);
					}
				} else
					scno("error", "no maze selected");
			}
		});
		// ***************************************************************************************************************
		listeners.put("solveButton", new Listener() {
			public void handleEvent(Event event) {
				
				
				Object solvedetails[] = { mazeWin.getCurrentPosition(), mazeWin.getMazeName() };
				scno("solveRequest", solvedetails);
			}
		});
		// ***************************************************************************************************************
	

	
	
	}
	
	void scno(String type, Object data) {
		notifications.put(type, data);
		setChanged();
		notifyObservers(type);

	}
	
	
	public void showError(String s) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
		messageBox.setMessage(s);
		messageBox.setText("Error!");
		messageBox.open();

	}

	
	/**
	 * initialize the Maze widget with the details needed and starts it
	 * */
	public void initMazeWidget(Maze3d maze,String name) {
		Shell mazeshell = new Shell(shell, SWT.SHELL_TRIM);
		mazeshell.setLayout(new GridLayout(1, false));
		mazeshell.setSize(400, 400);
		mazeWin= new MyMazeWidget(mazeshell, SWT.NONE, maze,name);
		mazeWin.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,true,1,1));
		mazeWin.addKeyListener(keys);
		
		
		mazeshell.addDisposeListener(new DisposeListener() {
			
			
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				if(solveButton.isEnabled())
					solveButton.setEnabled(false);
				if (mazeWin!=null)
				mazeWin.dispose();
				if (timer!=null)
				timer.cancel();
				
			}
		});
		
		mazeshell.open();
		
		
		
	}

	@Override
	public void start() {
		run();
		
	}



	@Override
	public void showList(String string) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void showMsg(String s) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WORKING);
		messageBox.setMessage(s);
		messageBox.setText("Message");
		messageBox.open();

	}







	@Override
	public void showMaze(byte[] arr) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void showCross(byte[] arr, String by, int i) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void showSolution(Solution<Position> s) {
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				
				ArrayList<State<Position>> states = s.getSolution(); // an
																		// arraylist
																		// of
																		// states
																		// to
																		// get
																		// from
																		// our
																		// solution

				System.out.println(s.getSolution().size());

				mazeWin.setFocus();
				timer = new Timer();
				
				

				Iterator<State<Position>> mazeIterator = states.iterator();
				timer.scheduleAtFixedRate(new TimerTask() {
					
					@Override
					public void run() {
						if(mazeIterator.hasNext()&&!mazeWin.isDisposed()){ 
							
							mazeWin.getDisplay().syncExec(new Runnable() {

								@Override
								public void run() {
									State<Position> state=mazeIterator.next();
									mazeWin.moveCharacter(state.getState());
									
								}
							});
							
							
					}
						else
						{
							
							
							timer.cancel();
							}
					}
						
				}, 0, 200);

			}
		});

	}
		
	



	@Override
	public void showExit() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public Object getData(String string) {
		return notifications.get(string);
	}



	@Override
	public void displayLoadedMaze(String s) {
		l.add(s);
		
	}

}