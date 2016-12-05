package com.tentone.constellations.input;

public class Button
{
	public boolean pressed, just_pressed, just_released;
	
	//Constructor
	public Button()
	{
		this.pressed = false;
		this.just_pressed = false;
		this.just_released = false;
	}
	
	//Update button state
	public void update(boolean pressed)
	{
		this.just_pressed = false;
		this.just_released = false;

		if(pressed)
		{
			if(this.pressed ==  false)
			{
				this.just_pressed = true;
			}
			this.pressed = true;
		}
		else
		{
			if(this.pressed)
			{
				this.just_released = true;
			}
			this.pressed = false;
		}
	}
}
