package com.stedeshain.loop.Engine.Utils;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;
import com.stedeshain.loop.Engine.Component.SceneComponent;
import com.stedeshain.loop.Engine.Component.Body.BoxBody;
import com.stedeshain.loop.Engine.Component.Body.CircleBody;
import com.stedeshain.loop.Engine.Scene.Scene;
import com.sun.istack.internal.NotNull;

public class Level implements Disposable
{
	private Scene mScene;
	private String mLevelName;
	private AssetsHelper assets = new AssetsHelper();
	
	public Level(@NotNull Scene scene, @NotNull String levelName)
	{
		mScene = scene;
		mLevelName = levelName;
	}
	
	public void init()
	{
		Globals g = mScene.getMotherGame().getLuaGlobals();
		
		String fileName = Utils.getLevelsPath(mLevelName + ".lua");
		LuaValue levelChunk = g.loadfile(fileName);
		levelChunk.call();
		
		LuaTable t = (LuaTable)g.get(mLevelName);
		
		float viewportWidth = t.get("viewportWidth").tofloat();
		float viewportHeight = t.get("viewportHeight").tofloat();
		mScene.setViewport(viewportWidth, viewportHeight);
		
		LuaTable comps = (LuaTable)t.get("comps");
		int compCount = comps.length();
		for(int i = 1; i <= compCount; i++)
		{
			LuaTable currentComp = (LuaTable)comps.get(i);
			
			//String name = currentComp.get("name").tojstring();
			String compType = currentComp.get("compType").tojstring();
			String bodyType = currentComp.get("bodyType").tojstring();
			int depth = currentComp.get("depth").toint();
			String textureAtlas = currentComp.get("textureAtlas").tojstring();
			String region = currentComp.get("region").tojstring();
			float originXFactor = currentComp.get("originXFactor").tofloat();
			float originYFactor = currentComp.get("originYFactor").tofloat();
			float positionX = currentComp.get("positionX").tofloat();
			float positionY = currentComp.get("positionY").tofloat();
			float width = currentComp.get("width").tofloat();
			float height = currentComp.get("height").tofloat();
			
			TextureRegion currentRegion = null;
			if(!textureAtlas.equals("null"))
			{
				TextureAtlas atlas = null;
				String atlasFileName = Utils.getImageAssetPath(textureAtlas);
				AssetsHelper sceneAssets = mScene.getAssetsHelper();
				if(sceneAssets != null && sceneAssets.isLoaded(atlasFileName, TextureAtlas.class))
				{
					atlas = sceneAssets.getTextureAtlas(atlasFileName);
				}
				else
				{
					AssetsHelper globalAssets = mScene.getMotherGame().getGlobalAssets();
					if(globalAssets != null && globalAssets.isLoaded(atlasFileName, TextureAtlas.class))
					{
						atlas = globalAssets.getTextureAtlas(atlasFileName);
					}
					else 
					{
						if(!assets.isLoaded(atlasFileName, TextureAtlas.class))
						{
							assets.loadTextureAtlas(atlasFileName);
							assets.finishLoadingAsset(atlasFileName);
							atlas = assets.getTextureAtlas(atlasFileName);
						}
						else
							atlas = assets.getTextureAtlas(atlasFileName);
					}
				}
				
				currentRegion = atlas.findRegion(region);
			}
			
			SceneComponent comp = null;
			if(compType.equals("DrawableComp"))
			{
				//TODO ...
			}
			else if(compType.equals("BoxBody"))
			{
				BoxBody body = new BoxBody(new Vector2(positionX, positionY), 
						new Vector2(width, height), 
						currentRegion);
				body.setBodyTypeDef(BodyType.valueOf(bodyType));
				body.setOriginFactor(originXFactor, originYFactor);
				body.setDepth(depth);
				comp = body;
			}
			else if(compType.equals("CircleBody"))
			{
				CircleBody body = new CircleBody(new Vector2(positionX, positionY), 
						width / 2, 
						currentRegion);
				body.setBodyTypeDef(BodyType.valueOf(bodyType));
				body.setOriginFactor(originXFactor, originYFactor);
				body.setDepth(depth);
				comp = body;
			}
			if(comp != null)
				mScene.addComponent(comp);
		}
	}

	public Scene getScene()
	{
		return mScene;
	}

	public String getLevelName()
	{
		return mLevelName;
	}

	@Override
	public void dispose()
	{
		assets.dispose();
	}
}
