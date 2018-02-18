package com.stedeshain.loop.Engine.Component.Body;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;

public class Role extends BoxBody
{
	private Fixture mGroundDetector = null;
	
	public Role(Vector2 position, Vector2 size, float horizontalMargin, float verticalMargin,
			TextureRegion textureRegion)
	{
		super(position, size, horizontalMargin, verticalMargin, textureRegion);
	}
	
	@Override
	public void create()
	{
		super.create();
		
		Vector2 size = getSize();
		float bodyWidth = size.x - getHorizontalBodyMargin() * 2;
		float bodyHeight = size.y - getVerticalBodyMargin() * 2;
	}

	public Fixture getGroundDetector()
	{
		return mGroundDetector;
	}
}
