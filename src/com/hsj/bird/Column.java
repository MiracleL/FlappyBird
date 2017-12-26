package com.hsj.bird;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
/**
 * x, y 是空隙中间位置. 
 */
public class Column {
	BufferedImage image;
	//以柱子的中间作为柱子的位置
	int x;
	int y; // 140 ~ 280
	int width;
	int height;
	int gap = 144;   //上下柱子的间距
	Random r = new Random();
	
	int distance = 245;   //两根柱子之间的间距，柱子的中间为柱子的
	
	public Column(int n) throws IOException {    //初始化柱子
		image = ImageIO.read(getClass().getResource("column.png"));
		width = image.getWidth();
		height = image.getHeight();
		
		this.x = distance * n + 80;
		this.y = r.nextInt(120) + 220;
	}
	
	public void step(){    //柱子向左移动
		x--;
		if(x<=-width/2){
			x = distance*2-width/2;
			this.y = r.nextInt(120) + 220;
		}
	}
	public void paint(Graphics g){   //绘制柱子
		//g.drawRect(x-width/2, y-height/2, width, height/2-gap/2);
		//g.drawRect(x-width/2, y+gap/2, width, height/2-gap/2);
		g.drawImage(image, x-width/2, y-height/2, null);
	}
}
