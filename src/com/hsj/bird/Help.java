package com.hsj.bird;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTextArea;

//��ȡ����
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
		build.append("��ӭ��������Ϸ\n");
		build.append("����Ϸͨ�����Ϳ��Բ���\n");
		
		text.setText(build.toString());
		this.add(text);
		
	}
	

}
