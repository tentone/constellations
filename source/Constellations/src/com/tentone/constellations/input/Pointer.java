package com.tentone.constellations.input;

import com.badlogic.gdx.math.Vector2;

public class Pointer extends Button
{
	public Vector2 position, delta;

	public Pointer()
	{
		super();
		
		this.position = new Vector2(0, 0);
		this.delta = new Vector2(0, 0);
	}
}
