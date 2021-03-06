package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
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
import model.XMLproperties;
import singletonexplicitpack.Properties;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import widgets.GameCharacter;
import widgets.MazeDisplayer;
import widgets.MyMazeWidget;
import widgets.PropertiesWidget;
import widgets.WidCommand;

public class GuiWindowView extends commonGuiView implements View{
	
	
	MyMazeWidget mazeWin; 
	
	//////////////////////{WIDGET FUNCTIONS COMPONENTS}////////////////////////////
	AudioStream audioStream;
	Image victorySplashImage=new Image(display, "resources/winsplash.jpg");
	Image goalImage;
	//////////////////////////////////////////////////////////
	
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
	 
	 boolean canExitAll=true;
	 
	 /////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 public GuiWindowView(String title, int width, int height) {
		super(title, width, height);
		listeners = new HashMap<String, Listener>();
		notifications = new HashMap<String, Object>();
	}

	 
	 
	@Override
	void initWidgets() {
		initListeners();

		
		
		
		
		
		shell.setLayout(new GridLayout(5,true));
		
		//***************************************************************** MAIN MENU BAR
        Menu menuBar = new Menu(shell, SWT.BAR);
      //***************************************************************** File Cascade
        MenuItem cascadeFileMenu = new MenuItem(menuBar, SWT.CASCADE); // file is a  top tier menu item and menu bar is its parent
        cascadeFileMenu.setText("&File");
      //*****************************************************************// turn the file menu to a drop down menu
        Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
        cascadeFileMenu.setMenu(fileMenu);
      //*****************************************************************// 
        MenuItem saveXmlItem = new MenuItem(fileMenu, SWT.PUSH);
        saveXmlItem.setText("Change properties");
        saveXmlItem.addListener(SWT.Selection ,listeners.get("saveSettings"));
        
      //*****************************************************************// 
        MenuItem loadXmlItem = new MenuItem(fileMenu, SWT.PUSH);
        loadXmlItem.setText("Load Custom properties");
        loadXmlItem.addListener(SWT.Selection ,listeners.get("loadSettings"));
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
        exitItem.addListener(SWT.Selection ,listeners.get("exitItem"));
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
 		l = new List(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL );
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
			solveButton.addListener(SWT.Selection,listeners.get("ShowMe"));
			solveButton.setEnabled(false); // only when there's a playable maze the option should be enabled
	     //*****************************************************************
		
		
        

        shell.setMenuBar(menuBar);
        shell.pack();
        
		
		
	}

	/**
	 * 
	 * 
	 * initialize the listeners map from which the gui components get their functionality
	 * 
	 * 
	 * **/
	
	 
	 void initListeners() {
		 
		 //****************************************************** DISPOSE LISTENER
		 
		 shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				scno("GuiDisposed", "");
				
			}
		});
		 
		 
		 
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
				

			}
		};

		
		// ***************************************************************************************************************
		
		listeners.put("loadSettings", new Listener() {
			public void handleEvent(Event event) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Please choose a file to load from");
				fd.setFilterPath("resources/");
				String[] filterExt = { "*.xml", "*.*" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
				if (selected != null)
					scno("loadSettings", selected);


			}
		});
				
				
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

		listeners.put("saveSettings", new Listener() {
			public void handleEvent(Event event) {
				Shell pshell = new Shell(display, SWT.SHELL_TRIM);
				PropertiesWidget pmenu = new PropertiesWidget(pshell, SWT.NONE);
				pmenu.setLayout(new GridLayout(3, false));
						
						pshell.setSize(247, 400);
						pshell.setLayout(new GridLayout(SWT.FILL, false));
						pshell.pack();
						pshell.open();
						
						
						
				
				
				pmenu.btnSaveTo.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						
						// when the user presses the button a file dialog opens,and then we send the presenter the new properties instance and the path 
						FileDialog pdialog = new FileDialog(pshell, SWT.SAVE);
					    pdialog
					        .setFilterNames(new String[] { "XML files", "All Files (*.*)" });
					    pdialog.setFilterExtensions(new String[] { "*.xml", "*.*" }); // Windows
					                                    // wild
					                                    // cards
					    pdialog.setFilterPath("resources/"); // Windows path
					    pdialog.setFileName("properties.xml");
					   String ppath;
					   if (pmenu.getProp()!=null&& ((ppath=pdialog.open())!=null)){
						   Object choices[]={pmenu.getProp(),ppath};
						   scno("saveSettings", choices); 
						   pshell.dispose();
					   }
						  
							  
					   
					   
						   
						
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					
						
					}
				});
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
		
		listeners.put("ShowMe", new Listener() {
			public void handleEvent(Event event) {
				
				
				Object solvedetails[] = { mazeWin.getCurrentPosition(), mazeWin.getMazeName() };
				scno("ShowMe", solvedetails);
			}
		});
		
		// ***************************************************************************************************************
				
				listeners.put("exitItem", new Listener() {
					public void handleEvent(Event event) {
					close();
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
		
		display.syncExec(new Runnable() {

			@Override
			public void run() {

				MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
				messageBox.setMessage(s);
				messageBox.setText("Error!");
				messageBox.open();
				
			}
		});
		
		

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
		
		////////////////////////////////////////////////a special command to inject to the Widget
		/////////////this command is the victory mode of my widget (acts as a special listener)
		
		mazeWin.setCommand(new WidCommand() {
			
			@Override
			public void doCommand() {
				InputStream in;
				try {
					if(XMLproperties.getMyPropertiesInstance().isSound())
					try {
						in = new FileInputStream("resources/dandan.mid");
						 // create an audiostream from the inputstream
					    audioStream = new AudioStream(in);
					 
					    // play the audio clip with the audioplayer class
					    AudioPlayer.player.start(audioStream);
					    
					} catch (FileNotFoundException e1) {
						scno("error", "music file not found");
						
					} catch (IOException e1) {
						scno("error", "music file I/O problem");
						
					}
				} catch (FileNotFoundException e1) {
					scno("error", "properties file corrupted");
				}
			Shell winshell = new Shell (mazeWin.getShell(), SWT.FILL |SWT.DOUBLE_BUFFERED);
			winshell.setLayout(new FillLayout ());
	        

	        winshell.addListener (SWT.Paint, new Listener () 
	        {
	            public void handleEvent (Event e) {
	                GC gc = e.gc;
	                int x = 0, y = 0;
	                gc.drawImage (victorySplashImage, x, y);
	                gc.dispose();
	            }
	        });
	        winshell.setSize(victorySplashImage.getBounds().width, victorySplashImage.getBounds().height);
	        winshell.setLocation(mazeWin.getShell().getLocation());
	       
	        winshell.open ();
	        
	       
	     winshell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				try {
					if(XMLproperties.getMyPropertiesInstance().isSound())
					AudioPlayer.player.stop(audioStream);
				} catch (FileNotFoundException e1) {
					scno("error", "properties file corruped");
				}
				
			}
		});
				
			}
		});
		
		
		mazeshell.addDisposeListener(new DisposeListener() { ////////////////////// WE NEED TO KNOW IF THE GAME WINDOW IS DISPOSED
			
			
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				//
				if(!mazeWin.getMaze().getStartPosition().equals(mazeWin.getCurrentPosition())&&!mazeWin.getMaze().getGoalPosition().equals(mazeWin.getCurrentPosition()) ){
				 MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
				            | SWT.YES | SWT.NO);
				        messageBox.setMessage("Would you like to change current position?");
				        messageBox.setText("Exiting Maze");
				        int response = messageBox.open();
				        if (response == SWT.YES){
				        	Object updatecurr[] ={new Position(mazeWin.getCurrentPosition()),mazeWin.getMazeName()};
				          scno("updateStart",updatecurr );
				        }
				}
				if (timer!=null)
					timer.cancel();
				if (task!=null)
					timer.cancel();
				if(solveButton.isEnabled())
					solveButton.setEnabled(false);
				if (mazeWin!=null)
				mazeWin.dispose();
				
				
			}
		});
		
		mazeshell.open();
		
		
		
	}

	@Override
	public void start() {
		
		run();
		
	}



	@Override
	public void showDir(String []string) {
		// TODO method is irrelevant here ,check for an alternative
		
	}



	
	@Override
	public void showMsg(String s) {
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WORKING);
				messageBox.setMessage(s);
				messageBox.setText("Message");
				messageBox.open();
				
			}
		});
		

	}







	@Override
	public void showMaze(byte[] arr) {
		// TODO showMaze
		
	}



	@Override
	public void showCross(byte[] arr, String by, int i) {
		// TODO showCross
		
	}



	@Override
	public void showSolution(Solution<Position> s) {
		if(solveButton.isEnabled())
		solveButton.setEnabled(false);
		if(!mazeWin.isDisposed())
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
						if(mazeIterator.hasNext()&& !(mazeWin.isDisposed())){ 
							
							display.syncExec(new Runnable() {

								@Override
								public void run() {
									State<Position> state=mazeIterator.next();
									mazeWin.moveCharacter(state.getState());
									
								}
							});
							
							
					}
						else
						{
							if( task!=null)
							task.cancel();
							if( timer!=null)
							timer.cancel();
							}
					}
						
				}, 0, 80);

			}
		});

	}
		


               /**
                * when the shell is disposed
                * **/
	@Override
	public void close() {
		canExitAll=false; // set the exit safety, since we want to dispose the view and keep the model running, once the shell is disposed the presenter gets notified with the safety boolean 
		if(!shell.isDisposed())
		display.dispose();
		
	}

	


	@Override
	public Object getData(String string) {
		return notifications.get(string);
	}



	@Override
	public void showMazeIsReady(String s) {
	
		//does nothing here(GUI) (the update list function does its job )
	}



	@Override
	public String getViewType() {
		
		ViewType=this.getClass().getSimpleName();
		return ViewType;
		
	}



	public boolean isCanExitAll() {
		return canExitAll;
	}



	public void setCanExitAll(boolean canExitAll) {
		this.canExitAll = canExitAll;
	}



	@Override
	public void showSolved(String name) {
		
		// does nothing here (GUI)(the update list function does its job )
 	}
	

	public void showUpdatedList(String []elements){
		String[] temp= new String[elements.length];
		/*for (String s : elements){
			temp=s.split(" ");           // TODO FIND A NICE WAY TO USE THE SOLVED STATUS AND PRESENT IT TO THE USER
			s=temp[1];
		}*/
		for(int i=0;i<elements.length;i++)
			temp[i]=(elements[i].split(" "))[0];
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				l.setItems(temp);
				
			}
		});
		
	}
	
	
}
