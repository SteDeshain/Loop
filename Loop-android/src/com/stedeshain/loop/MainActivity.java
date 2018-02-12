package com.stedeshain.loop;

import java.io.InputStream;

import org.luaj.vm2.lib.ResourceFinder;

import android.os.Bundle;
import android.view.View;
import android.view.ViewConfiguration;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication implements ResourceFinder
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();

		initialize(new LoopMain("scripts/?.lua", this, ViewConfiguration.get(this).getScaledTouchSlop()), cfg);
		
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_FULLSCREEN | 
				View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
				View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();

		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_FULLSCREEN | 
				View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
				View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}

	@Override
	public InputStream findResource(String name)
	{
		try
		{
			return getAssets().open(name);
		}
		catch(java.io.IOException ioe)
		{
			return null;
		}
	}
}