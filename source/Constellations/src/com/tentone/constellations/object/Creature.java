package com.tentone.constellations.object;

import java.util.Iterator;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tentone.constellations.tree.QuadTree;
import com.tentone.constellations.utils.Generator;

public class Creature extends Vector2
{
	private static final long serialVersionUID = 7669506454940957738L;
	
	private static final float ATTRACTION_DISTANCE = 2.0f;
	private static final float COLLISION_DISTANCE = 0.1f;
	private static final float FRICTION = 2.0f;
	private static final float TURBULENCE = 0.03f;
	
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
			if(distance < planet.level + 2f)
			{
				Vector2 direction = new Vector2(planet.x - x, planet.y - y);
				direction.nor();
				
				//Inside the planet core
				if(distance < planet.level)
				{
					//Process planet collision
					planet.collidePlanet(this);
					direction.scl(0.15f);
					velocity.sub(direction);
				}
				//Close to the planet ring
				else
				{
					direction.scl(0.12f);
					direction.rotateRad(0.7f);
					velocity.add(direction);
				}
			}
			
			//Creature reacts to close creatures
			if(this.parent != null)
			{
				//Calculate how many level to go up
				int levels = MathUtils.roundPositive(ATTRACTION_DISTANCE / this.parent.height);
				QuadTree parent = this.parent;
				
				//Go up the tree
				while(levels > 0 && parent.parent != null)
				{
					parent = parent.parent;
					levels--;
				}
				
				this.updateTree(this.parent);
			}
		}
		
		//Add velocity to move to location
		if(this.task != Task.Idle)
		{
			float distance = dst(destination);
			
			//Check is destination reached
			if(distance > 0.2f)
			{
				Vector2 direction = new Vector2(destination.x - x, destination.y - y);
				direction.nor();
				direction.scl(0.15f);
				
				velocity.add(direction);
			}
			//If destination reached set state to idle
			else
			{
				this.task = Task.Idle;
			}
		}
		
		//Random movement
		velocity.x += MathUtils.random(-TURBULENCE, TURBULENCE);
		velocity.y += MathUtils.random(-TURBULENCE, TURBULENCE);
		
		//Update position
		add(velocity.x * delta, velocity.y * delta);
		
		//Apply friction
		velocity.sub(velocity.x * delta * FRICTION, velocity.y * delta * FRICTION);
		
		//Update position inside tree
		if(this.parent != null)
		{
			this.parent.update(this);
		}
	}
	
	//Update creatures of tree
	public void updateTree(QuadTree tree)
	{
		if(tree.elements.size() > 0)
		{
			Iterator<Creature> creatures = tree.elements.iterator();
			while(creatures.hasNext())
			{
				updateCreature(creatures.next());
			}
		}
		
		if(!tree.isLeaf())
		{
			for(int i = 0; i < tree.children.length; i++)
			{
				updateTree(tree.children[i]);
			}
		}
	}
	
	//Update data relative to creature
	public void updateCreature(Creature creature)
	{
		//Check if creatures are from different owners
		if(this.owner != creature.owner)
		{
			float dist = dst(creature);
			
			if(dist < COLLISION_DISTANCE)
			{
				this.destroy();
				creature.destroy();
				return;
			}
			else if(dist < ATTRACTION_DISTANCE)
			{
				Vector2 direction = new Vector2(creature.x - x, creature.y - y);
				direction.nor();
				direction.scl(0.001f);
				
				velocity.add(direction);
			}
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
	
	//Set creature position
	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	//Create string with debug information
	@Override
	public String toString()
	{
		String str = "ID: " + id + " Position:" + x + "," + y;
		QuadTree tree = parent;
		
		while(tree != null)
		{
			str += "\n    " + tree.id;
			
			if(tree.isRoot())
			{
				str += " Root";
			}
			else if(tree.isLeaf())
			{
				str += " Leaf";
			}
			else if(tree.parent.isLeaf())
			{
				str += " Parent is Leaf";
			}
			else
			{
				boolean found = false;
				
				for(int i = 0; i < tree.parent.children.length; i++)
				{
					if(tree.parent.children[i] == tree)
					{
						found = true;
						break;
					}
				}
				
				if(!found)	
				{
					str += " No down path";
				}
			}
			
			tree = tree.parent;
		}
		
		return str;
	}
}
