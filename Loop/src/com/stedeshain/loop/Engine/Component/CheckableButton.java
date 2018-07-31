package com.stedeshain.loop.Engine.Component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.stedeshain.loop.Engine.Utils.Utils;
import com.sun.istack.internal.NotNull;

/**
 * SetSourceAnchor() must be called before setViewportAnchor(), otherwise GDX will throw a NullPointerException
 * @author SteDeshain
 *
 */
public abstract class CheckableButton extends Button
{
	public static final int DEFAULT_LINE_WIDTH = 2;
	public static final Color DEFAULT_LINE_COLOR = Color.BLACK;
	
	private boolean mChecked = false;
	private boolean mLastChecked = false;
	private OnCheckListener mOnCheckListener = null;
	private OnDecheckListener mOnDecheckListener = null;
	
	// picture mode
	//TODO draw a 9-patch picture over the original picture to show it's checked.
	//for now, it can only draw a box over it, which performs very bad.
	
	private Color mLineColor = DEFAULT_LINE_COLOR;
	/**
	 * in pixel
	 */
	private int mLineWidth = DEFAULT_LINE_WIDTH;
	
	private ShapeRenderer mShapeRenderer;
	
	// text mode
	/**
	 * if this button is on the text mode, those texture will be shown at the left of the text label. 
	 * mHeadPicture[0] will be shown when mChecked == false, as well as the default picture to show. 
	 * mHeadPicture[1] will be shown when mChecked == true.
	 * Those two picture should have the same size.
	 */
	private TextureRegion[] mHeadPicture = new TextureRegion[2];
	/**
	 * inner use
	 */
	private Vector2 mPicFinalSize = new Vector2();
	/**
	 * padding between the head picture and the text label, in pixel.
	 */
	private int mPadding = Utils.getDefaultChckBtnPicTxtPadding();
	
	/**
	 * Picture mode. Show a box around the picture to tell whether checked
	 * @param textureRegion
	 */
	public CheckableButton(@NotNull TextureRegion textureRegion)
	{
		super(textureRegion);
		mShapeRenderer = new ShapeRenderer();
		mShapeRenderer.setAutoShapeType(true);
		//mLabel == null
	}
	/**
	 * Text mode. Show an additional little round or square white block to tell whether checked,
	 * at the head of the text.
	 * The head picture's height will be equal with the text Label's height, 
	 * and its width will be scaled to a right value by the original aspect ratio.
	 * @param text
	 * @param uncheckedPic
	 * @param checkedPic
	 */
	public CheckableButton(@NotNull String text, @NotNull TextureRegion uncheckedPic, @NotNull TextureRegion checkedPic)
	{
		this(text, Utils.getDefaultFont(), uncheckedPic, checkedPic);
	}
	public CheckableButton(@NotNull String text, @NotNull BitmapFont font, 
			@NotNull TextureRegion uncheckedPic, @NotNull TextureRegion checkedPic)
	{
		super(text, font);
		mHeadPicture[0] = uncheckedPic;
		mHeadPicture[1] = checkedPic;
		//mLabel != null
	}
	
	/**
	 * Will do nothing
	 */
	@Override
	public void setPressedTextureRegion(TextureRegion pressedRegion)
	{
		//do nothing
	}
	/**
	 * Will do nothing
	 */
	@Override
	public void setPressedColor(Color pressedColor)
	{
		//do nothing
	}
	
	public void setLineColor(@NotNull Color color)
	{
		mLineColor = color;
	}
	public Color getLineColor()
	{
		return mLineColor;
	}
	public void setLineWidth(int lineWidth)
	{
		mLineWidth = lineWidth;
		if(mLineWidth < 1)
			mLineWidth = 1;
	}
	public int getLineWidth()
	{
		return mLineWidth;
	}

	@Override
	public void setPosition(Vector2 position)
	{
		super.setPosition(position);
		if(mLabel != null)	// text mode need to reset the Label's position
		{
			calculatePicFinalSize();
			mLabel.setPosition(new Vector2(position.x + mPadding + mPicFinalSize.x, position.y));
		}
	}

	private boolean hasGotPicFinalSize = false;
	private void calculatePicFinalSize()
	{
		if(!hasGotPicFinalSize)
		{
			Vector2 labelSize = mLabel.getSize();
			float picFinalHeight = labelSize.y;
			float picFinalWidth = (float)mHeadPicture[0].getRegionWidth() / (float)mHeadPicture[0].getRegionHeight()
					* picFinalHeight;
			mPicFinalSize.set(picFinalWidth, picFinalHeight);
			hasGotPicFinalSize = true;
		}
	}
	
	/**
	 * If on <b>picture mode</b>, will set the picture's size. <p>
	 * If on <b>text mode</b>, will only set the text Label's width to size.x, and size.y will be ignored.
	 */
	@Override
	public void setSize(Vector2 size)
	{
		super.setSize(size);
	}

