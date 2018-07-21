package com.stedeshain.loop.Engine.Component;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.stedeshain.loop.Engine.Scene.Layer;
import com.stedeshain.loop.Engine.Utils.Constants;
import com.stedeshain.loop.Engine.Utils.Deepable;
import com.sun.istack.internal.NotNull;

public class DrawableComponent extends SceneComponent implements Disposable, Deepable
{
	public static final long BOTTOM_DEPTH = Long.MIN_VALUE;
	public static final long TOP_DEPTH = Long.MAX_VALUE;

	private Layer mMotherLayer;
	
	private boolean mVisible = true;
	/**
	 * draw order in the MotherScene
	 */
	private long mDepth;
	
	private Vector2 mPosition = new Vector2();
	private Vector2 mSize = new Vector2();
	private Vector2 mOrigin = new Vector2();
	private Vector2 mScale = new Vector2(1f, 1f);
	private float mRotation = 0f;
	
	private TextureRegion mTextureRegion;
	private boolean mOwnAssets = false;
	
	public DrawableComponent()
	{
		this(null);
		
		//TODO necessary ?
		//mVisible = false;
	}
	public DrawableComponent(TextureRegion textureRegion)
	{
		super();

		setTextureRegion(textureRegion);
	}

	@Override
	public void update(float deltaTime) {}
	
	public void draw(SpriteBatch batch)
	{
		if(mTextureRegion == null)
			return;
		
		batch.draw(mTextureRegion, 
				mPosition.x, mPosition.y, 
				mOrigin.x, mOrigin.y, 
				mSize.x, mSize.y, 
				mScale.x, mScale.y, 
				mRotation);
	}
	
	public boolean isVisible()
	{
		return mVisible;
	}
	
	public void setVisible(boolean visible)
	{
		mVisible = visible;
	}
	public void hide()
	{
		setVisible(false);
	}
	public void show()
	{
		setVisible(true);
	}

	public Vector2 getPosition()
	{
		return mPosition;
	}
	public void setPosition(@NotNull Vector2 position)
	{
		mPosition.set(position);
	}
	public void setPosition(float x, float y)
	{
		setPosition(new Vector2(x, y));
	}

	public Vector2 getSize()
	{
		return mSize;
	}
	public void setSize(@NotNull Vector2 size)
	{
		mSize = size;
		if(mSize.x < 0) mSize.x = 0;
		if(mSize.y < 0) mSize.y = 0;
		
		if(!mOrigin.isZero())
		{
			mOrigin.x = MathUtils.clamp(mOrigin.x, 0f, mSize.x);
			mOrigin.y = MathUtils.clamp(mOrigin.y, 0f, mSize.y);
		}
	}
	public void setSize(float width, float height)
	{
		setSize(new Vector2(width, height));
	}
	/**
	 * set the size.y = height, and calculate size.x by
	 * using mTextureReigon's size, and the final size has
	 * the same ratio(w/h) with the mTextureRegion.
	 * @param height
	 */
	public void setHeightToFitRegion(float height)
	{
		if(mTextureRegion == null)
		{
			setSize(0f, height);
		}
		else
		{
			setSize((float)mTextureRegion.getRegionWidth() / (float)mTextureRegion.getRegionHeight() * height, height);
		}
	}
	/**
	 * see {@link #setHeightToFitRegion(float)}
	 * @param width
	 */
	public void setWidthToFitRegion(float width)
	{
		if(mTextureRegion == null)
		{
			setSize(width, 0f);
		}
		else
		{
			setSize(width, (float)mTextureRegion.getRegionHeight() / (float)mTextureRegion.getRegionWidth() * width);
		}
	}

	public Vector2 getOrigin()
	{
		return mOrigin;
	}

	/**
	 * can only be called after the Size were settled correctly
	 * @param origin
	 */
	public void setOrigin(@NotNull Vector2 origin)
	{
		mOrigin.x = MathUtils.clamp(origin.x, 0f, mSize.x);
		mOrigin.y = MathUtils.clamp(origin.y, 0f, mSize.y);
	}
	/**
	 * can only be called after the Size were settled correctly
	 * @param x
	 * @param y
	 */
	public void setOrigin(float x, float y)
	{
		setOrigin(new Vector2(x, y));
	}
	/**
	 * can only be called after the Size were settled correctly
	 * @param factor
	 */
	public void setOriginFactor(@NotNull Vector2 factor)
	{
		setOrigin(new Vector2(factor.x * mSize.x, factor.y * mSize.y));
	}
	/**
	 * can only be called after the Size were settled correctly
	 * @param xFactor
	 * @param yFactor
	 */
	public void setOriginFactor(float xFactor, float yFactor)
	{
		setOrigin(new Vector2(xFactor * mSize.x, yFactor * mSize.y));
	}
	/**
	 * can only be called after the Size were settled correctly
	 */
	public void setCenterOrigin()
	{
		setOriginFactor(0.5f, 0.5f);
	}

	public long getDepth()
	{
		return mDepth;
	}
	public void setDepth(long depth)
	{
		mDepth = depth;
	}
	public void setDepthToBottom()
	{
		setDepth(DrawableComponent.BOTTOM_DEPTH);
	}
	public void setDepthToTop()
	{
		setDepth(DrawableComponent.TOP_DEPTH);
	}

	public Vector2 getScale()
	{
		return mScale;
	}
	public void setScale(@NotNull Vector2 scale)
	{
		this.mScale.set(scale);
	}
	public void setScale(float xScale, float yScale)
	{
		setScale(new Vector2(xScale, yScale));
	}

	public float getRotation()
	{
		return mRotation;
	}
	public void setRotation(float rotation)
	{
		this.mRotation = rotation % 360;
	}
	
	public TextureRegion getTextureRegion()
	{
		return mTextureRegion;
	}
	public void setTextureRegion(TextureRegion textureRegion)
	{
		mTextureRegion = textureRegion;
		if(mTextureRegion != null)
			mTextureRegion.getTexture().setFilter(Constants.TEXTURE_MIN_FILTER,
					Constants.TEXTURE_MAG_FILTER);
	}

	public Layer getMotherLayer()
	{
		return mMotherLayer;
	}
	public void setMotherLayer(Layer motherLayer)
	{
		mMotherLayer = motherLayer;
	}
	
	public boolean isOwnAssets()
	{
		return mOwnAssets;
	}
	/**
	 * Set weather this component owns its all assets that need disposing.
	 * For example, if a DrawableComponent's mTextureRegion.getTexture() is not from AssetsHelper,
	 * and only is used in this single one Component, we can tell this DrawableComponent
	 * that it does own its all assets and can dispose its assets when it's disposed itself.
	 * User must set it very CAREFULLY cause it may cause some fatal bug when setted wrong
	 * @param ownAssets : weather this component's all assets are not from AssetsHelper,
	 * and doesn't share any of its assets with anyone else.
	 */
	public void setOwnAssets(boolean ownAssets)
	{
		mOwnAssets = ownAssets;
	}
	@Override
	public void dispose()
	{
		if(mOwnAssets)
		{
			if(mTextureRegion != null)
				mTextureRegion.getTexture().dispose();
		}
	}

}
