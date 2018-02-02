package com.stedeshain.loop.Engine.Lua;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import com.badlogic.gdx.Gdx;
import com.stedeshain.loop.Engine.Game;
import com.stedeshain.loop.Engine.Scene.Scene;
import com.stedeshain.loop.Engine.Utils.Constants;

public class lgame extends TwoArgFunction
{
	public lgame() {}

	@Override
	public LuaValue call(LuaValue modname, LuaValue env)
	{
		LuaValue library = tableOf();
		library.set("setFirstScene", new setFirstScene());
		//...
		env.set("lgame", library);
		env.get("package").get("loaded").set("lgame", library);
		return library;
	}
	
	static class setFirstScene extends OneArgFunction 
	{
		@Override
		public LuaValue call(LuaValue scene)
		{
			try
			{
				Object ud = scene.checkuserdata(Scene.class);
				Game.sSetFirstScene((Scene)ud);
			}
			catch(LuaError e)
			{
				Gdx.app.error(Constants.LUA_CALL_JAVA_TAG, "Attempt to set a non-Scene object as the first scene");
			}
			
			return LuaValue.NIL;
		}
	}

}
