package com.hsj.bird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;



public class World extends JPanel {
	
	
	public static int model=1;   //0表示传统模式，默认为0，1表示Q-learning模式
	public static int music_count=0;  //用于设置背景音乐的开与关,0表示开
	public static int mysql_count=0;  //这个变量时一个flag，避免多次写入mysql数据库
	
	Column column1;
	Column column2;
	Bird bird;
	Ground ground;
	BufferedImage background;
	BufferedImage gameoverImg;
	BufferedImage startImg;
	
	boolean start;
	int score;
	boolean gameOver;
	
	int index = 0;
	
	public static Mysql mysql;
	public static int first=0;
	
	
	QLearningThread learning;

	public World() throws IOException {
		background = ImageIO.read(getClass().getResource("bg.png"));
		gameoverImg = ImageIO.read(getClass().getResource("gameover.png"));
		startImg = ImageIO.read(getClass().getResource("start.png"));
		
		start();
	}
	public void start(){
		try {
			start = false;
			gameOver = false;
			bird = new Bird();
			ground = new Ground(); 
			column1 = new Column(1);
			column2 = new Column(2);
			learning=new QLearningThread(column1,column2,bird);
			first=0;
			score = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void action() throws Exception{		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (gameOver) {
					start();
					mysql_count=0;
					return;
				}
				start = true;
				bird.flappy();
			}
		});
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_SPACE){
					if(gameOver){
						start();	
						mysql_count=0;
						return;
					}
					start = true;
					bird.flappy();
				}
			}
		});
		requestFocus();
		//主循环, 时间间隔是 1/60 秒
		while(true){
			
			bird.setModel(model);
			if (model==0)
			{
				if(start && !gameOver){
					bird.step();
					column1.step();
					column2.step();
					
					//检查是否通过柱子了
					if(bird.pass(column1, column2)){
						score++;
					}
					if(bird.hit(column1, column2, ground)){
						start = false;
						gameOver = true;
					}
				}
			}
			else  //q-learning模式
			{
				if(start && !gameOver){
					//bird.qLearning(column1, column2);
					bird.step();
					if(first==0)  //第一次就开始线程
					{
						learning.start();
						first=1;
					}
					
					column1.step();
					column2.step();
					//检查是否通过柱子了
					if(bird.pass(column1, column2)){
						score++;
					}
					if(bird.hit(column1, column2, ground)){
						start = false;
						gameOver = true;
					}
				}
			}
			if(! gameOver) bird.fly();
			ground.step();
			repaint();
			Thread.sleep(1000/70);
		}			
	}
	@Override
	public void paint(Graphics g) {
		//抗锯齿代码
//		Graphics2D g2 = (Graphics2D)g;
//		RenderingHints qualityHints = new RenderingHints(
//				RenderingHints.KEY_ANTIALIASING,
//				RenderingHints.VALUE_ANTIALIAS_ON);
//		qualityHints.put(RenderingHints.KEY_RENDERING,
//				RenderingHints.VALUE_RENDER_QUALITY);
//		g2.setRenderingHints(qualityHints);
		//绘制背景
		g.drawImage(background, 0, 0, null);
		//绘制柱子
		column1.paint(g);
		column2.paint(g); 
		//绘制地面
		ground.paint(g);
		//绘制分数
		Font font = new Font(Font.MONOSPACED, Font.BOLD, 45);
		g.setFont(font);
		g.setColor(Color.white);
		g.drawString(score+"", 30, 50);
		//绘制小鸟
		bird.paint(g);
		//绘制结束状态
		if(gameOver){
			//g.drawString("Game Over!", 70 , 190);
			try {                //加最终分数到数据库中
				if(mysql_count==0)
				{
					mysql.addOneUser(score);
					mysql_count=1;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			g.drawImage(gameoverImg, 0, 0, null);
			return;
		}
		if(! start){
			//g.drawString("Start >>>", bird.x+35, bird.y);
			g.drawImage(startImg, 0, 0, null);
		}
	}
	
	
	
	
	
	
	public static void main(String[] args) throws Exception {
		
		JFrame frame = new JFrame("飞扬小鸟");   //一个JFrame
		JMenuBar menuBar=new JMenuBar(); //创建菜单栏对象.
		frame.setJMenuBar(menuBar); //将菜单栏对象添加到窗口的菜单栏中.
		
		class ItemListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				JMenuItem menuItem=(JMenuItem)e.getSource();//获得触发此次事件的菜单项.
				if (menuItem.getText().equals("传统模式"))
				{
					model=0;
					
				}
				else if(menuItem.getText().equals("Q-Learning模式"))
				{
					model=1;
				}
				else if(menuItem.getText().equals("开关背景音乐"))
				{
					BackgroundMusic back=BackgroundMusic.getInstance();
					if(music_count==0)
					{
						music_count=1;
						back.start();
					}
					else if(music_count==1)
					{
						music_count=0;
						BackgroundMusic.init();
						back.stop();
						
					}
				}
				else if(menuItem.getText().equals("查看历史积分"))
				{
					new Thread() {
						
						@Override
						public void run()
						{
							ArrayList<Integer> array = null;
							try {
								array=mysql.findAll();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							SearchHistoryScore Score=new SearchHistoryScore(array);
							System.out.println("22");
						}
					
					}.start();
				}
				else if(menuItem.getText().equals("获取游戏帮助"))
				{
				
					JFrame helpFrame=new JFrame("获取游戏帮助");
					Help help=new Help();
					helpFrame.add(help);
					helpFrame.setSize(300, 500);
					helpFrame.setLocationRelativeTo(null);
					helpFrame.setVisible(true);	
				}
			}
		}
		 
		//创建菜单对象及名称.
		JMenu menu1=new JMenu("切换模式"); 
		JMenu menu2=new JMenu("辅助功能");
		menuBar.add(menu1); //将菜单对象添加到菜单栏对象中.
		menuBar.add(menu2);
		 
		//menu1的子菜单
		JMenuItem menuItem1=new JMenuItem("传统模式");
		menuItem1.addActionListener(new ItemListener());
		JMenuItem menuItem2=new JMenuItem("Q-Learning模式");
		menuItem2.addActionListener(new ItemListener());
		menu1.add(menuItem1);//将子菜单添加到主菜单中.
		menu1.add(menuItem2);//将子菜单添加到主菜单中.
		
		//menu2的子菜单
		JMenuItem menuItem3=new JMenuItem("开关背景音乐");
		JMenuItem menuItem4=new JMenuItem("查看历史积分");
		JMenuItem menuItem5=new JMenuItem("获取游戏帮助");
		
		menuItem3.addActionListener(new ItemListener());
		menuItem4.addActionListener(new ItemListener());
		menuItem5.addActionListener(new ItemListener());
		
		menu2.add(menuItem3);
		menu2.add(menuItem4);
		menu2.add(menuItem5);
		
		
		
		World world = new World();  
		mysql=new Mysql();
		frame.add(world);
		frame.setSize(432+8, 644+30);
		//frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		world.action();
		
	}	
}
