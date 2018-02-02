package com.stedeshain.loop.Engine.Component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.stedeshain.loop.Engine.Utils.Utils;
import com.sun.istack.internal.NotNull;

/**
 * For Label, some properties like rotation, scaling, origin, and size.h don't make any sense
 * @author SteDeshain
 */
public class Label extends UIComponent
{
	private String mText;
	private BitmapFont mFont;
	private Color mColor;
	private GlyphLayout mGlyphLayout;
	
	private Vector2 mLeftTopPosition = new Vector2();
	
	private int mAlign = Align.left;
	private boolean mWrap = false;
	private String mTruncate = null;
	
	public Label(String text)
	{
		this(null, text);
	}
	public Label(BitmapFont font, String text)
	{
		super();
		
		if(font == null)
			setFont(Utils.getDefaultFont());	//default 15pt Arial font
		else
			setFont(font);
		mGlyphLayout = new GlyphLayout();
		
		mText = text;
	}
	
	@Override
	public void draw(SpriteBatch batch)
	{
		calculateLeftTop();
		
		mFont.draw(batch, mGlyphLayout, mLeftTopPosition.x, mLeftTopPosition.y);
	}
	
	/**
	 * package access
	 * @param batch
	 * @param left
	 * @param top
	 */
	void draw(SpriteBatch batch, float left, float top)
	{
		mGlyphLayout.setText(mFont, mText, 0, mText.length(), getColor(),
				super.getSize().x, mAlign, mWrap, mTruncate);
		mFont.draw(batch, mGlyphLayout, left, top);
	}
	
	private void calculateLeftTop()
	{
		if(mLeftBottomUpdated)
			return;
		
		mGlyphLayout.setText(mFont, mText, 0, mText.length(), getColor(),
				super.getSize().x, mAlign, mWrap, mTruncate);
		
		float offsetX, offsetY;
		
		//mGlyphLayout.height dosen't count in descent ...
		float height = mGlyphLayout.height - mFont.getDescent();
		if(getSourceAnchor() == null)
		{
			offsetX = getOrigin().x;
			offsetY = getOrigin().y;
		}
		else
		{
			offsetX = getSourceAnchor().x * mGlyphLayout.width;
			offsetY = getSourceAnchor().y * height;
		}
		
		if(getViewportAnchor() == null)
		{
			mLeftTopPosition.x = getPosition().x - offsetX;
			mLeftTopPosition.y = getPosition().y - offsetY + height;
		}
		else
		{
			float viewportWidth = getMotherScene().getUICamera().viewportWidth;
			float viewportHeight = getMotherScene().getUICamera().viewportHeight;
			mLeftTopPosition.x = getViewportAnchor().x * viewportWidth - viewportWidth / 2 - offsetX;
			mLeftTopPosition.y = getViewportAnchor().y * viewportHeight - viewportHeight / 2 - offsetY + height;
		}
		
		mLeftBottomUpdated = true;
	}

	public String getText()
	{
		return mText;
	}
	public void setText(@NotNull String text)
	{
		if(!(mText != null && mText.equals(text)))
		{
			mText = text;
			mLeftBottomUpdated = false;
		}
	}

	public BitmapFont getFont()
	{
		return mFont;
	}
	public void setFont(BitmapFont font)
	{
		mFont = font;
		mLeftBottomUpdated = false;
	}
	
	/**
	 * used to set glyphLayout's width, size.h will be ignored.
	 * glyphLayout's width is used when wrap or truncate were not false or null,
	 * if wrap is false and truncate is null, glyphLayout's width will be ignored too.
	 */
	@Override
	public void setSize(Vector2 size)
	{
		//to limit the value of origin.y == 0f forever
		super.setSize(new Vector2(size.x, 0f));
	}
	/**
	 * get the glyphLayout's size, which is exactly shown on screen.
	 */
	@Override
	public Vector2 getSize()
	{
		calculateLeftTop();
		Vector2 result = new Vector2();
		result.x = mGlyphLayout.width;
		result.y = mGlyphLayout.height - mFont.getDescent();
		return result;
	}

	@Override
	public void setPosition(Vector2 position)
	{
		super.setPosition(position);
	}
	@Override
	public void setOrigin(Vector2 origin)
	{
		super.setOrigin(origin);
	}
	@Override
	public void setScale(Vector2 scale)
	{
		return;
	}
	@Override
	public void setRotation(float rotation)
	{
		return;
	}
	@Override
	public void setTextureRegion(TextureRegion textureRegion)
	{
		return;
	}
	
	public Vector2 getLeftTopPosition()
	{
		calculateLeftTop();
		return mLeftTopPosition;
	}
	
	@Override
	public Vector2 getLeftBottomPosition()
	{
		calculateLeftTop();
		float height = mGlyphLayout.height - mFont.getDescent();
		return new Vector2(mLeftTopPosition.x, mLeftTopPosition.y - height);
	}

	/**
	public int getAlign()
	{
		return mAlign;
	}
	public void setAlign(int align)
	{
		mAlign = align;
	}
	/**/
	
	public boolean isWrap()
	{
		return mWrap;
	}
	public void setWrap(boolean wrap)
	{
		mWrap = wrap;
		mLeftBottomUpdated = false;
	}

	public String getTruncate()
	{
		return mTruncate;
	}
	public void setTruncate(String truncate)
	{
		mTruncate = truncate;
		mLeftBottomUpdated = false;
	}

	public Color getColor()
	{
		if(mColor == null)
			return mFont.getColor();
		else
			return mColor;
	}
	public void setColor(Color color)
	{
		mColor = color;
		//TODO necessary ?
		mLeftBottomUpdated = false;
	}
	public void setColor(float r, float g, float b, float a)
	{
		if(mColor == null)
			mColor = new Color(r, g, b, a);
		else
			mColor.set(r, g, b, a);
	}
	
	@Override
	public void dispose()
	{
		if(isOwnAssets())
		{
			mFont.dispose();
		}
		
		super.dispose();
	}
}
