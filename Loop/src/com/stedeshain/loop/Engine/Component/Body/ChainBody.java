package com.stedeshain.loop.Engine.Component.Body;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Cannot inherit this class because: see {@link Role#registerGroundDetection}
 * @author Administrator
 *
 */
public final class ChainBody extends AbstractBody
{
	private boolean mIsLoop = false;
	private List<Vector2> mPoints = new ArrayList<Vector2>();
	
	public ChainBody(Vector2 position, Vector2 size, TextureRegion textureRegion)
	{
		super(position, textureRegion);
		setPosition(position);
		setSize(size);
	}

	@Override
	public void create()
	{
		World world = getWorld();
		if(world == null)
			return;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = mBodyTypeDef;
		bodyDef.angle = mAngleDef;
		bodyDef.fixedRotation = mFixedRotationDef;
		bodyDef.position.set(getPosition());
		bodyDef.bullet = mBulletDef;
		mBody = world.createBody(bodyDef);
		
		if(mPoints.size() > 0)
		{
			ChainShape chainShape = new ChainShape();
			if(mIsLoop)
			{
				chainShape.createLoop(mPoints.toArray(new Vector2[0]));
			}
			else
			{
				chainShape.createChain(mPoints.toArray(new Vector2[0]));
			}

			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = chainShape;
			fixtureDef.density = mDensityDef;
			fixtureDef.friction = mFrictionDef;
			fixtureDef.restitution = mRestitutionDef;
			fixtureDef.filter.categoryBits = mCategoryBits;
			fixtureDef.filter.maskBits = mMaskBits;
			fixtureDef.filter.groupIndex = mGroupIndex;
			fixtureDef.isSensor = mIsSensor;
			mMainFixture = mBody.createFixture(fixtureDef);
			
			chainShape.dispose();
		}
		
		registerGeneralContactEvent();
		
		registerUserData();
	}

	@Override
	public void updatePhysics()
	{
		super.updatePhysics();

		if(mBody == null)
			return;
		final Vector2 position = mBody.getPosition();
		setPosition(position.x - getOrigin().x, position.y - getOrigin().y);
		setRotation(mBody.getAngle() * MathUtils.radiansToDegrees);
	}
	
	public boolean isLoop()
	{
		return mIsLoop;
	}
	public void setLoop(boolean isLoop)
	{
		mIsLoop = isLoop;
	}
	
	public void addPoint(Vector2 point)
	{
		mPoints.add(point);
	}
	public void addPoint(float x, float y)
	{
		addPoint(new Vector2(x, y));
	}
}
