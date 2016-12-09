package com.tentone.constellations.worker;

import java.util.Iterator;

import com.tentone.constellations.elements.Creature;
import com.tentone.constellations.tree.QuadTree;

public class WorldWorker implements Runnable
{
	private QuadTree tree;
	private float delta;
	
	//Constructor
	public WorldWorker(QuadTree tree, float delta)
	{
		this.tree = tree;
		this.delta = delta;
	}

	public WorldWorker()
	{
		this.tree = null;
		this.delta = 0.0167f;
	}
	
	//Set tree and delta
	public void set(QuadTree tree, float delta)
	{
		this.tree = tree;
		this.delta = delta;
	}
	
	//Run worker update
	@Override
	public void run()
	{
		Iterator<Creature> creatures = this.tree.toLinkedList().iterator();
		while(creatures.hasNext())
		{
			creatures.next().update(delta);
		}
	}
}
