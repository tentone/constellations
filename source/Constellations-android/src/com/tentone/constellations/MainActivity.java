package com.tentone.constellations;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.hideStatusBar = true;
		cfg.useImmersiveMode = true;
		
		initialize(new ConstellationsMain(), cfg);
	}
}