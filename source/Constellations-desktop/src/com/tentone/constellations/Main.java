package com.tentone.constellations;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main
{
	public static void main(String[] args)
	{
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		
		cfg.title = Constellations.NAME + " " + Constellations.VERSION;
		cfg.width = 1024;
		cfg.height = 600;
		cfg.backgroundFPS = 0;
		cfg.foregroundFPS = 0;
		cfg.forceExit = true;
		cfg.vSyncEnabled = false;
		cfg.useGL30 = false;
		
		new LwjglApplication(new Constellations(), cfg);
	}
}
