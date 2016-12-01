package com.tentone.constellations.elements;

import com.badlogic.gdx.math.Vector2;
import com.tentone.constellations.Player;

public class Creature extends Vector2
{
	private static final long serialVersionUID = 7669506454940957738L;

	//World pointer
	public World world;
	
	//Creature runtime
	public Player owner = null;
	public Vector2 velocity;
	
	//Creature constructor
	public Creature()
	{
		this.velocity = new Vector2(0, 0);
	}
	
	//Update creature state
	public void update()
	{
		this.add(velocity);
		
		this.velocity.x *= 0.3;
		this.velocity.y *= 0.3;
	}
	
	//Set creature position
	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
}
