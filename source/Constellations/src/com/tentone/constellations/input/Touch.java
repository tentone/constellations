package com.tentone.constellations.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.tentone.constellations.graphics.OrthographicCamera;

public class Touch
{
	//Position and delta
	public Pointer[] pointers;
	
	//Camera
	private OrthographicCamera camera;
	
	//Input Constructor
	public Touch(OrthographicCamera camera)
	{
		this.camera = camera;

		this.pointers = new Pointer[2];
		for(int i = 0; i < 2; i++)
		{
			this.pointers[i] = new Pointer();
		}
	}
	
	//Update input status
	public void update()
	{
		float ratio = camera.size_ratio * camera.zoom;
		
		//if(camera.resize_mode == ResizeMode.HORIZONTAL)
		float offset_x = camera.position.x - (camera.size / 2f * camera.aspect_ratio * camera.zoom);
		float offset_y = (camera.size / 2f * camera.zoom) + camera.position.y;
		
		for(int i = 0; i < pointers.length; i++)
		{
			if(Gdx.input.isTouched(i))
			{
				pointers[i].update(true);
				
				pointers[i].position.x = Gdx.input.getX(i) * ratio + offset_x;
				pointers[i].position.y = -Gdx.input.getY(i) * ratio + offset_y;
				
				pointers[i].delta.x = Gdx.input.getDeltaX(i) * ratio;
				pointers[i].delta.y = Gdx.input.getDeltaY(i) * ratio;
			}
			else
			{
				pointers[i].update(false);
			}
		}
	}
	
	//Check is a pointer is pressed
	public boolean justReleased(int pointer)
	{
		return pointers[pointer].just_released;
	}
	
	//Check is a pointer is pressed
	public boolean justPressed(int pointer)
	{
		return pointers[pointer].just_pressed;
	}
	
	//Check is a pointer is pressed
	public boolean isPressed(int pointer)
	{
		return pointers[pointer].pressed;
	}
	
	//Update input
	public Vector2 getPosition(int pointer)
	{
		return pointers[pointer].position;
	}
	
	//Get pointer delta
	public Vector2 getDelta(int pointer)
	{
		return pointers[pointer].delta;
	}
}
