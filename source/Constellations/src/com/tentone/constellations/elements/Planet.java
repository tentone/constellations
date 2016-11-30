package com.tentone.constellations.elements;

import com.badlogic.gdx.math.Circle;
import com.tentone.constellations.Player;

public class Planet extends Circle
{	
	private static final long serialVersionUID = -4799808330666229595L;

	//World pointer
	public World world;
		
	//Planet attributes
	public int size; //Planet size (max level)
	public float orbit; //Orbit ring size
	
	//Runtime variables
	public Player owner; //Planet owner
	public int life; //max life = level * 100
	public int level; //max level = size
	
	//Planet constructor
	public Planet(int size, int level)
	{
		super(0, 0, size);
		
		assert level <= size;
		
		this.size = size;
		
		this.life = 100 * level;
		this.level = level;
		this.owner = null;
	}
	
	//Set level 
	public void setLevel(int level)
	{
		assert level <= this.size;
		
		this.radius = level;
	}
	
	//Set planet position
	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	//Set planet owner
	public void setOwner(Player player)
	{
		this.owner = player;
	}
}
