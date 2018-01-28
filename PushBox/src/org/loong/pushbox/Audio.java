package org.loong.pushbox;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;


public class Audio{ 
	
	private static File msg = new File("msg.wav");
	private static File click = new File("click.wav");
	private static File step = new File("step.wav");
	private static File winner = new File("winner.wav");
	
	public static void playMsg(){
		try {
			AudioClip ac = Applet.newAudioClip(msg.toURL());
			ac.play();
		}
		catch (MalformedURLException e) {
            e.printStackTrace();
        }
	}
	
	public static void playWinner(){
		try {
			AudioClip ac = Applet.newAudioClip(winner.toURL());
			ac.play();
		}
		catch (MalformedURLException e) {
            e.printStackTrace();
        }
	}
	
	public static void playClick(){
		try {
			AudioClip ac = Applet.newAudioClip(click.toURL());
			ac.play();
		}
		catch (MalformedURLException e) {
            e.printStackTrace();
        }
	}
	
	public static void playStep(){
		try {
			AudioClip ac = Applet.newAudioClip(step.toURL());
			ac.play();
		}
		catch (MalformedURLException e) {
            e.printStackTrace();
        }
	}
} 
