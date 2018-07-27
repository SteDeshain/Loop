package com.stedeshain.loop.Engine.Utils;

import com.badlogic.gdx.math.Vector2;

public class IntVector2
{
	public int x;
	public int y;
	
	public IntVector2()
	{
		x = 0;
		y = 0;
	}
	public IntVector2(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public IntVector2(Vector2 vector)
	{
		this.x = (int)vector.x;
		this.y = (int)vector.y;
	}
	public IntVector2(IntVector2 vector)
	{
		this.x = vector.x;
		this.y = vector.y;
	}
	public void set(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public float dst(int x, int y)
	{
		int dx = this.x - x;
		int dy = this.y - y;
		return (float)Math.sqrt((float)(dx * dx + dy * dy));
	}
	
	public boolean equal(int x, int y)
	{
		return this.x == x && this.y == y;
	}
}
