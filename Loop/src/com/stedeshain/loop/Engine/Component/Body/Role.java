package com.stedeshain.loop.Engine.Component.Body;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.stedeshain.loop.Engine.Utils.Constants;
import com.stedeshain.loop.Engine.Utils.ContactAdapter;

public class Role extends BoxBody
{	
	private float mGroundDetectorHeight = 0;
	private float mGroundDetectorVerticalOffset = 0;
	private float mGroundDetectorHorizontalShrink = 0;
	
	private Fixture mGroundDetector = null;
	
	private boolean mGrounded = false;
	/**
	 * Only when all terrain fixtures underneath this Role were gone, can we say this Role is not grounded
	 */
	private Array<Fixture> mUnderneathFixtures = new Array<Fixture>();
	
	public Role(Vector2 position, Vector2 size, float horizontalMargin, float verticalMargin,
			TextureRegion textureRegion)
	{
		super(position, size, horizontalMargin, verticalMargin, textureRegion);
	}
	
	@Override
	public void create()
	{
		super.create();
		
		if(getWorld() == null)
			return;

		PolygonShape detectorShape = new PolygonShape();
		Vector2 size = getSize();
		float bodyHalfWidth = size.x / 2 - getHorizontalBodyMargin();
		Vector2 origin = getOrigin();
		detectorShape.setAsBox(bodyHalfWidth - mGroundDetectorHorizontalShrink, mGroundDetectorHeight / 2,
				new Vector2(bodyHalfWidth - origin.x + getHorizontalBodyMargin(), 
						-origin.y + getHorizontalBodyMargin() - mGroundDetectorHeight / 2 + mGroundDetectorVerticalOffset), 
				0f);
		
		FixtureDef detectorFixtureDef = new FixtureDef();
		detectorFixtureDef.shape = detectorShape;
		detectorFixtureDef.density = 0;
		detectorFixtureDef.filter.categoryBits = mCategoryBits;
		detectorFixtureDef.filter.maskBits = mMaskBits;
		detectorFixtureDef.filter.groupIndex = mGroupIndex;
		detectorFixtureDef.isSensor = true;
		mGroundDetector = getBody().createFixture(detectorFixtureDef);
		
		detectorShape.dispose();
		
		//ground detection contact listener
		registerGroundDetection();
	}

	//the second and the last place telling whether a Role should be grounded
	@Override
	protected void onBeginContact(Fixture anotherFixture, Contact contact)
	{
		super.onBeginContact(anotherFixture, contact);
		
		Object anotherBody = anotherFixture.getBody().getUserData();
		if(anotherBody instanceof OneSidedPlatform)
		{
			if(((OneSidedPlatform)anotherBody).shouldCollide(contact.getWorldManifold().getNormal()))
			{
				mGrounded = true;
				mUnderneathFixtures.add(anotherFixture);
			}
		}
	}
	@Override
	protected void onEndContact(Fixture anotherFixture, Contact contact)
	{
		super.onEndContact(anotherFixture, contact);

		Object anotherBody = anotherFixture.getBody().getUserData();
		if(anotherBody instanceof OneSidedPlatform)
		{
			mUnderneathFixtures.removeValue(anotherFixture, true);
			if(mUnderneathFixtures.size <= 0)
				mGrounded = false;
		}
	}

	//the first place telling whether a Role should be grounded
	private void registerGroundDetection()
	{
		getMotherScene().addContactListener(new ContactAdapter()
		{
			@Override
			public void beginContact(Contact contact)
			{
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();
				Object bodyA = fixtureA.getBody().getUserData();
				Object bodyB = fixtureB.getBody().getUserData();
				if(fixtureA != mGroundDetector && fixtureB != mGroundDetector)
					return;
				
				AbstractBody anotherBody = (AbstractBody)(fixtureA == mGroundDetector ? bodyB : bodyA);
				Fixture anotherFixture = fixtureA == mGroundDetector ? fixtureB : fixtureA;

				if(anotherBody.matchTag(Constants.TERRAIN_TAG))
				{
					//Must treat the OneSidedPlatform differently
					if(!(anotherBody instanceof OneSidedPlatform))
					{
						mGrounded = true;
						mUnderneathFixtures.add(anotherFixture);
					}
				}
			}

			@Override
			public void endContact(Contact contact)
			{
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();
				Object bodyA = fixtureA.getBody().getUserData();
				Object bodyB = fixtureB.getBody().getUserData();
				if(fixtureA != mGroundDetector && fixtureB != mGroundDetector)
					return;

				AbstractBody anotherBody = (AbstractBody)(fixtureA == mGroundDetector ? bodyB : bodyA);
				Fixture anotherFixture = fixtureA == mGroundDetector ? fixtureB : fixtureA;

				if(anotherBody.matchTag(Constants.TERRAIN_TAG))
				{
					mUnderneathFixtures.removeValue(anotherFixture, true);
					if(mUnderneathFixtures.size <= 0)
						mGrounded = false;
				}
			}
		});
	}

	public Fixture getGroundDetector()
	{
		return mGroundDetector;
	}

	public float getGroundDetectorHeight()
	{
		return mGroundDetectorHeight;
	}
	/**
	 * Must call it before added to a Scene
	 * @param groundDetectorHeight
	 */
	public void setGroundDetectorHeight(float groundDetectorHeight)
	{
		mGroundDetectorHeight = groundDetectorHeight;
	}

	public float getGroundDetectorVerticalOffset()
	{
		return mGroundDetectorVerticalOffset;
	}
	/**
	 * Must call it before added to a {@link com.stedeshain.loop.Engine.Scene.Scene Scene}
	 * @param groundDetectorVerticalOffset
	 */
	public void setGroundDetectorVerticalOffset(float groundDetectorVerticalOffset)
	{
		mGroundDetectorVerticalOffset = groundDetectorVerticalOffset;
	}
	
	public float getGroundDetectorHorizontalShrink()
	{
		return mGroundDetectorHorizontalShrink;
	}
	/**
	 * Must call it before added to a {@link com.stedeshain.loop.Engine.Scene.Scene Scene}
	 * @param groundDetectorHorizontalShrink
	 */
	public void setGroundDetectorHorizontalShrink(float groundDetectorHorizontalShrink)
	{
		mGroundDetectorHorizontalShrink = groundDetectorHorizontalShrink;
	}

	/**
	 * check whether this {@link Role} stands on an {@link AbstractBody} whose tag equals 
	 * {@link com.stedeshain.loop.Engine.Utils.Constants#TERRAIN_TAG TERRAIN_TAG}
	 * @return whether this Role is grounded
	 */
	public boolean isGrounded()
	{
		if(mBody == null)
			return false;
		
		return mGrounded;
	}
	
	/**
	 * Normally, a Role will always stand on several terrain AbstractBodies,
	 * and this method will only find the latest terrain on which this Role stands.
	 * @return
	 */
	public AbstractBody getStandingTerrain()
	{
		if(!isGrounded())
			return null;
		
		//to this line, mUnderneathFixtures must have at least one element, so no need to check the argument validation
		Fixture fixture = mUnderneathFixtures.get(mUnderneathFixtures.size - 1);
		return (AbstractBody)(fixture.getBody().getUserData());
	}
}
