package com.stedeshain.loop.Engine.Component.Body;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.stedeshain.loop.Engine.Component.DrawableComponent;
import com.stedeshain.loop.Engine.Utils.Utils;

public abstract class AbstractBody extends DrawableComponent
{
	public static final BodyType DEFAULT_TYPE = BodyType.StaticBody;
	public static final float DEFAULT_ANGLE = 0f;
	public static final float DEFAULT_DENSITY = 0.5f;
	public static final float DEFAULT_FRICTION = 0.4f;
	public static final float DEFAULT_RESTITUTION = 0.6f;

	protected Body mBody = null;
	
	protected BodyType mBodyTypeDef = DEFAULT_TYPE;
	protected float mAngleDef = DEFAULT_ANGLE;
	protected float mDensityDef = DEFAULT_DENSITY;
	protected float mFrictionDef = DEFAULT_FRICTION;
	protected float mRestitutionDef = DEFAULT_RESTITUTION;
	protected boolean mFixedRotationDef = false;
	protected boolean mBulletDef = false;

	public AbstractBody(Vector2 position, TextureRegion textureRegion)
	{
		super(textureRegion);
	}
	
	public Body getBody()
	{
		return mBody;
	}
	
	protected World getWorld()
	{
		return getMotherScene().getPhysicsWorld();
	}
	
	/**
	 * Must be called before added into a Scene
	 * @param type
	 */
	public void setBodyTypeDef(BodyType type)
	{
		mBodyTypeDef = type;
	}
	/**
	 * Must be called before added into a Scene
	 * @param angleDef : in radians
	 */
	public void setAngleDef(float angleDef)
	{
		mAngleDef = angleDef;
	}
	/**
	 * Must be called before added into a Scene
	 * @param degreeDef : in degree
	 */
	public void setAngleDegreeDef(float degreeDef)
	{
		mAngleDef = Utils.toRadians(degreeDef);
	}
	/**
	 * Must be called before added into a Scene
	 * @param densityDef
	 */
	public void setDensityDef(float densityDef)
	{
		mDensityDef = densityDef;
	}
	/**
	 * Must be called before added into a Scene
	 * @param frictionDef
	 */
	public void setFrictionDef(float frictionDef)
	{
		mFrictionDef = frictionDef;
	}
	/**
	 * Must be called before added into a Scene
	 * @param restitutionDef
	 */
	public void setRestitutionDef(float restitutionDef)
	{
		mRestitutionDef = restitutionDef;
	}
	/**
	 * Must be called before added into a Scene
	 * @param fixedRotationDef
	 */
	public void setFixedRotationDef(boolean fixedRotationDef)
	{
		mFixedRotationDef = fixedRotationDef;
	}
	/**
	 * Must be called before added into a Scene
	 * @param bulletDef
	 */
	public void setBulletDef(boolean bulletDef)
	{
		mBulletDef = bulletDef;
	}
	
	@Override
	public void departFromScene()
	{
		super.departFromScene();

		getWorld().destroyBody(mBody);
		mBody = null;
	}
}
