package com.tentone.constellations.worker;

import java.util.Iterator;

import com.tentone.constellations.elements.Creature;
import com.tentone.constellations.elements.World;
import com.tentone.constellations.tree.QuadTree;

public class WorldWorker implements Runnable
{
	public World world;
	private QuadTree tree;
	private float delta;
	
	//Constructor
	public WorldWorker(World world)
	{
		this.world = world;
		this.tree = world.tree;
		this.delta = 0.0167f;
	}
	
	//Set tree branch
	public void setTree(QuadTree tree)
	{
		this.tree = tree;
	}
	
	//Set delta time
	public void setDelta(float delta)
	{
		this.delta = delta;
	}
	
	//Run worker update
	@Override
	public void run()
	{
		Iterator<Creature> creatures = this.tree.iterator();
		while(creatures.hasNext())
		{
			creatures.next().update(delta);
		}
	}
}
