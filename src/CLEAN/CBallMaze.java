package CLEAN;
/**
Program:   Assignment 2: Application – Ball Maze
Filename:  CBallMaze.java                              
@author:   © Tsering Khando Lama (18406499)                   
Course:    Bsc Hons Computing Year 1        
Module:    CSY1020 Problem Solving & Programming       
Tutor:     Gary Hill                                   
@version:  2.0 Incorporates Artificial Intelligence!  
Date:      12/07/18                                    
*/

import java.awt.*;     // access java.awt components 
import java.awt.event.*;   
import java.io.File;   // access java.io
import java.util.Random;  // access java.util.Random component
import javax.sound.sampled.*;   // access java.sound component
import javax.swing.*;          //access all the java.swing. components
import javax.swing.border.Border;  //access java.swing.border.Border

public class CBallMaze extends JFrame implements ActionListener
{
	private Clip audioCheck;   //Clip object created
	private int timetick=0;  // to update the timer of  side panel
	private JMenuBar jMToolbar;  //menubar created 
	private JMenu scenario,edit,controls,help;  //menu options
	private JMenuItem exit,topic,about;        //submenus
	private JPanel jPmazeScene, jPSideControls, jPBottomControls;  //the three main panels
	private JButton jMazeSand[][]=new JButton[16][13]; // 16*13 button initialised for the maze

	//panel components added
	private JButton jBtnAct,jBtnRun, jBtnReset,jbMoveUp,jbMoveDown,jbMoveLeft,jbMoveRight,jbOption1,jbOption2,jbOption3,jbOption4,jMap;
	private JTextField jOfield,jSfield,jDfield,jTimer1,jTimer2,jTimer3;
	private JSlider jSpeedSlider;
	private Timer jTGameTracker, jTBallFallTracker, runSimulator,enemyTime;  //Timers initialised

	//imageicons
	private ImageIcon sand= new ImageIcon("images/sand1.jpg");
	private ImageIcon ball =new ImageIcon("images/gball2.png");
	private ImageIcon goal = new ImageIcon("images/sandstone2.jpg");
	private ImageIcon whitespace= new ImageIcon("images/whites.jpg");
	private ImageIcon win= new ImageIcon("images/win.png");
	private ImageIcon pause= new ImageIcon("images/pause.png");
	private ImageIcon act=new ImageIcon("images/stepRight.png");
	private ImageIcon run=new ImageIcon("images/run.png");
	private ImageIcon reset=new ImageIcon("images/reset.png");
	private ImageIcon greenfoot = new ImageIcon("images/greenfoot.jpg");

	//ball position tracker
	private int ballPx;
	private int ballPy; 

	//boolean checkers
	private boolean optionIntermediate=false, optionAdvance=false;

	//advanced components
	private ImageIcon smilingEnemy = new ImageIcon("images/smiley5.png");
	private ImageIcon eatMushroom = new ImageIcon("images/mushroom.png");
	private boolean leftClicked= false , rightClicked=false;
	private int eatingPoints=1;
	private int counter = 0 , smileX, smileY, mushX, mushY; //tracking simley and mushroom
	private Container mazeBody= getContentPane();

	public CBallMaze()   // the constructor
	{
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(775,650);
		setTitle("C Ball Maze- Maze Ball Application");
		setIconImage(greenfoot.getImage());
		setLocationRelativeTo(null);
		setResizable(false);
		//calling the methods//
		menuToolbar();
		methodMazeScene();
		sidePanelMethod();
		panelBottomMethod();
		posBall(ballPx,ballPy);
		setVisible(true);
	}


	public static void main(String[] args)
	{ 
		new CBallMaze(); // constructor called
	}


