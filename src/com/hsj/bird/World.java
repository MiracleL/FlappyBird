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
	
	
	public static int model=1;   //0��ʾ��ͳģʽ��Ĭ��Ϊ0��1��ʾQ-learningģʽ
	public static int music_count=0;  //�������ñ������ֵĿ����,0��ʾ��
	public static int mysql_count=0;  //�������ʱһ��flag��������д��mysql���ݿ�
	
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
		//��ѭ��, ʱ������ 1/60 ��
		while(true){
			
			bird.setModel(model);
			if (model==0)
			{
				if(start && !gameOver){
					bird.step();
					column1.step();
					column2.step();
					
					//����Ƿ�ͨ��������
					if(bird.pass(column1, column2)){
						score++;
					}
					if(bird.hit(column1, column2, ground)){
						start = false;
						gameOver = true;
					}
				}
			}
			else  //q-learningģʽ
			{
				if(start && !gameOver){
					//bird.qLearning(column1, column2);
					bird.step();
					if(first==0)  //��һ�ξͿ�ʼ�߳�
					{
						learning.start();
						first=1;
					}
					
					column1.step();
					column2.step();
					//����Ƿ�ͨ��������
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
		//����ݴ���
//		Graphics2D g2 = (Graphics2D)g;
//		RenderingHints qualityHints = new RenderingHints(
//				RenderingHints.KEY_ANTIALIASING,
//				RenderingHints.VALUE_ANTIALIAS_ON);
//		qualityHints.put(RenderingHints.KEY_RENDERING,
//				RenderingHints.VALUE_RENDER_QUALITY);
//		g2.setRenderingHints(qualityHints);
		//���Ʊ���
		g.drawImage(background, 0, 0, null);
		//��������
		column1.paint(g);
		column2.paint(g); 
		//���Ƶ���
		ground.paint(g);
		//���Ʒ���
		Font font = new Font(Font.MONOSPACED, Font.BOLD, 45);
		g.setFont(font);
		g.setColor(Color.white);
		g.drawString(score+"", 30, 50);
		//����С��
		bird.paint(g);
		//���ƽ���״̬
		if(gameOver){
			//g.drawString("Game Over!", 70 , 190);
			try {                //�����շ��������ݿ���
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
		
		JFrame frame = new JFrame("����С��");   //һ��JFrame
		JMenuBar menuBar=new JMenuBar(); //�����˵�������.
		frame.setJMenuBar(menuBar); //���˵���������ӵ����ڵĲ˵�����.
		
		class ItemListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				JMenuItem menuItem=(JMenuItem)e.getSource();//��ô����˴��¼��Ĳ˵���.
				if (menuItem.getText().equals("��ͳģʽ"))
				{
					model=0;
					
				}
				else if(menuItem.getText().equals("Q-Learningģʽ"))
				{
					model=1;
				}
				else if(menuItem.getText().equals("���ر�������"))
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
				else if(menuItem.getText().equals("�鿴��ʷ����"))
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
				else if(menuItem.getText().equals("��ȡ��Ϸ����"))
				{
				
					JFrame helpFrame=new JFrame("��ȡ��Ϸ����");
					Help help=new Help();
					helpFrame.add(help);
					helpFrame.setSize(300, 500);
					helpFrame.setLocationRelativeTo(null);
					helpFrame.setVisible(true);	
				}
			}
		}
		 
		//�����˵���������.
		JMenu menu1=new JMenu("�л�ģʽ"); 
		JMenu menu2=new JMenu("��������");
		menuBar.add(menu1); //���˵�������ӵ��˵���������.
		menuBar.add(menu2);
		 
		//menu1���Ӳ˵�
		JMenuItem menuItem1=new JMenuItem("��ͳģʽ");
		menuItem1.addActionListener(new ItemListener());
		JMenuItem menuItem2=new JMenuItem("Q-Learningģʽ");
		menuItem2.addActionListener(new ItemListener());
		menu1.add(menuItem1);//���Ӳ˵���ӵ����˵���.
		menu1.add(menuItem2);//���Ӳ˵���ӵ����˵���.
		
		//menu2���Ӳ˵�
		JMenuItem menuItem3=new JMenuItem("���ر�������");
		JMenuItem menuItem4=new JMenuItem("�鿴��ʷ����");
		JMenuItem menuItem5=new JMenuItem("��ȡ��Ϸ����");
		
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
