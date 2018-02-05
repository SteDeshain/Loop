package com.stedeshain.loop.Engine.Utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class Constants
{
	public static final String ENGINE_TAG = "LoopEngine";
	
	public static final String LUA_TAG = "LuaModule"; 
	
	public static final String LUA_CALL_JAVA_TAG = "LuaCallJava"; 

	public static final float FLOAT_PRECISION = 0.00001f;

	public static final float TIME_STEP = 1f / 60f;
	public static final int VELOCITY_ITERATIONS = 8;
	public static final int POSITION_ITERATIONS = 3;
	
	public static final Format GAME_PIXEL_FORMAT = Format.RGBA8888;
	
	public static final TextureFilter TEXTURE_MIN_FILTER = TextureFilter.Linear;
	public static final TextureFilter TEXTURE_MAG_FILTER = TextureFilter.Nearest;
	
	public static final TextureFilter DESKTOP_FONT_MIN_FILTER = TextureFilter.Nearest;
	public static final TextureFilter DESKTOP_FONT_MAG_FILTER = TextureFilter.Nearest;
	
	public static final TextureFilter ANDROID_FONT_MIN_FILTER = TextureFilter.Nearest;
	public static final TextureFilter ANDROID_FONT_MAG_FILTER = TextureFilter.Nearest;
	
	public static final float SCENE_DEFAULT_TRANSITION_DURATION = 0.2f;
	
	public static final float SELECTOR_DEFAULT_SHOWING_TIME = 0.2f;
	public static final float SELECTOR_DEFAULT_MOVING_TIME = 0.4f;
	public static final float SELECTOR_DEFAULT_SHRINKING_TIME = 0.05f;
	public static final float SELECTOR_DEFAULT_HIDING_EXPAND_WIDTH_FACTOR = 5f;
	public static final float SELECTOR_DEFAULT_HIDING_EXPAND_HEIGHT_FACTOR = 1.3f;
	
	public static final Color SCREEN_FLUSH_COLOR = new Color(0f, 0f, 0f, 1f);

	public static final String FONTS_PATH = "fonts/";
	public static final String IMAGES_PATH = "images/";
	public static final String MUSIC_PATH = "music/";
	public static final String SCRIPTS_PATH = "scripts/";
	public static final String LEVELS_PATH = "scripts/levels/";
	public static final String SOUNDS_PATH = "sounds/";
	
	//debug use
	public static final Color TOUCH_CROSS_LINE_COLOR = Color.BLUE;
	public static final Color TOUCH_INFO_TEXT_COLOR = Color.GREEN;
	
	public static final float MESS_CENTER_CROSS_SIZE = 0.06f;
	public static final Color MESS_CENTER_COLOR = Color.GREEN;
	public static final float BODY_ORIGIN_CROSS_SIZE = 0.04f;
	public static final Color BODY_ORIGIN_COLOR = Color.ORANGE;
}
