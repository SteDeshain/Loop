package com.stedeshain.loop.Engine.Component.Body;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.ObjectSet;
import com.stedeshain.loop.Engine.Utils.Utils;

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
	private ObjectSet<Fixture> mNonCollisionFixtures = new ObjectSet<Fixture>();
	/**
	 * in degree
	 */
	private float mCollisionAngle = 45f;
	
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
		
		//TODO need to apply StandingSide
		Vector2 normal = contact.getWorldManifold().getNormal();
		float angle = mBody.getAngle();
		Vector2 normalAlignedPlatform = Utils.rotateAxis(normal, angle);
		float normalAngle = Utils.getDegreeAngle(normalAlignedPlatform);
		if(normalAngle >= 0 || (normalAngle >= -mCollisionAngle || normalAngle <= mCollisionAngle - 180))
		{
			mNonCollisionFixtures.add(anotherFixture);
		}
	}
	
	@Override
	protected void onEndContact(Fixture anotherFixture, Contact contact)
	{
		super.onEndContact(anotherFixture, contact);
		
		mNonCollisionFixtures.remove(anotherFixture);
	}
	
	@Override
	protected void onPreSolveContact(Fixture anotherFixture, Contact contact, Manifold oldManifold)
	{
		super.onPreSolveContact(anotherFixture, contact, oldManifold);
		
		if(mNonCollisionFixtures.contains(anotherFixture))
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
	
	public float getCollisionAngle()
	{
		return mCollisionAngle;
	}
	public void setCollisionAngle(float collisionAngle)
	{
		mCollisionAngle = collisionAngle;
	}

	public void letThrougn(Fixture fixture)
	{
		mNonCollisionFixtures.add(fixture);
	}
}
