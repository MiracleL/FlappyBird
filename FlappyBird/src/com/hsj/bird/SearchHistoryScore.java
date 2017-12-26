package com.hsj.bird;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

//�鿴��ʷ����
public class SearchHistoryScore extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane; 
    private JScrollPane scrollPane;
    private JTextArea textArea;

	SearchHistoryScore(ArrayList<Integer> array)
	{
		
		contentPane=new JPanel();
        contentPane.setBorder(new EmptyBorder(5,5,5,5));
        contentPane.setLayout(new BorderLayout(0,0));
        this.setContentPane(contentPane);
        scrollPane=new JScrollPane();
        contentPane.add(scrollPane,BorderLayout.CENTER);
        textArea=new JTextArea();
           
        StringBuilder build=new StringBuilder();
		for(int i=0;i<array.size();i++)
		{
			build.append("������  ");
			build.append(array.get(i).toString()+"\n");	
		}
		textArea.setText(build.toString());
		textArea.setEditable(false);
		
		textArea.setBackground(Color.cyan);
		
        //scrollPane.add(textArea); 
        scrollPane.setViewportView(textArea);
        this.setTitle("��ѯ��ʷ����");
        this.setBounds(100, 100, 250, 500);
        this.setVisible(true);
	}
}
