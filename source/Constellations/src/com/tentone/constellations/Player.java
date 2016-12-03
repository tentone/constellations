package com.tentone.constellations;

import com.badlogic.gdx.graphics.Color;
import com.tentone.constellations.elements.Element;

public class Player
{
	public String name;
	public Color color;
	public int id;
	
	//Player constructor
	public Player(String name, Integer id, Color color)
	{
		this.name = name;
		this.id = (id != null) ? id : Element.generateID();
		this.color = (color != null) ? color : Element.generateColor();
	}
	
	public Player(String name)
	{
		this.name = name;
		this.id = Element.generateID();
		this.color = Element.generateColor();
	}
}
