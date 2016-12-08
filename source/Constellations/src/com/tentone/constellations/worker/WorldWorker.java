package com.tentone.constellations.worker;

import com.tentone.constellations.elements.World;

public class WorldWorker implements Runnable
{
	private World world;
	
	public WorldWorker(World world)
	{
		this.world = world;
	}
	
	@Override
	public void run()
	{
		world.update(0.016f);
	}
}
