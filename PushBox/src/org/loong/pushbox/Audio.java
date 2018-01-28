package org.loong.pushbox;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;


public class Audio{ 
	
	private static AudioClip msg;
	private static AudioClip click;
	private static AudioClip step;
	private static AudioClip winner;
	
	public Audio() throws MalformedURLException
	{
		msg = Applet.newAudioClip((new File("msg.wav")).toURL());
		click = Applet.newAudioClip((new File("click.wav")).toURL());
		step = Applet.newAudioClip((new File("step.wav")).toURL());
		winner = Applet.newAudioClip((new File("winner.wav")).toURL());
	}
	
	public void playMsg(){
		msg.play();
	}
	
	public void playWinner(){
		winner.play();
	}
	
	public void playClick(){
		click.play();
	}
	
	public void playStep(){
		step.play();
	}
} 
