package com.stedeshain.loop.Engine.Component.Body;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Only collide at one side of this body
 * @author SteDeshain
 */
public class OneSidedPlatform extends BoxBody
{
	public enum StandingSide
	{
		Up, Right, Down, Left,
	}

	private StandingSide mSandingSide = StandingSide.Up;
	
	public OneSidedPlatform(Vector2 position, Vector2 size, float horizontalMargin, float verticalMargin,
			TextureRegion textureRegion)
	{
		super(position, size, horizontalMargin, verticalMargin, textureRegion);
	}
	
	@Override
	protected void onPreSolveContact(Fixture anotherFixture, Contact contact, Manifold oldManifold)
	{
		super.onPreSolveContact(anotherFixture, contact, oldManifold);
		
		//TODO
		Vector2 normal = contact.getWorldManifold().getNormal();
		//TODO still need to calculate overlapped area size to cancel contact
		if(normal.y >= 0f || anotherFixture.getBody().getLinearVelocity().y > 0)
		{
			contact.setEnabled(false);
		}
	}

	public StandingSide getSandingSide()
	{
		return mSandingSide;
	}
	public void setSandingSide(StandingSide sandingSide)
	{
		mSandingSide = sandingSide;
	}
}
