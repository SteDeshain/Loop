package com.stedeshain.loop.Engine.Lua;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import com.badlogic.gdx.Gdx;
import com.stedeshain.loop.Engine.Utils.Constants;

public class lutils extends TwoArgFunction
{
	@Override
	public LuaValue call(LuaValue modname, LuaValue env)
	{
		LuaValue library = tableOf();
		library.set("info", new info());
		library.set("debug", new debug());
		library.set("error", new error());
		//...
		env.set("lutils", library);
		env.get("package").get("loaded").set("lutils", library);
		return library;
	}
	
	static class info extends OneArgFunction
	{
		@Override
		public LuaValue call(LuaValue message)
		{
			try
			{
				String msg = message.checkstring().tojstring();
				Gdx.app.log(Constants.LUA_TAG, msg);
			}
			catch(LuaError e)
			{
				Gdx.app.error(Constants.LUA_CALL_JAVA_TAG, "Attempt to log a non-JavaString message");
			}
			
			return LuaValue.NIL;
		}
	}
	
	static class debug extends OneArgFunction
	{
		@Override
		public LuaValue call(LuaValue message)
		{
			try
			{
				String msg = message.checkstring().tojstring();
				Gdx.app.debug(Constants.LUA_TAG, msg);
			}
			catch(LuaError e)
			{
				Gdx.app.error(Constants.LUA_CALL_JAVA_TAG, "Attempt to log a non-JavaString message");
			}
			
			return LuaValue.NIL;
		}
	}
	
	static class error extends OneArgFunction
	{
		@Override
		public LuaValue call(LuaValue message)
		{
			try
			{
				String msg = message.checkstring().tojstring();
				Gdx.app.error(Constants.LUA_TAG, msg);
			}
			catch(LuaError e)
			{
				Gdx.app.error(Constants.LUA_CALL_JAVA_TAG, "Attempt to log a non-JavaString message");
			}
			
			return LuaValue.NIL;
		}
	}
}
