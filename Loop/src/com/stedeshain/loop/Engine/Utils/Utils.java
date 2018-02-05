package com.stedeshain.loop.Engine.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Utils
{
	public static void info(String message)
	{
		Gdx.app.log(Constants.ENGINE_TAG, message);
	}
	public static void debug(String message)
	{
		Gdx.app.debug(Constants.ENGINE_TAG, message);
	}
	public static void error(String message)
	{
		Gdx.app.error(Constants.ENGINE_TAG, message);
	}
	
	public static String getImageAssetPath(String fileName)
	{
		return Constants.IMAGES_PATH + fileName;
	}
	public static String getSoundAssetPath(String fileName)
	{
		return Constants.SOUNDS_PATH + fileName;
	}
	public static String getScriptsPath(String fileName)
	{
		return Constants.SCRIPTS_PATH + fileName;
	}
	public static String getLevelsPath(String fileName)
	{
		return Constants.LEVELS_PATH + fileName;
	}

	public static boolean isEqual(double a, double b) 
	{  
	    if(Double.isNaN(a) || Double.isNaN(b) || Double.isInfinite(a) || Double.isInfinite(b))
	    {  
	        return false;  
	    }  
	    return (a - b) < 0.00001d;  
	} 
	public static boolean isEqual(float a, float b) 
	{  
	    if(Float.isNaN(a) || Float.isNaN(b) || Float.isInfinite(a) || Float.isInfinite(b))
	    {  
	        return false;  
	    }  
	    return (a - b) < 0.001d;  
	} 
	
	public static boolean isEqual(Vector2 a, Vector2 b)
	{
		return a.epsilonEquals(b, Constants.FLOAT_PRECISION);
	}
	
	public enum VectorConvertMode
	{
		NegativeToZero,
		NegativeToPositive,
	}
	public static boolean isVectorNotBiggerThanUnit(Vector2 vector)
	{
		return Math.abs(vector.x) <= 1f && Math.abs(vector.y) <= 1f;
	}
	public static Vector2 toModifiedVector(Vector2 vector)
	{
		if(isVectorNotBiggerThanUnit(vector))
			return new Vector2(vector);
		
		int xSignal = vector.x >= 0 ? 1 : -1;
		int ySignal = vector.y >= 0 ? 1 : -1;
		
		float xAbs = Math.abs(vector.x);
		float yAbs = Math.abs(vector.y);
		
		Vector2 result;
		if(isEqual(xAbs, yAbs))
			result = new Vector2(1f, 1f);
		else
		{
			if(xAbs > yAbs)
			{
				result = new Vector2(1f, yAbs / xAbs);
			}
			else
			{
				result = new Vector2(xAbs / yAbs, 1f);
			}
		}
		result.x *= xSignal;
		result.y *= ySignal;
		
		return result;
	}
	public static boolean isPositiveVectorNotBiggerThanUnit(Vector2 vector)
	{
		if(vector.x < 0 || vector.y < 0)
			return false;
		
		return vector.x <= 1f && vector.y <= 1f;
	}
	public static Vector2 toPositiveModifiedVector(Vector2 vector, VectorConvertMode convertMode)
	{
		if(isPositiveVectorNotBiggerThanUnit(vector))
			return new Vector2(vector);

		Vector2 newVector = new Vector2(vector);
		switch(convertMode)
		{
		case NegativeToPositive:
			newVector.x *= newVector.x >= 0 ? 1 : -1;
			newVector.y *= newVector.y >= 0 ? 1 : -1;
			break;
		case NegativeToZero:
		default:
			newVector.x = newVector.x >= 0 ? newVector.x : 0f;
			newVector.y = newVector.y >= 0 ? newVector.y : 0f;
			break;
		}
		
		return toModifiedVector(newVector);
	}
	public static Vector2 toPositiveModifiedVector(Vector2 vector)
	{
		return toPositiveModifiedVector(vector, VectorConvertMode.NegativeToZero);
	}
	
	public static Vector2 measureText(BitmapFont font, String text, float targetWidth,
			int hAlign, boolean wrap, String truncate)
	{
		GlyphLayout layout = new GlyphLayout();
		layout.setText(font, text, 0, text.length(), Color.WHITE, targetWidth, hAlign, wrap, truncate);
		return new Vector2(layout.width, layout.height - font.getDescent());
	}

	public static Texture getPureColorTexture(Color color, int width, int height)
	{
		Pixmap pixmap = new Pixmap(width, height, Constants.GAME_PIXEL_FORMAT);
		pixmap.setColor(color);
		pixmap.fill();
		return new Texture(pixmap);
	}
	public static TextureRegion getColorTextureRegion(Color color, int width, int height)
	{
		Texture texture = getPureColorTexture(color, width, height);
		return new TextureRegion(texture);
	}
	public static TextureRegion getCircleColorTextureRegion(Color color, int radius)
	{
		int diameter = radius * 2;
		Pixmap pixmap = new Pixmap(diameter, diameter, Constants.GAME_PIXEL_FORMAT);
		pixmap.setColor(color);
		pixmap.fillCircle(radius, radius, radius);
		return new TextureRegion(new Texture(pixmap));
	}
	
	public static float getRadians(float degree)
	{
		return MathUtils.degreesToRadians * degree;
	}
	
	private static BitmapFont mDefaultFont = null;
	public static BitmapFont getDefaultFont()
	{
		if(mDefaultFont == null)
			mDefaultFont = new BitmapFont();
		
		return mDefaultFont;
	}
	
	public static void dispose()
	{
		if(mDefaultFont != null)
			mDefaultFont.dispose();
	}
}
