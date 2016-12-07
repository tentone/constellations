package com.tentone.constellations.elements;

import java.util.Iterator;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tentone.constellations.Player;
import com.tentone.constellations.tree.QuadTree;
import com.tentone.constellations.utils.Generator;

public class Creature extends Vector2
{
	private static final long serialVersionUID = 7669506454940957738L;
	private static final float friction = 2.0f;
	
	//World pointer
	public World world;
	
	//Creature identification
	public int id;
	public Player owner;
	
	//Parent node inside the quad tree
	public QuadTree parent;
	
	//Physics control
	public Vector2 velocity;
	
	//Task control
	public Task task;
	public int limit;
	public Planet target;
	public Vector2 destination;
	
	//Creature constructor
	public Creature()
	{
		this.id = Generator.generateID();
		this.owner = null;
		
		this.parent = null;
		
		this.task = Task.Idle;
		this.target = null;
		this.limit = 0;
		this.destination = new Vector2(0, 0);
		
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
					//Process planet collision
					planet.collidePlanet(this);
					
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
			if(this.parent != null)
			{
				Iterator<Creature> creatures = this.parent.iterator();//creatures.iterator();
				while(creatures.hasNext())
				{
					Creature creature = creatures.next();
					
					//Check if creatures are from different owners
					if(this.owner != creature.owner)// && this.id != creature.id)
					{
						if(this.colliding(creature))
						{
							this.destroy();
							creature.destroy();
							
							break;
						}
					}
				}
			}
		}
		
		//Add velocity to move to location
		if(this.task != Task.Idle)
		{
			float distance = dst(destination);
			
			if(distance < 0.3f)
			{
				this.task = Task.Idle;
			}
			else
			{
				Vector2 direction = new Vector2(destination.x - x, destination.y - y);
				direction.nor();
				direction.scl(0.15f);
				
				velocity.add(direction);
			}
		}
		
		velocity.x += MathUtils.random(-0.03f, 0.03f);
		velocity.y += MathUtils.random(-0.03f, 0.03f);
		
		//Update position
		add(velocity.x * delta, velocity.y * delta);
		
		//Apply friction
		velocity.sub(velocity.x * delta * friction, velocity.y * delta * friction);
		
		//Update position inside tree
		if(this.parent != null)
		{
			this.parent.update(this);
		}
	}
	
	//Destroy creature
	public void destroy()
	{
		if(this.parent != null)
		{
			this.parent.remove(this);
		}
		
		this.world.creatures.remove(this);
	}
	
	//Check if its colliding with another creature
	public boolean colliding(Creature creature)
	{
		return dst(creature) < 0.1f;
	}
	
	//Set creature position
	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
}
