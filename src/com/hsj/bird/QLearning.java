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

//q-learning�㷨,ѧϰ��Ϊ0.1

public class QLearning {
	
	int[][] q_table = new int[300][144];     //ֻѡȡ���õĲ��֣����ಿ������Ϊ0
	
	int gap = 144;   //�������ӵļ��
	int size = 40;   //��ķ�Χ, ��ķ�Χ��һ������������, ���ĵ���x,y 
	int columnWidth=78;  //���ӵĿ��
	
	int distance=20;    //ÿ�����������½��ľ���
	
	int bird_x,bird_y;   //����ֵ
	Random rand;
	
	double learningRate=0.2;  //ѧϰ��
	
	public static int time=0;  //ѵ���Ĵ���
	
	
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
		// ��ʼ��q_table��ֵΪ0
//		for(int i=0;i<300;i++)
//		{
//			for(int j=0;j<144;j++)
//			{
//				//Խ���м䣬ֵԽ��
//				double rate=Math.abs(72-j)/150.0;
//				if(j<=40||j>=105)   //�߽磬��΢���õĴ���һ��
//				{
//					q_table[i][j]=0;
//				}
//				else
//				{
//					//q_table[i][j]=(int)(10000*(1-rate));   //Խ�������ӿ�϶���м佱��Խ��
//					q_table[i][j]=(int)(100*(Math.abs(72-j)+1));
//				}
//			}
//		}
//		save();
		rand=new Random();
	}
	
	
	//����rewardֵ����ѵ��ʱ��,Խ�������ӿ�϶�Ľ���Խ��
	public int getReward(int birdX,int birdY,int columnX,int columnY)
	{
		
		int reward=0;   //����Z
		double rate=Math.abs(columnY-birdY)/1000.0;  
		if (birdY>=(columnY-gap/2+size/2)&&birdY<=(columnY+gap/2-size/2))
		{
			double value=10000*(1-rate);   //Խ�������ӿ�϶���м佱��Խ��
			reward=(int)(value);
		}
		return reward;
	}
	
	//��excel�ļ��ж�ȡq_table��
	 public void read() throws FileNotFoundException, IOException
	 {
	    	HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream("D:\\\\qtable.xls"));
	    	HSSFSheet sheet = workbook.getSheet("�û���һ");
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
	
	//����q_tableΪexcel�ļ�
	public void save()
	{
		 HSSFWorkbook workbook = new HSSFWorkbook();
	        //�ڶ�������workbook�д���һ��sheet��Ӧexcel�е�sheet
	     HSSFSheet sheet = workbook.createSheet("�û���һ");
	     
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
	
	
	//����Q_table��ֵ��action=0��ʾС��������action=1��ʾС���½�
	public int getQtable(int birdX,int birdY,int columnX,int columnY,int action)
	{
		int y;
		int x=(columnX+columnWidth/2+size/2)-birdX;
		if(action==0)  //����
		{
			y=birdY-distance;
		}
		else    //�½�
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
	
	//��ȡ���ŵ���һ����0��ʾ������1��ʾ�½�
	public int getOptimist(int birdX,int birdY,int columnX,int columnY)
	{
		int true_action=0;   //0��ʾС������,1��ʾ����
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
				return 0;   //С������
			}
			else if(value0<value1)
			{
				return 1;  //С���½�
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
	
	//ѵ��,Ŀ�ľ��Ǹ���q_table��ֵ
	public int training(int birdX,int birdY,int columnX,int columnY,Bird bird) throws Exception
	{
		
		int true_action=0;   //0��ʾС������,1��ʾ����
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
			
			int action=rand.nextInt(2);   //���ѡ��
			true_action=action;
			if(action==0)    //��ʾС������
			{
				int y=birdY-distance;
				bird_y=y;
				int x=(columnX+columnWidth/2+size/2)-birdX;
				int real_y=y-(columnY-gap/2);
				q_table[x][real_y]=getReward(birdX,y,columnX,columnY)+(int)(learningRate*getMax(birdX,y,columnX,columnY));
			}
			else             //��ʾС���½�
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
