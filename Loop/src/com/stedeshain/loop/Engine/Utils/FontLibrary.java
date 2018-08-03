package com.stedeshain.loop.Engine.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.sun.istack.internal.NotNull;

/**
 * A static library can be used through the whole game life time. User can find
 * a BitmapFont by file name(like "calibri.ttf") and font size. If there is one,
 * return it. Otherwise, the library will automatically create a correct
 * BitmapFont and restore it, also will return it. If cannot creat the right
 * BitmapFont(cannot find the font file, etc.), will return null.
 * 
 * @author SteDeshain
 * 
 */
public class FontLibrary
{
	public static final int MIN_FONT_SIZE = 15;
	public static final int DEFAULT_FONT_SIZE = 20;
	
	private static int sDefaultSize = DEFAULT_FONT_SIZE;

	private static ObjectMap<String, FreeTypeFontGenerator> sGenerators = new ObjectMap<String, FreeTypeFontGenerator>();
	private static ObjectMap<String, IntMap<BitmapFont>> sFonts = new ObjectMap<String, IntMap<BitmapFont>>();
	
	public static BitmapFont get(@NotNull String fontFileName)
	{
		return get(fontFileName, sDefaultSize);
	}
	/**
	 * The BitmapFont returned is owned by the FontLibrary,
	 * a Component already setted as isOwnAssets == true cannot use this font.
	 * @param fontFileName
	 * @param fontSize
	 * @return
	 */
	public static BitmapFont get(@NotNull String fontFileName, int fontSize)
	{
		if(fontFileName.trim().equals(""))
		{
			Utils.error("Invalid file name.");
			return null;
		}
		
		int size = fontSize;
		if(size < MIN_FONT_SIZE)
			size = MIN_FONT_SIZE;
		
		IntMap<BitmapFont> targetMap = sFonts.get(fontFileName);
		if(targetMap == null)
		{
			//then create the generator
			FreeTypeFontGenerator newGenerator;
			try
			{
				newGenerator = new FreeTypeFontGenerator(Gdx.files.internal(Utils.getFontPath(fontFileName)));
			}
			catch(GdxRuntimeException e)
			{
				Utils.error("Cannot load font file \"" + fontFileName + "\", detail information:" + e.getMessage());
				return null;
			}
			sGenerators.put(fontFileName, newGenerator);
			
			IntMap<BitmapFont> newMap = new IntMap<BitmapFont>();
			sFonts.put(fontFileName, newMap);
			LazyBitmapFont newFont = new LazyBitmapFont(newGenerator, size);
			newMap.put(size, newFont);
			
			return newFont;
		}
		else
		{
			BitmapFont targetFont = targetMap.get(size);
			if(targetFont == null)
			{
				FreeTypeFontGenerator targetGenerator = sGenerators.get(fontFileName);
				if(targetGenerator == null)
				{
					Utils.error("No correct FreeTypeFontGenerator found in FontLibrary.");
					return null;
				}
				LazyBitmapFont newFont = new LazyBitmapFont(targetGenerator, size);
				targetMap.put(size, newFont);
				return newFont;
			}
			else
			{
				return targetFont;
			}
		}
	}
	
	public static void setDefaultSize(int newSize)
	{
		if(newSize < MIN_FONT_SIZE)
			sDefaultSize = MIN_FONT_SIZE;
		else
			sDefaultSize = newSize;
	}
	public static int getDefaultSize()
	{
		return sDefaultSize;
	}
	
	public static void dispose()
	{
		for(FreeTypeFontGenerator gen: sGenerators.values())
		{
			gen.dispose();
		}
		for(IntMap<BitmapFont> map: sFonts.values())
		{
			for(BitmapFont font: map.values())
			{
				font.dispose();
			}
		}
	}
}