	@Override
	public Vector2 getSize()
	{
		if(mLabel != null)	// text mode
		{
			calculatePicFinalSize();
			Vector2 labelSize = mLabel.getSize();
			return new Vector2(mPicFinalSize.x + mPadding + labelSize.x, labelSize.y);
		}
		else	// picture mode
		{
			return super.getSize();
		}
	}
	
	@Override
	public Vector2 getLeftBottomPosition()
	{
		if(mLabel != null)	// text mode
		{
			calculatePicFinalSize();
			Vector2 labelLeftBottom = mLabel.getLeftBottomPosition();
			return new Vector2(labelLeftBottom.x - mPadding - mPicFinalSize.x, labelLeftBottom.y);
		}
		else	// picture mode
		{
			return super.getLeftBottomPosition();
		}
	}

	@Override
	public void setViewportAnchor(Vector2 viewportAnchor)
	{
		super.setViewportAnchor(viewportAnchor);
	}
	
	@Override
	public void setSourceAnchor(Vector2 sourceAnchor)
	{
		if(mLabel != null)	// text mode
		{
			calculatePicFinalSize();
			Vector2 finalLabelAnchor = new Vector2();
			finalLabelAnchor.y = sourceAnchor.y;
			Vector2 labelSize = mLabel.getSize();
			
			float totalWidth = mPicFinalSize.x + labelSize.x;
			float picRatio = mPicFinalSize.x / totalWidth;
			float labelRatio = labelSize.x / totalWidth;
			finalLabelAnchor.x = (sourceAnchor.x - picRatio) / labelRatio;
			
			mLabel.setSourceAnchor(finalLabelAnchor);
		}
		else	// picture mode
		{
			super.setSourceAnchor(sourceAnchor);
		}
	}
	
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		
		if(mLastChecked && !mChecked)
		{
			onDecheck();
		}
		else if(!mLastChecked && mChecked)
		{
			onCheck();
		}
	}
	
	@Override
	public void updateLastFrameValue()
	{
		super.updateLastFrameValue();
		mLastChecked = mChecked;
	}
	
	@Override
	public void draw(SpriteBatch batch)
	{
		//let the father class decide how to draw
		super.draw(batch);
		
		if(mLabel != null)	// text mode
		{
			//must draw head picture, father class won't draw it
			TextureRegion curRegion = mChecked ? mHeadPicture[1] : mHeadPicture[0];
			calculatePicFinalSize();
			
			Vector2 labelLeftTop = mLabel.getLeftTopPosition();
			batch.draw(curRegion, labelLeftTop.x - mPadding - mPicFinalSize.x, labelLeftTop.y - mPicFinalSize.y,
					0f, 0f, mPicFinalSize.x, mPicFinalSize.y, 1f, 1f, 0f);
		}
		else	// picture mode
		{
			//must draw box over picture, father class won't draw it
			//TODO
//			if(hasNinePatch)
//			{...} else ...
			if(mChecked)
			{
				batch.end();
				mShapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
				mShapeRenderer.setColor(mLineColor);
				mShapeRenderer.begin(ShapeType.Filled);
				
				Vector2 leftBottom = getLeftBottomPosition();
				Vector2 size = getSize();
				mShapeRenderer.rectLine(leftBottom.x, leftBottom.y, leftBottom.x + size.x, leftBottom.y, mLineWidth);
				mShapeRenderer.rectLine(leftBottom.x, leftBottom.y, leftBottom.x, leftBottom.y + size.y, mLineWidth);
				mShapeRenderer.rectLine(leftBottom.x + size.x, leftBottom.y + size.y, leftBottom.x + size.x, leftBottom.y, mLineWidth);
				mShapeRenderer.rectLine(leftBottom.x + size.x, leftBottom.y + size.y, leftBottom.x, leftBottom.y + size.y, mLineWidth);
				
				mShapeRenderer.end();
				batch.begin();
			}
		}
	}
	
	/**
	 * Override it in sub-classes
	 */
	@Override
	protected void onClick()
	{
		super.onClick();
	}
	
	protected void onCheck()
	{
		if(mOnCheckListener != null)
			mOnCheckListener.onCheck();
	}
	protected void onDecheck()
	{
		if(mOnDecheckListener != null)
			mOnDecheckListener.onDecheck();
	}
	
	public void check()
	{
		mChecked = true;
	}
	public void decheck()
	{
		mChecked = false;
	}
	public void toggleCheck()
	{
		mChecked = !mChecked;
	}
	public boolean isChecked()
	{
		return mChecked;
	}
	
	public void setOnCheckListener(OnCheckListener listener)
	{
		mOnCheckListener = listener;
	}
	public void setOnDecheckListener(OnDecheckListener listener)
	{
		mOnDecheckListener = listener;
	}

	@Override
	public void dispose()
	{
		if(isOwnAssets())
		{
			mHeadPicture[0].getTexture().dispose();
			mHeadPicture[1].getTexture().dispose();
		}
		
		if(mShapeRenderer != null)
			mShapeRenderer.dispose();
		
		super.dispose();
	}
	
	public interface OnCheckListener
	{
		public void onCheck();
	}
	public interface OnDecheckListener
	{
		public void onDecheck();
	}
}
