package com.tentone.constellations.elements;

import com.badlogic.gdx.graphics.Color;
import com.tentone.constellations.utils.Generator;

public class Player
{
	public String name;
	public Color color;
	public int id;
	
	//Constructor
	public Player(String name)
	{
		this.name = name;
		this.id = Generator.generateID();
		this.color = Generator.generateColor();
	}
	
	public Player(String name, Integer id, Color color)
	{
		this.name = name;
		this.id = (id != null) ? id : Generator.generateID();
		this.color = (color != null) ? color : Generator.generateColor();
	}
}
