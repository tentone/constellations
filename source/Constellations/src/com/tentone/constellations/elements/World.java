package com.tentone.constellations.elements;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tentone.constellations.Player;

public class World extends Rectangle
{
	private static final long serialVersionUID = 2597058350349965364L;
	
	public ArrayList<Planet> planets; //ConcurrentLinkedQueue
	public ArrayList<Creature> creatures;
	
	//World constructor
	public World()
	{
		super(0, 0, 40, 25);
		
		this.planets = new ArrayList<Planet>();
		this.creatures = new ArrayList<Creature>();
	}
	
	//Generate random world
	public static World generateWorld()
	{
		World world = new World();
		
		Player a = new Player("a", null, Color.RED);
		Player b = new Player("b", null, Color.BLUE);
		
		//Create planets
		for(int i = 0; i < 10; i++)
		{
			boolean colliding = true;
			Planet planet = null;
			
			while(colliding)
			{
				colliding = false;
				
				planet = new Planet((int)Math.ceil(Math.random() * 2.0) + 1, 1);
				
				while(!world.contains(planet))
				{
					planet.setPosition((float)Math.random() * world.width, (float)Math.random() * world.height);
				}
				
				Iterator<Planet> itp = world.planets.iterator();
				while(itp.hasNext())
				{
					if(planet.overlaps(itp.next()))
					{
						colliding = true;
					}
				}
			}
			
			world.addPlanet(planet);
		}
		
		//Give player a and b a a starting planet
		world.planets.get(0).owner = a;
		world.planets.get(1).owner = b;
		
		//Create creatures
		for(int i = 0; i < 3000; i++)
		{
			Creature creature = new Creature();
			creature.setPosition((float)Math.random() * world.width, (float)Math.random() * world.height);
			creature.owner = (i < 1500) ? a : b;
			
			world.addCreature(creature);
		}
		
		return world;
	}
	
	//Update world state
	public void update()
	{
		//Update planets state
		/*Iterator<Planet> itp = this.planets.iterator();
		while(itp.hasNext())
		{
			Planet planet = itp.next();
			
			if(planet.owner != null)
			{
				for(int i = 0; i < planet.level; i++)
				{
					Creature creature = new Creature();
					creature.owner = planet.owner;
					creature.x = planet.x;
					creature.y = planet.y;
					
					this.creatures.add(creature);
				}
			}
		}*/
		
		//Update creatures state
		Iterator<Creature> itc = this.creatures.iterator();
		while(itc.hasNext())
		{
			Creature creature = itc.next();
			
			Iterator<Planet> itpl = this.planets.iterator();
			while(itpl.hasNext())
			{
				Planet planet = itpl.next();
				float dist = creature.dst(planet.x, planet.y);

				//Outside the planet ring
				if(dist > planet.level + 1)
				{
					Vector2 dir = new Vector2(planet.x - creature.x, planet.y - creature.y);
					dir.nor();
					
					dist *= dist * 10;
					
					creature.velocity.add(dir.x / dist, dir.y / dist);
				}
				//Inside the planet core
				else if(dist < planet.level)
				{
					Vector2 dir = new Vector2(creature.x - planet.x, creature.y - planet.y);
					dir.nor();

					dist *= dist * 0.1f;
					
					creature.velocity.add(dir.x * dist, dir.y * dist);
				}
				//Inside the planet ring
				else if(dist > planet.level && dist < planet.level + 1)
				{
					Vector2 dir = new Vector2(creature.x - planet.x, creature.y - planet.y);
					dir.nor();
					dir.rotateRad(1.57f * (dist - planet.level));
					
					creature.velocity.add(dir.x * 0.01f, dir.y * 0.01f);
				}
				
				creature.update();
			}
		}
		
	}
	
	//Add a planet to the world
	public void addPlanet(Planet planet)
	{
		this.planets.add(planet);
	}
	
	//Add a creature to the world
	public void addCreature(Creature creature)
	{
		this.creatures.add(creature);
	}
	
	//Check if a planet is inside the world
	public boolean contains(Circle circle)
	{
		float xmin = circle.x - circle.radius;
		float xmax = xmin + 2f * circle.radius;

		float ymin = circle.y - circle.radius;
		float ymax = ymin + 2f * circle.radius;
		
		return ((xmin > x && xmin < x + width) && (xmax > x && xmax < x + width))
			&& ((ymin > y && ymin < y + height) && (ymax > y && ymax < y + height));
		//return this.contains(new Rectangle(circle.x - circle.radius, circle.y - circle.radius, 2f * circle.radius, 2f * circle.radius));
	}
}
