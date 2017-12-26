package com.hsj.bird;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class BackgroundMusic extends Thread{
	
	Player player;
    File music;
    
    static BackgroundMusic instance=null;
    //构造方法  参数是一个.mp3音频文件
    public BackgroundMusic(File file) {
        this.music = file;
    }
    
    public static BackgroundMusic getInstance()   //单例模式
    {
    	if (instance==null)
    	{
    		File file=new File("music.mp3");
    		instance=new BackgroundMusic(file);
    	}
    	return instance;
    }
    
    public static void init()
    {
    	instance=null;
    }
    
    //重写run方法
    @Override
    public void run() {
        super.run();
        try {
            play();     
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //播放方法
    public void play() throws FileNotFoundException, JavaLayerException {

            BufferedInputStream buffer =
                    new BufferedInputStream(new FileInputStream(music));
            player = new Player(buffer);
            player.play();
    }
    
	
	
}
 
	


