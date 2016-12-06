package com.tentone.constellations.elements;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.tentone.constellations.Player;
import com.tentone.constellations.tree.QuadTree;

public class World extends Rectangle
{
	private static final long serialVersionUID = 2597058350349965364L;
	
	//Quad tree
	public QuadTree tree;
	
	//Planets, creatures and players
	public ConcurrentLinkedQueue<Player> players;
	public ConcurrentLinkedQueue<Planet> planets;
	public ConcurrentLinkedQueue<Creature> creatures;
	
	//World global time
	public double time;
	
	//World constructor
	public World()
	{
		super(0, 0, 45, 25);
		
		this.players = new ConcurrentLinkedQueue<Player>();
		this.planets = new ConcurrentLinkedQueue<Planet>();
		this.creatures = new ConcurrentLinkedQueue<Creature>();
		
		this.tree = new QuadTree(x, y, width, height);
	}
	
	//Generate random world
	public static World generateWorld()
	{
		World world = new World();
		
		Player a = new Player("a");
		world.addPlayer(a);
		
		Planet planet = new Planet(3);
		while(!world.contains(planet))
		{
			planet.setPosition(MathUtils.random(world.width * 0.1f), MathUtils.random(world.height));
		}
		planet.setOwner(a);
		planet.setLevel(1);
		world.addPlanet(planet);
		
		Player b = new Player("b");
		world.addPlayer(b);
		
		planet = new Planet(3);
		while(!world.contains(planet))
		{
			planet.setPosition(world.width * 0.8f + MathUtils.random(world.width * 0.1f), MathUtils.random(world.height));
		}
		planet.setOwner(b);
		planet.setLevel(1);
		world.addPlanet(planet);
		
		//Create planets
		for(int i = 0; i < 8; i++)
		{
			boolean colliding = true;
			planet = null;
			
			while(colliding)
			{
				colliding = false;
				
				planet = new Planet(MathUtils.random(1, 2));
				while(!world.contains(planet))
				{
					planet.setPosition(MathUtils.random(world.width), MathUtils.random(world.height));
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
		
		return world;
	}
	
	//Update world state
	public void update(float delta)
	{
		//Don't let delta get to high or too low (10~300fps)
		if(delta > 0.1f)
		{
			delta = 0.1f;
		}
		else if(delta < 0.00333f)
		{
			delta = 0.00333f;
		}
		
		//Update world time
		this.time += delta;
		
		//Update creatures state
		Iterator<Creature> creatures = this.creatures.iterator();
		while(creatures.hasNext())
		{
			creatures.next().update(delta);
		}
		
		//Update planets
		Iterator<Planet> planets = this.planets.iterator();
		while(planets.hasNext())
		{
			planets.next().update(delta);
		}
	}
	
	//Add a creature to the world
	public void addPlayer(Player player)
	{
		this.players.add(player);
	}
	
	//Add a planet to the world
	public void addPlanet(Planet planet)
	{
		planet.world = this;
		
		this.planets.add(planet);
	}
	
	//Add a creature to the world
	public void addCreature(Creature creature)
	{
		creature.world = this;
		
		this.tree.add(creature);
		this.creatures.add(creature);
	}
	
	//Check if a planet is inside the world
	public boolean contains(Circle circle)
	{
		return circle.x - circle.radius >= x && circle.x + circle.radius <= x + width
				&& circle.y - circle.radius >= y && circle.y + circle.radius <= y + height;
	}
}