	private void menuToolbar() //menubar method
	{
		jMToolbar = new JMenuBar();
		this.setJMenuBar(jMToolbar);
		//adding menus name
		scenario = new JMenu("Scenario");  
		jMToolbar.add(scenario);

		edit = new JMenu("Edit");
		jMToolbar.add(edit);
		controls = new JMenu("Controls");
		jMToolbar.add(controls);
		help= new JMenu("Help");
		jMToolbar.add(help);

		//adding sub-menus
		exit = new JMenuItem("Exit");
		scenario.add(exit);
		exit.addActionListener(this);

		topic = new JMenuItem("Help Topic");
		help.add(topic);
		topic.addActionListener(this);

		about = new JMenuItem("About");
		help.add(about);
		about.addActionListener(this);
	}


	//maze layout
	private void methodMazeScene()
	{
		jPmazeScene = new JPanel();
		jPmazeScene.setPreferredSize(new Dimension (570,570));
		jPmazeScene.setLayout(new GridBagLayout()); //using gridbag layout
		jPmazeScene.setBorder(BorderFactory.createEtchedBorder()); 
		mazeBody.add(jPmazeScene, BorderLayout.LINE_START);

		// making maze//
		GridBagConstraints g = new GridBagConstraints(); //initialising gridbag constraints
		int x,y; // tracking the maze position
		for(x=0;x<16;x++)
		{
			for(y=0;y<13;y++) 
			{
				g.gridx= x;
				g.gridy=y;

				jMazeSand[x][y]=new JButton(sand);

				if(x==15 && y==0)   //initial position of the golden ball
				{
					jMazeSand[x][y]=new JButton(ball);
					ballPx=x;
					ballPy=y;
				}

				if(x==0 && y==12) 
				{
					jMazeSand[x][y]=new JButton(goal);
				}

				if((x!=1 && x!=5 && x!=9 && y==1) || (x!=1 && x!=5 && x!=9 && y==2) ) 
				{
					jMazeSand[x][y]=new JButton(whitespace);
				}

				if((x!=2 && x!=6 &&x!=11 && y==4) || (x!=2 && x!=6 &&x!=11 && y==5))
				{
					jMazeSand[x][y]=new JButton(whitespace);
				}

				if((x!=1 && x!=5 && x!=12 && y==7) ||(x!=1 && x!=5 && x!=12 && y==8))
				{
					jMazeSand[x][y]=new JButton(whitespace);
				}


				if((x!=2 && x!=6 && y==10)|| (x!=2 && x!=6 && y==11))
				{
					jMazeSand[x][y]=new JButton(whitespace);
				}

				jMazeSand[x][y].setContentAreaFilled(false);
				jMazeSand[x][y].setBorder(null);

				jPmazeScene.add(jMazeSand[x][y],g);
			}
		}	
	}


