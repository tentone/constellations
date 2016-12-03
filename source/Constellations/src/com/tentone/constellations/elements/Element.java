package com.tentone.constellations.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class Element
{
	//Generate random Color
	public static Color generateColor()
	{
		return new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1.0f);
	}
	
	//Generate random ID
	public static int generateID()
	{
		return MathUtils.random(0, 65535);
	}
}
