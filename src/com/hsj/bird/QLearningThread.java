package com.hsj.bird;

import java.util.Random;

public class QLearningThread extends Thread {
	
	Column column1;
	Column column2;
	Bird bird;
	QLearning qLearn;
	
	public static int time=0;  //训练的次数
	
	public QLearningThread(Column column1,Column column2,Bird bird)
	{
		this.column1=column1;
		this.column2=column2;
		this.bird=bird;
		qLearn=new QLearning ();
		
	}
	
	
	//重写run方法
    @Override
    public void run() {
        super.run();
        
        int flag=-1;
        while(true)
        {
        	
        	if(bird.getModel()==1)   //q_learning模式
        	{
        		System.out.println(" ");
	        	time=1001;
	        	int which=bird.chooseCurrentColumn(column1, column2);
	        	if(time<1000)  //训练的次数
	        	{
	        		if(which==1)
	        		{
	        			try {
							flag=qLearn.training(bird.x, bird.y, column1.x, column1.y, bird);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	        		}
	        		else
	        		{
	        			try {
							flag=qLearn.training(bird.x, bird.y, column2.x, column2.y, bird);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	        		}
	        	}
	        	else    //训练完成之后，就开始玩游戏了
	        	{
	        		if(time==1000)
	        		{
	        			qLearn.save();
	        			System.out.println("训练完成");
	        		}
	        	
	        		if(which==1)
	        		{
	        			flag=qLearn.getOptimist(bird.x, bird.y, column1.x, column1.y);
	        		}
	        		else
	        		{
	        			flag=qLearn.getOptimist(bird.x, bird.y, column2.x, column2.y);
	        		}
	        	}
	        
	        	if(flag==0)   //表示小鸟向上飞
	        	{
	        		System.out.println("上");
	        		bird.flappy();
	        	}
        	}
//        	
        	try {
				Thread.sleep(50);   //睡眠100秒
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}
