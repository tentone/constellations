package com.tentone.constellations.elements;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.tentone.constellations.utils.Generator;

public class Planet extends Circle
{	
	private static final long serialVersionUID = 2799808330666229595L;
	
	//Constants
	public static final float spawn_time = 0.001f;
	public static final int life_per_level = 100;
	public static final int life_per_creature = 2;
	
	//World pointer
	public World world;
		
	//Planet size
	public int size;

	//Identification
	public Player owner;
	public int id;
	
	//Runtime variables
	public float time; //Planet time
	public boolean conquered; //True if planet is conquered
	public int life; //Max life = level * life per level
	public int level; //Max level = size
	
	//Planet constructor
	public Planet(int size)
	{
		super(0, 0, size + 1);
		
		this.size = size;
		
		//Runtime
		this.time = 0.0f;
		this.life = 0;
		
		this.conquered = false;
		this.level = 1;
		
		//Identification
		this.owner = null;
		this.id = Generator.generateID();
	}
	
	public Planet(int size, int level, Player owner)
	{
		super(0, 0, size + 1);
		
		this.size = size;
		this.time = 0.0f;
		this.level = level;
		this.life = level * life_per_level;
		this.conquered = true;
		
		this.owner = owner;
		this.id = Generator.generateID();
	}
	
	//Update planet
	public void update(float delta)
	{
		if(this.conquered)
		{
			//Update time
			this.time += delta;
			
			//Check time to spawn creatures
			while(this.time > spawn_time)
			{
				this.time -= spawn_time;

				//Spawn creatures
				for(int i = 0; i < this.level; i++)
				{
					Creature creature = new Creature();
					creature.owner = owner;
					creature.set(x + MathUtils.random(-0.01f, 0.01f), y + MathUtils.random(-0.01f, 0.01f));
					world.addCreature(creature);
				}
			}
		}
	}
	
	//Check if planet is upgradable
	public boolean upgradable()
	{
		return this.level < this.size;
	}
	
	//Check if this planet is damaged
	public boolean damaged()
	{
		return this.life < this.level * Planet.life_per_level;
	}
	
	//Process creature collisions with planet
	public void collidePlanet(Creature creature)
	{
		assert creature != null;
		
		if(creature.task == Task.Conquer && !this.conquered && creature.target == this)
		{
			if(this.owner == null)
			{
				this.owner = creature.owner;
			}
			
			if(creature.owner == this.owner)
			{
				creature.destroy();
				this.life += life_per_creature;
				
				if(this.life >= life_per_level)
				{
					this.conquered = true;
				}
			}
			else
			{				
				creature.destroy();
				this.life -= life_per_creature;
				
				if(this.life <= 0)
				{
					this.reset();
				}
			}
		}
		else if(creature.owner == this.owner)
		{
			if(creature.task == Task.Upgrade && creature.limit > this.level)
			{
				creature.destroy();
				this.life += life_per_creature;
				
				if(this.life >= (this.level + 1) * life_per_level)
				{
					this.level++;
				}
			}
			else if(creature.task == Task.Heal && this.damaged())
			{
				creature.destroy();
				this.life += life_per_creature;
			}
		}
		else if(this.owner != creature.owner && this.owner != null)
		{
			creature.destroy();
			this.life -= life_per_creature;
			
			if(this.life <= 0)
			{
				this.reset();
			}
		}
	}
	
	//Set level 
	public void setLevel(int level)
	{
		assert level <= this.size;
		
		this.level = level;
		this.life = level * life_per_level;
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
		assert player != null;
		
		this.owner = player;
		this.life = this.level * life_per_level;
		this.conquered = true;
	}
	
	//Reset planet to default value
	public void reset()
	{
		this.life = 0;
		this.level = 1;
		this.owner = null;
		this.conquered = false;
	}
}