	//right panel//
	public void sidePanelMethod()
	{
		jPSideControls = new JPanel();
		jPSideControls.setPreferredSize(new Dimension (205,570));
		//compound border created 
		Border compound =BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0),BorderFactory.createEtchedBorder()); 
		jPSideControls.setBorder(compound);
		mazeBody.add(jPSideControls, BorderLayout.LINE_END);
		jPSideControls.setLayout(null);  //layout is set null to use the setbounds

		//adding components to the right panel.

		JLabel jOpt =new JLabel("Option:");
		jPSideControls.add(jOpt);
		jOpt.setBounds(50,5,50,50);
		//shows option selected in the textfield
		jOfield=new JTextField(10); 
		jPSideControls.add(jOfield);
		jOfield.setText("          1");
		jOfield.setEditable(false);
		jOfield.setBounds(100,20,80,20);


		JLabel jSq =new JLabel("Square:");
		jPSideControls.add(jSq);
		jSq.setBounds(50,40,50,50);
		//shows grid of the ball in the textfield
		jSfield=new JTextField(10);
		jPSideControls.add(jSfield);
		jSfield.setEditable(false);
		jSfield.setBounds(100,55,80,20);


		JLabel jDir =new JLabel("Direction:");
		jPSideControls.add(jDir);
		jDir.setBounds(40,75,80,50);
		//shows grid of the ball in the textfield
		jDfield=new JTextField(10);
		jPSideControls.add(jDfield);
		jDfield.setText("East");
		jDfield.setEditable(false);
		jDfield.setBounds(100,90,80,20);

		//for the timer field
		JLabel jDig=new JLabel("DIGITAL TIMER");
		jPSideControls.add(jDig);
		jDig.setBounds(70,110,100,50);

		//adding textfields to show timer	
		jTimer1= new JTextField(3);
		jPSideControls.add(jTimer1);
		jTimer1.setBounds(40,155,30,20);
		jTimer1.setText("00");
		jTimer1.setEditable(false);
		jTimer1.setBackground(Color.black);
		jTimer1.setForeground(Color.white);

		JLabel l=new JLabel(":");
		jPSideControls.add(l);
		l.setBounds(80,155,20,20);

		jTimer2= new JTextField(3);
		jPSideControls.add(jTimer2);
		jTimer2.setBounds(90,155,30,20);
		jTimer2.setText("00");
		jTimer2.setEditable(false);
		jTimer2.setBackground(Color.black);
		jTimer2.setForeground(Color.white);

		JLabel l2=new JLabel(":");
		jPSideControls.add(l2);
		l2.setBounds(130,155,20,20);

		jTimer3= new JTextField(3);
		jPSideControls.add(jTimer3);
		jTimer3.setBounds(140,155,30,20);
		jTimer3.setText("00");
		jTimer3.setEditable(false);
		jTimer3.setBackground(Color.black);
		jTimer3.setForeground(Color.white);

		//Timer with speed of 1 second
		jTGameTracker = new Timer(1000, new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				gameTimeTracker();
			}
		});

		//the controls buttons are added
		jbMoveUp = new  JButton("^");
		jbMoveUp.addActionListener(this);

		jbMoveDown = new  JButton("v");
		jbMoveDown.addActionListener(this);

		jbMoveLeft = new  JButton("<");
		jbMoveLeft.addActionListener(this);

		jbMoveRight = new  JButton(">");
		jbMoveRight.addActionListener(this);

		//buttons for the control decoration
		JButton eB1,eB2,eB3,eB4,eB5;
		eB1= new JButton();
		eB1.setEnabled(false);
		eB2= new JButton();
		eB2.setEnabled(false);
		eB3= new JButton();
		eB3.setEnabled(false);
		eB4= new JButton();
		eB4.setEnabled(false);
		eB5= new JButton();
		eB5.setEnabled(false);

		//adding movement buttons
		jPSideControls.add(jbMoveUp);
		jbMoveUp.setBackground(Color.WHITE);
		jbMoveUp.setBorder(BorderFactory.createRaisedBevelBorder());
		jbMoveUp.setBounds(90,200,40,30);

		jPSideControls.add(jbMoveDown);
		jbMoveDown.setBackground(Color.WHITE);
		jbMoveDown.setBorder(BorderFactory.createRaisedBevelBorder());
		jbMoveDown.setBounds(90,260,40,30);

		jPSideControls.add(jbMoveLeft);
		jbMoveLeft.setBackground(Color.WHITE);
		jbMoveLeft.setBorder(BorderFactory.createRaisedBevelBorder());
		jbMoveLeft.setBounds(50,230,40,30);

		jPSideControls.add(jbMoveRight);
		jbMoveRight.setBackground(Color.WHITE);
		jbMoveRight.setBorder(BorderFactory.createRaisedBevelBorder());
		jbMoveRight.setBounds(130,230,40,30);

		//adding non usable buttons
		jPSideControls.add(eB1);
		eB1.setBounds(50,200,40,30);

		jPSideControls.add(eB2);
		eB2.setBounds(130,200,40,30);

		jPSideControls.add(eB3);
		eB3.setBounds(90,230,40,30);

		jPSideControls.add(eB4);
		eB4.setBounds(50,260,40,30);

		jPSideControls.add(eB5);
		eB5.setBounds(130,260,40,30);


		//Options Button added
		jbOption1 = new  JButton("Option1");
		jbOption2 = new  JButton("Option2");
		jbOption3 = new  JButton("Option3");
		jbOption4 = new  JButton("Exit");

		jPSideControls.add(jbOption1 );
		jbOption1.setBounds(30,320,80,30);
		jbOption1.setEnabled(false);
		jbOption1.setBackground(Color.WHITE);
		jbOption1.addActionListener(this);

		jPSideControls.add(jbOption2 );
		jbOption2.setBackground(Color.WHITE);
		jbOption2.setBounds(120,320,80,30);
		jbOption2.addActionListener(this);

		jPSideControls.add(jbOption3 );
		jbOption3.setBounds(30,360,80,30);
		jbOption3.setBackground(Color.WHITE);
		jbOption3.addActionListener(this);

		jPSideControls.add(jbOption4 );
		jbOption4.setBounds(120,360,80,30);
		jbOption4.setBackground(Color.WHITE);
		jbOption4.addActionListener(this);

		//adding compass image 
		ImageIcon map = new ImageIcon("images/east.jpg");
		jMap = new JButton(map);
		jPSideControls.add(jMap);
		jMap.setBounds(60,400, 100, 100);
		jMap.setBorderPainted(false);
		jMap.setContentAreaFilled(false);

	}

	//bottom panel 
	private void panelBottomMethod() 
	{
		jPBottomControls = new JPanel();
		jPBottomControls.setPreferredSize(new Dimension (775,80));
		jPBottomControls.setBorder(BorderFactory.createEtchedBorder());
		jPBottomControls.setLayout(new GridBagLayout());
		mazeBody.add(jPBottomControls, BorderLayout.PAGE_END);

		//components
		jSpeedSlider = new JSlider(JSlider.HORIZONTAL, 1,5,1);
		jSpeedSlider.setPaintTicks(true);
		jSpeedSlider.setMajorTickSpacing(1);
		GridBagConstraints gc = new GridBagConstraints();

		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.anchor=GridBagConstraints.FIRST_LINE_END;
		gc.insets=new Insets(20, 0, 0, 0);
		jBtnAct =new JButton("Act");
		jBtnAct.setIcon(act);
		jPBottomControls.add(jBtnAct,gc);
		jBtnAct.addActionListener(this);


		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.gridx = 2;
		gc.gridy = 0;
		gc.anchor=GridBagConstraints.FIRST_LINE_END;
		gc.insets=new Insets(20, 0, 0, 0);
		jBtnRun=new JButton("Run");
		jBtnRun.setIcon(run);
		jPBottomControls.add(jBtnRun,gc);
		jBtnRun.addActionListener(this);

		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.gridx = 3;
		gc.gridy = 0;
		gc.anchor=GridBagConstraints.FIRST_LINE_END;
		gc.insets=new Insets(20, 0, 0, 50);
		jBtnReset=new JButton("Reset");
		jBtnReset.setIcon(reset);
		jPBottomControls.add(jBtnReset,gc);
		jBtnReset.addActionListener(this);

		gc.weightx = 2;
		gc.weighty = 0.1;
		gc.gridx = 4;
		gc.gridy = 0;
		gc.anchor=GridBagConstraints.FIRST_LINE_END;
		gc.insets=new Insets(20, 0, 0, 0);
		jPBottomControls.add(new JLabel("Speed:"),gc);

		gc.weightx = 2;
		gc.weighty = 0.1;
		gc.gridx = 5;
		gc.gridy = 0;
		gc.anchor=GridBagConstraints.FIRST_LINE_START;
		gc.insets=new Insets(20, 0, 0, 0);
		jPBottomControls.add(jSpeedSlider,gc);

	}

	// timer method created
	private void gameTimeTracker() 
	{
		jTimer1.setText(" "+Integer.toString((timetick/60)/60));
		jTimer2.setText(" "+Integer.toString((timetick/60)%60));
		jTimer3.setText(" "+Integer.toString(timetick%60));
		timetick=timetick+ 1;
	}


	// method to display the co-ordinates of the golden ball
	private void posBall(int x, int y) 
	{
		jSfield.setText(x +" , "+y);
	}


	//movement possibility checkers
	private boolean leftPossible()
	{//checking if there is sand on the left of the ball
		if(ballPx!=0 && jMazeSand[ballPx-1][ballPy].getIcon()==sand || jMazeSand[ballPx-1][ballPy].getIcon()==goal ) 
			return true;
		return false;
	}


	private boolean rightPossible()
	{//checking if there is sand on the right of the ball
		if(ballPx!=15 && jMazeSand[ballPx+1][ballPy].getIcon()==sand) 
			return true;
		return false;
	}


	private boolean upPossible()
	{//checking if there is sand above the ball
		if(ballPy!=0 && jMazeSand[ballPx][ballPy-1].getIcon()==sand) 
			return true;
		return false;
	}



	private boolean downPossible()
	{//checking if there is sand below the ball
		if(ballPy!=12 && jMazeSand[ballPx][ballPy+1].getIcon()==sand) 
			return true;
		return false;
	}


	private boolean finalGoal() //checking if goal reached
	{
		if(ballPx==0 && ballPy==12) 
		{
			jMazeSand[0][12].setIcon(win);
			jTGameTracker.stop();
			if(optionAdvance==true)
				jMazeSand[8][6].setIcon(new ImageIcon("images/congrats.png"));
			int chooseOption= JOptionPane.showConfirmDialog(null, "WELL DONE \n Your Score is " +eatingPoints +" \n PlAY AGAIN?","Confirm",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(chooseOption==JOptionPane.YES_OPTION)
				disposeMyMaze();
			else
				System.exit(0);
			runSimulator.stop();  //needed to stop the run button effect
			return true;
		}
		return false;
	}	


	//making movement methods
	private void leftChange()  //shifts left
	{
		jMazeSand[ballPx][ballPy].setIcon(sand);
		jMazeSand[ballPx-1][ballPy].setIcon(ball);
		ballPx=ballPx-1;
		jMap.setIcon(new ImageIcon("images/west.jpg"));
		jDfield.setText("West");
		posBall(ballPx,ballPy);
		finalGoal();
	}



	private void rightChange() //shifts right
	{
		jMazeSand[ballPx+1][ballPy].setIcon(ball);
		jMazeSand[ballPx][ballPy].setIcon(sand);
		ballPx=ballPx+1;

		jMap.setIcon(new ImageIcon("images/east.jpg"));
		jDfield.setText("East");
		posBall(ballPx,ballPy);
	}


	private void upChange() //moves up
	{
		jMazeSand[ballPx][ballPy-1].setIcon(ball);
		jMazeSand[ballPx][ballPy].setIcon(sand);
		ballPy=ballPy-1;

		jMap.setIcon(new ImageIcon("images/north.jpg"));
		jDfield.setText("North");
		posBall(ballPx,ballPy);
	}


	private void downChange() //moves down
	{
		jMazeSand[ballPx][ballPy+1].setIcon(ball);
		jMazeSand[ballPx][ballPy].setIcon(sand);
		ballPy=ballPy+1;

		jMap.setIcon(new ImageIcon("images/south.jpg"));
		jDfield.setText("South");
		posBall(ballPx,ballPy);
	}



	//freefalling method
	private void ballDropFree()
	{
		jTBallFallTracker= new Timer(300, new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if(optionIntermediate==true) 
				{
					if(jMazeSand[ballPx][ballPy+1].getIcon()==sand)
					{
						ballDropSound();
						jDfield.setText("South");
						jMazeSand[ballPx][ballPy+1].setIcon(ball);
						jMazeSand[ballPx][ballPy].setIcon(sand);
						ballPy=ballPy+1;
						posBall(ballPx, ballPy);
						eatMushroom();
						avoidEnemy();
					}
				}

			}
		});

		jTBallFallTracker.start();
	}


	//run simulation method
	private void runSimulator()
	{

		runSimulator = new Timer(600,new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				if(leftPossible())
				{	
					optionIntermediate=true;

					leftChange();

					if(downPossible())
						ballDropFree();
					posBall(ballPx,ballPy);

				}
			}
		});
		runSimulator.start();
	}


	//playing sound
	private void ballDropSound() 
	{
		try 
		{
			AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File("sounds/cartoon008.wav"));
			audioCheck = AudioSystem.getClip();
			audioCheck.open(audioInput);
			audioCheck.start();
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}


	// act button function
	private void actButtonMovesStep() 
	{
		if(leftPossible())
		{
			leftChange();
			optionIntermediate=true;
			if(downPossible() )
				ballDropFree();
			posBall(ballPx,ballPy);
		}
	}


	//help and about menu items
	private void aboutGreenfoot()
	{
		JOptionPane.showInternalMessageDialog(null, "Program:   Assignment 2: Application – Ball Maze\r\n" + 
				"Filename:  CBallMaze.java                              \r\n" + 
				"@author:   © Tsering Khando Lama (18406499)                   \r\n" + 
				"Course:    Bsc Hons Computing Year 1        \r\n" + 
				"Module:    CSY1020 Problem Solving & Programming       \r\n" + 
				"Tutor:     Gary Hill                                   \r\n" + 
				"@version:  2.0 Incorporates Artificial Intelligence!  \r\n" + 
				"Date:      12/07/2018 ");
	}

	private void helpTopic()
	{
		JOptionPane.showInternalMessageDialog(null, "To get a demo of the game, press the Run Button.\n For controls, user mouse to click on the control buttons\" '<', '>' ,'^','v'  \".");
	}


	//randomly appearing enemy and mushroom
	private void randomAppear()
	{
		if(optionAdvance==true)
		{
			enemyTime= new Timer(1000, new ActionListener()
			{
				public void actionPerformed(ActionEvent eventTimer) 
				{
					if(counter<10)
					{
						smileX = (int) (Math.ceil(Math.random()*10));
						smileY = (int) (Math.ceil(Math.random()*11+1));

						mushX = (int) (Math.random()*14);
						mushY = (int) (Math.random()*11);
						if(jMazeSand[smileX][smileY].getIcon()==sand)
							jMazeSand[smileX][smileY].setIcon(smilingEnemy);

						if(jMazeSand[mushX][mushY].getIcon()==sand)
							jMazeSand[mushX][mushY].setIcon(eatMushroom);
						counter++;
					}
				}
			});
			enemyTime.start();
		}
	}


	//dispose method 
	private void disposeMyMaze()
	{
		dispose();
		CBallMaze.main(null);
	}


	//when enemy touches
	private void avoidEnemy()
	{		
		if( jMazeSand[ballPx-1][12].getIcon()==smilingEnemy 
				|| jMazeSand[ballPx-1][ballPy].getIcon()==smilingEnemy
				&& jMazeSand[ballPx][ballPy+1].getIcon()==whitespace)
		{
			if(leftClicked)  //the boolean button click checker
				dodgeEnemy();
			else
				jMazeSand[ballPx][ballPy].setIcon(ball);
		}

		if(jMazeSand[ballPx+1][12].getIcon()==smilingEnemy
				|| jMazeSand[ballPx+1][ballPy].getIcon()==smilingEnemy
				&& jMazeSand[ballPx][ballPy+1].getIcon()==whitespace )
		{
			if(rightClicked)
				dodgeEnemy();

			else
				jMazeSand[ballPx][ballPy].setIcon(ball);
		}

		if(jMazeSand[ballPx][ballPy+1].getIcon()==smilingEnemy )
		{
			dodgeEnemy();
		}

	}


	private void eatMushroom()
	{//checks the mushroom object and replaces it with ball image
		if(jMazeSand[ballPx-1][ballPy].getIcon()==eatMushroom &&
				jMazeSand[ballPx][ballPy+1].getIcon()==whitespace ||
				jMazeSand[ballPx-1][12].getIcon()==eatMushroom)
		{
			jMazeSand[ballPx-1][ballPy].setIcon(ball);
			jMazeSand[ballPx][ballPy].setIcon(sand);

			ballPx--;
			eatingPoints++;// adds a score if the mushroom is eaten
		}

		else if(jMazeSand[ballPx+1][ballPy].getIcon()==eatMushroom && 
				jMazeSand[ballPx][ballPy+1].getIcon()==whitespace ||
				jMazeSand[ballPx-1][12].getIcon()==eatMushroom)
		{
			jMazeSand[ballPx][ballPy].setIcon(sand);
			jMazeSand[ballPx+1][ballPy].setIcon(ball);
			ballPx++;
			eatingPoints++;
		}

		else if(jMazeSand[ballPx][ballPy+1].getIcon()==eatMushroom)
		{
			jMazeSand[ballPx][ballPy].setIcon(sand);
			jMazeSand[ballPx][ballPy+1].setIcon(ball);
			ballPy++;
			eatingPoints++;  
		}
	}


	//enemy collide checker
	private void dodgeEnemy()
	{
		int chooseOption= JOptionPane.showConfirmDialog(null, "YOU LOST! PlAY AGAIN?","Confirm",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(chooseOption==JOptionPane.YES_OPTION)
			disposeMyMaze();
		else
			System.exit(0);
	}


	//all the events
	public void actionPerformed(ActionEvent eve) 
	{
		//movement buttons
		if(eve.getSource()==jbMoveLeft)  //left button
		{
			leftClicked=true; //for advanced option
			if(leftPossible())
				leftChange();
			ballDropFree();

			avoidEnemy(); 
			eatMushroom();
		}


		if(eve.getSource()==jbMoveRight)//right button
		{
			rightClicked=true;//for advanced option
			if(rightPossible())
				rightChange();
			avoidEnemy();
			eatMushroom();
		}

		if(eve.getSource()==jbMoveUp)//up button
		{
			if(upPossible())
				upChange();

		}

		if(eve.getSource()==jbMoveDown)//down button
		{
			if(downPossible())
				downChange();


		}

		//options buttons

		if(eve.getSource()==jbOption1) // option 1 button
		{
			optionIntermediate=false;
			jbOption1.setEnabled(false);
			jbOption2.setEnabled(true);
			jbOption3.setEnabled(true);
			jOfield.setText("          1");
		}

		if(eve.getSource()==jbOption2) // option 2 button
		{
			optionIntermediate=true;
			jbOption1.setEnabled(true);
			jbOption2.setEnabled(false);
			jbOption3.setEnabled(true);
			jOfield.setText("          2");

		}

		if(eve.getSource()==jbOption3) // option 3 button
		{
			optionAdvance=true;
			optionIntermediate=true;
			jbOption1.setEnabled(true);
			jbOption2.setEnabled(true);
			jbOption3.setEnabled(false);
			jOfield.setText("          3");
			randomAppear();



		}

		if(eve.getSource()==jbOption4 || eve.getSource()==exit) // also adding the menu item "exit"
		{
			System.exit(0);
		}


		//bottom panel buttons

		if(eve.getSource()== jBtnAct)
		{
			actButtonMovesStep();
		}


		//checking run button activation
		if(eve.getActionCommand()=="Run")
		{
			jTGameTracker.start();
			runSimulator();
			jBtnRun.setIcon(pause);
			jBtnRun.setText("Pause");
			timetick=0;
			jTGameTracker.start();
		}

		if(eve.getActionCommand()=="Pause")
		{
			jBtnRun.setIcon(run);
			jBtnRun.setText("Run");
			jTGameTracker.stop();
			runSimulator.stop();
		}



		//reset button
		if(eve.getSource()== jBtnReset)
		{
			timetick=0;
			disposeMyMaze();
			runSimulator.stop();
		}

		if(eve.getSource()==about)
		{
			aboutGreenfoot();
		}

		if(eve.getSource()==topic)
		{
			helpTopic();
		}

	}
}
