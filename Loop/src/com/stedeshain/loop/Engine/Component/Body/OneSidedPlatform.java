package com.stedeshain.loop.Engine.Component.Body;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

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
	private Array<Fixture> mNonCollisionFixtures = new Array<Fixture>();
	
	public OneSidedPlatform(Vector2 position, Vector2 size, float horizontalMargin, float verticalMargin,
			TextureRegion textureRegion)
	{
		super(position, size, horizontalMargin, verticalMargin, textureRegion);
	}
	
	@Override
	protected void onBeginContact(Fixture anotherFixture, Contact contact)
	{
		super.onBeginContact(anotherFixture, contact);

		if(anotherFixture.isSensor())
			return;
		
		//TODO need to apply rotation angle
		if(contact.getWorldManifold().getNormal().y >= 0)
		{
			if(!mNonCollisionFixtures.contains(anotherFixture, true))
				mNonCollisionFixtures.add(anotherFixture);
		}
	}
	
	@Override
	protected void onEndContact(Fixture anotherFixture, Contact contact)
	{
		super.onEndContact(anotherFixture, contact);
		
		mNonCollisionFixtures.removeValue(anotherFixture, true);
	}
	
	@Override
	protected void onPreSolveContact(Fixture anotherFixture, Contact contact, Manifold oldManifold)
	{
		super.onPreSolveContact(anotherFixture, contact, oldManifold);
		
		if(mNonCollisionFixtures.contains(anotherFixture, true))
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
	
	public void letThrougn(Fixture fixture)
	{
		if(!mNonCollisionFixtures.contains(fixture, true))
			mNonCollisionFixtures.add(fixture);
	}
}
