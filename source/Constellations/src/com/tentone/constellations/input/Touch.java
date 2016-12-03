package com.tentone.constellations.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.tentone.constellations.camera.OrthographicCamera;
import com.tentone.constellations.camera.ResizeMode;

public class Touch
{
	//Position and delta
	public Vector2[] position, delta;
	
	//Camera
	private OrthographicCamera camera;
	
	//Input Constructor
	public Touch(OrthographicCamera camera)
	{
		this.camera = camera;

		this.delta = new Vector2[2];
		this.position = new Vector2[2];
		
		for(int i = 0; i < 2; i++)
		{
			this.delta[i] = new Vector2(0, 0);
			this.position[i] = new Vector2(0, 0);
		}
	}
	
	//Update input
	public Vector2 getPosition(int pointer)
	{
		Vector2 position = new Vector2(0, 0);
		
		if(camera.resize_mode == ResizeMode.HORIZONTAL)
		{
			position.x = (Gdx.input.getX(pointer) * camera.size_ratio * camera.zoom) + camera.position.x - (camera.size * camera.zoom * camera.aspect_ratio / 2f);
			position.y = ((camera.size - Gdx.input.getY(pointer) * camera.size_ratio) * camera.zoom) + camera.position.y - (camera.size / 2f * camera.zoom);
		}
		else if(camera.resize_mode == ResizeMode.VERTICAL)
		{
			//TODO <ADD CODE HERE>
		}
		
		return position;
	}
	
	//Get pointer delta
	public Vector2 getDelta(int pointer)
	{
		Vector2 delta = new Vector2(0, 0);
		
		if(camera.resize_mode == ResizeMode.HORIZONTAL)
		{
			delta.x = Gdx.input.getDeltaX(pointer) * camera.size_ratio * camera.zoom;
			delta.y = Gdx.input.getDeltaY(pointer) * camera.size_ratio * camera.zoom;
		}
		else if(camera.resize_mode == ResizeMode.VERTICAL)
		{
			//TODO <ADD CODE HERE>
		}
		
		return delta;
	}
}
