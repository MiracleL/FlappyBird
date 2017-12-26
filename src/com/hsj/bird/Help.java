package com.hsj.bird;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTextArea;

//获取帮助
public class Help extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Help()
	{
		this.setBackground(Color.cyan);
		JTextArea text=new JTextArea ();
		text.setBackground(Color.cyan);
		text.setEditable(false);
		StringBuilder build=new StringBuilder();
		build.append("欢迎来到本游戏\n");
		build.append("本游戏通过鼠标就可以操作\n");
		
		text.setText(build.toString());
		this.add(text);
		
	}
	

}
