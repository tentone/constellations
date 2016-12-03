package com.tentone.constellations.elements;

import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.tentone.constellations.Player;

public class Creature extends Vector2
{
	private static final long serialVersionUID = 7669506454940957738L;
	private static final float friction = 3f;
	
	//World pointer
	public World world;
	
	//Creature identification
	public int id;
	public Player owner;
	
	//Physics control
	public Vector2 velocity;
	
	//Creature control
	public Task task;
	public Vector2 target;
	
	//Creature constructor
	public Creature()
	{
		this.id = (int)(Math.random() * Integer.MAX_VALUE);
		this.owner = null;
		
		this.task = Task.Idle;
		this.target = new Vector2(0, 0);
		
		this.velocity = new Vector2(0, 0);
	}
	
	//Update creature state
	public void update(float delta)
	{	
		//Creature react to close planets
		Iterator<Planet> planets = world.planets.iterator();

		while(planets.hasNext())
		{
			Planet planet = planets.next();
			float distance = dst(planet.x, planet.y);
			
			//Check if its close to planet
			if(distance < 2f * planet.level)
			{
				Vector2 direction = new Vector2(planet.x - x, planet.y - y);
				direction.nor();
				
				//Inside the planet core
				if(distance < planet.level)
				{	
					if(planet.owner == null)
					{
						planet.constructPlanet(this);
					}
					else if(planet.owner != owner)
					{
						planet.inflictDamage(this);
					}
					else if(task == Task.Upgrade)
					{
						
					}
					
					direction.scl(0.1f);
					velocity.sub(direction.x, direction.y);
				}
				//Close to the planet ring
				else
				{
					direction.scl(0.12f);
					direction.rotateRad(0.8f);
					velocity.add(direction.x, direction.y);
				}
			}

			//Creature reacts to close creatures
			Iterator<Creature> creatures = world.creatures.iterator();
			while(creatures.hasNext())
			{
				Creature creature = creatures.next();
				
				if(id != creature.id && owner != creature.owner)
				{
					if(colliding(creature))
					{
						world.creatures.remove(this);
						world.creatures.remove(creature);
						break;
					}
				}
			}
		}
		
		//Add velocity to move to location
		if(task == Task.Move)
		{
			float distance = dst(target);
			
			if(distance < 0.3f)
			{
				task = Task.Idle;
			}
			else
			{
				Vector2 direction = new Vector2(target.x - x, target.y - y);
				direction.nor();
				direction.scl(0.15f);
				
				velocity.add(direction);
			}
		}
		
		//Update position
		add(velocity.x * delta, velocity.y * delta);
		
		//Apply friction
		velocity.sub(velocity.x * delta * friction, velocity.y * delta * friction);
	}
	
	//Destroy creature
	public void destroy()
	{
		world.creatures.remove(this);
	}
	
	//Check if its colliding with another creature
	public boolean colliding(Creature creature)
	{
		return dst(creature) < 0.15f;
	}
	
	//Set creature position
	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
}
