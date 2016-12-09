package com.tentone.constellations.utils;

public class ThreadUtils
{
	public static boolean allFinished(Thread[] threads)
	{
		for(int i = 0; i < threads.length; i++)
		{
			if(threads[i].isAlive())
			{
				return false;
			}
		}
		
		return true;
	}
}
