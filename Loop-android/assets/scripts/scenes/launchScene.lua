local M = {}
local moduleName = "launchScene"
_G[moduleName] = M;

setmetatable(M, {__index = _ENV})
_ENV = M

sceneName = "launchScene"

require "tools/className"
require "tools/pathHelper"

globalAssets.lantingFontGenerator = luajava.newInstance(className.FreeTypeFontGenerator, Gdx.files:internal(pathHelper.getFontPath("lanting.ttf")))
globalAssets.hugeLantingFont = luajava.newInstance(className.LazyBitmapFont, globalAssets.lantingFontGenerator, 50)
globalAssets.largeLantingFont = luajava.newInstance(className.LazyBitmapFont, globalAssets.lantingFontGenerator, 30)
globalAssets.smallLantingFont = luajava.newInstance(className.LazyBitmapFont, globalAssets.lantingFontGenerator, 25)
globalAssets.calibriFontGenerator = luajava.newInstance(className.FreeTypeFontGenerator, Gdx.files:internal(pathHelper.getFontPath("calibri.ttf")))
globalAssets.hugeCalibriFont = luajava.newInstance(className.LazyBitmapFont, globalAssets.calibriFontGenerator, 50)
globalAssets.largeCalibriFont = luajava.newInstance(className.LazyBitmapFont, globalAssets.calibriFontGenerator, 30)
globalAssets.smallCalibriFont = luajava.newInstance(className.LazyBitmapFont, globalAssets.calibriFontGenerator, 20)

--[[
loadingLabel = luajava.newInstance(className.Label, largeFont, "Loading...")
loadingLabel:setSourceAnchor(1, 0)
loadingLabel:setViewportAnchor(1, 0)
--]]
require "scenes/welcomeScene"
sceneInstance = luajava.newInstance(className.LoadScene, game.gameInstance, welcomeScene.sceneInstance)
--sceneInstance:addComponent(loadingLabel)

--game.gameInstance:setFirstScene(sceneInstance)
--[[
local startAScene = false
local eclipseTime = 0
local totalTime = 0
local dotCount = 3
local dotText = "."
local updateListener = {}
function updateListener.onUpdate(scene, deltaTime)
	totalTime = totalTime + deltaTime
	eclipseTime = eclipseTime + deltaTime
	if totalTime >= 0.5 then
		totalTime = 0
		dotCount = dotCount - 1
		if dotCount < 0 then
			dotCount = 3
		end
		dotText = ""
		for i = 1, dotCount do
			dotText = dotText .. "."
		end
		loadingLabel:setText("Loading" .. dotText)
	end
	
	if eclipseTime >= 2 and startAScene == false then
		startAScene = true
		require "scenes/welcomeScene"
		game.beginScene(welcomeScene)
	end
end

local updateProxy = luajava.createProxy(className.SceneOnUpdateListener, updateListener)
sceneInstance:setUpdateListener(updateProxy)
--]]

sceneInstance:setOneTimeUsed(true)
--sceneInstance:setBackColor(1, 1, 1, 1)