package com.tentone.constellations;

import com.badlogic.gdx.graphics.Color;

public class Player
{
	public String name;
	public Color color;
	public int id;
	
	//Player constructor
	public Player(String name, Integer id, Color color)
	{
		this.name = name;
		this.id = (id != null) ? id : (int)(Math.random() * Integer.MAX_VALUE);
		this.color = (color != null) ? color : new Color();
	}
}
