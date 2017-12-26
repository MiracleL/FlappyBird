package com.hsj.bird;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

//q-learning算法,学习率为0.1

public class QLearning {
	
	int[][] q_table = new int[300][144];     //只选取有用的部分，其余部分设置为0
	
	int gap = 144;   //上下柱子的间距
	int size = 40;   //鸟的范围, 鸟的范围是一个正方形区域, 中心点是x,y 
	int columnWidth=78;  //柱子的宽度
	
	int distance=20;    //每次上升或者下降的距离
	
	int bird_x,bird_y;   //坐标值
	Random rand;
	
	double learningRate=0.2;  //学习率
	
	public static int time=0;  //训练的次数
	
	
	public QLearning()
	{
		try {
			read();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 初始化q_table的值为0
//		for(int i=0;i<300;i++)
//		{
//			for(int j=0;j<144;j++)
//			{
//				//越往中间，值越大
//				double rate=Math.abs(72-j)/150.0;
//				if(j<=40||j>=105)   //边界，稍微设置的大了一点
//				{
//					q_table[i][j]=0;
//				}
//				else
//				{
//					//q_table[i][j]=(int)(10000*(1-rate));   //越靠近柱子空隙的中间奖励越高
//					q_table[i][j]=(int)(100*(Math.abs(72-j)+1));
//				}
//			}
//		}
//		save();
		rand=new Random();
	}
	
	
	//返回reward值，供训练时用,越靠近柱子空隙的奖励越高
	public int getReward(int birdX,int birdY,int columnX,int columnY)
	{
		
		int reward=0;   //奖励Z
		double rate=Math.abs(columnY-birdY)/1000.0;  
		if (birdY>=(columnY-gap/2+size/2)&&birdY<=(columnY+gap/2-size/2))
		{
			double value=10000*(1-rate);   //越靠近柱子空隙的中间奖励越高
			reward=(int)(value);
		}
		return reward;
	}
	
	//从excel文件中读取q_table表
	 public void read() throws FileNotFoundException, IOException
	 {
	    	HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream("D:\\\\qtable.xls"));
	    	HSSFSheet sheet = workbook.getSheet("用户表一");
	    	HSSFRow row;
	    	HSSFCell cell;
	    	for(int i=0;i<300;i++)
	    	{
	    		row=sheet.getRow(i);
	    		for(int j=0;j<144;j++)
	    		{
	    			
	    			cell = row.getCell((short)j);
	        		q_table[i][j]=(int)(cell.getNumericCellValue());
	    			
	    		}
	    	}
	    	
	 }
	
	//保存q_table为excel文件
	public void save()
	{
		 HSSFWorkbook workbook = new HSSFWorkbook();
	        //第二部，在workbook中创建一个sheet对应excel中的sheet
	     HSSFSheet sheet = workbook.createSheet("用户表一");
	     
	     HSSFCell cell;
         HSSFRow row;
         for(int i=0;i<300;i++)
         {
        	row = sheet.createRow(i);
        	for (int j=0;j<144;j++)
        	{
        		cell = row.createCell(j);
        		cell.setCellValue(q_table[i][j]);
        	}
         }
         try {
		          FileOutputStream fos = new FileOutputStream("D:\\qtable.xls");
		          workbook.write(fos);
		        
		          fos.close();
         } catch (IOException e) {
           e.printStackTrace();
         }
	}
	
	
	//返回Q_table的值，action=0表示小鸟上升，action=1表示小鸟下降
	public int getQtable(int birdX,int birdY,int columnX,int columnY,int action)
	{
		int y;
		int x=(columnX+columnWidth/2+size/2)-birdX;
		if(action==0)  //上升
		{
			y=birdY-distance;
		}
		else    //下降
		{
			y=birdY+distance;
		}
		int real_y=y-(columnY-gap/2);
		return q_table[x][real_y];
	}
	
	public int getMax(int birdX,int birdY,int columnX,int columnY)
	{
		int value0=getQtable(birdX,birdY,columnX,columnY,0);
		int value1=getQtable(birdX,birdY,columnX,columnY,1);
		if (value0>=value1)
		{
			return value0;
		}
		else
		{
			return value1;
		}
	}
	
	//获取最优的那一个，0表示上升，1表示下降
	public int getOptimist(int birdX,int birdY,int columnX,int columnY)
	{
		int true_action=0;   //0表示小鸟向上,1表示向下
		if (birdY<=(columnY-gap/2+size/2))
		{
			//bird_y=birdY+distance;
			return 1;
		}
		else if (birdY>=(columnY+gap/2-size/2))
		{
			//bird_y=birdY-distance;
			//true_action=0;
			return 0;
		}
		else
		{
			int value0=getQtable(birdX,birdY,columnX,columnY,0);
			int value1=getQtable(birdX,birdY,columnX,columnY,1);
			if (value0>value1)
			{
				return 0;   //小鸟上升
			}
			else if(value0<value1)
			{
				return 1;  //小鸟下降
			}
			else
			{
				if(birdY>=columnY)
				{
					return 0;
				}
				else
				{
					return 1;
				}
			}
		}
	}
	
	//训练,目的就是更新q_table的值
	public int training(int birdX,int birdY,int columnX,int columnY,Bird bird) throws Exception
	{
		
		int true_action=0;   //0表示小鸟向上,1表示向下
		if (birdY<=(columnY-gap/2+size/2))
		{
			//bird_y=birdY+distance;
			true_action=1;
		}
		else if (birdY>=(columnY+gap/2-size/2))
		{
			//bird_y=birdY-distance;
			true_action=0;
		}
		else
		{
			
			int action=rand.nextInt(2);   //随机选择
			true_action=action;
			if(action==0)    //表示小鸟上升
			{
				int y=birdY-distance;
				bird_y=y;
				int x=(columnX+columnWidth/2+size/2)-birdX;
				int real_y=y-(columnY-gap/2);
				q_table[x][real_y]=getReward(birdX,y,columnX,columnY)+(int)(learningRate*getMax(birdX,y,columnX,columnY));
			}
			else             //表示小鸟下降
			{
				int y=birdY+distance;
				bird_y=y;
				int x=(columnX+columnWidth/2+size/2)-birdX;
				int real_y=y-(columnY-gap/2);
				q_table[x][real_y]=getReward(birdX,y,columnX,columnY)+(int)(learningRate*getMax(birdX,y,columnX,columnY));
			}
		}
		System.out.println("here");
		return true_action;
		
	}
	
}
