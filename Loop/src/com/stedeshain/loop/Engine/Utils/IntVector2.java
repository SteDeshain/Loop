package com.stedeshain.loop.Engine.Utils;

public class IntVector2
{
	int x;
	int y;
	
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
