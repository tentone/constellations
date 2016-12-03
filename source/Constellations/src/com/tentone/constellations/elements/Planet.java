package com.tentone.constellations.elements;

import com.badlogic.gdx.math.Circle;
import com.tentone.constellations.Player;

public class Planet extends Circle
{	
	private static final long serialVersionUID = -4799808330666229595L;
	private static final float SPAWN_TIME = 1f;
	
	//World pointer
	public World world;
		
	//Planet attributes
	public int size; //Planet size (max level)

	//Identification
	public Player owner;
	public int id;
	
	//Runtime variables
	public float time;
	public int life; //max life = level * 100
	public int level; //max level = size
	
	//Planet constructor
	public Planet(int size)
	{
		super(0, 0, size + 1);
		
		this.size = size;
		
		//Runtime
		this.time = 0.0f;
		this.level = 0;
		this.life = 0;
		
		//Identification
		this.owner = null;
		this.id = (int)(Math.random() * Integer.MAX_VALUE);
	}
	
	//Update planet
	public void update(float delta)
	{
		if(owner != null)
		{
			//Update time
			this.time += delta;
			
			//Check time to spawn creatures
			if(time > SPAWN_TIME)
			{
				time -= SPAWN_TIME;

				//Spawn creatures
				for(int i = 0; i < this.level; i++)
				{
					Creature creature = new Creature();
					creature.owner = owner;
					creature.set(x + (float)Math.random() * 0.01f, y + (float)Math.random() * 0.01f);
					
					world.addCreature(creature);
				}
			}
		}
	}
	
	//Build planet
	public void constructPlanet(Creature creature)
	{
		creature.destroy();
		life += 2;
		
		this.owner = creature.owner; 
	}
	
	//Inflict damage to planet
	public void inflictDamage(Creature creature)
	{
		creature.destroy();
		life -= 2;
		
		if(life <= 0)
		{
			this.owner = null;
			this.level = 0;
			this.life = 0;
		}		
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
		this.life = (player != null) ? 100 : 0;
	}
}
