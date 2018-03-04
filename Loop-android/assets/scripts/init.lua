require "tools/log"
require "tools/className"

init = {}
game = {}
init.fileName, game.gameInstance = ...
game.currentScene = nil	--contains a instance of currentScene, in case of the lua gc collect currentScene
game.lastScene = nil

function init.registerGameInstance(game)
	if type(game) ~= "userdata" then
		log.error("Attempt to register some other value rather than userdata as the gameInstance")
	else
		if game.gameInstance == nil then
			game.gameInstance = game
		else
			log.error("Only one Game instance can be created during one application lifetime")
		end
	end
end

function init.registerScriptPath(path)
	if type(game) ~= "string" then
		log.error("Attempt to register some other value rather than string as the scriptPath")
	else
		package.path = package.path .. ";" .. path
	end
end

function game.setFirstScene(sceneTable)
	game.gameInstance:setFirstScene(sceneTable.sceneInstance)
	game.lastScene = nil
	game.currentScene = sceneTable
end

function game.beginScene(sceneTable)
	game.gameInstance:beginScene(sceneTable.sceneInstance)
	game.lastScene = game.currentScene
	game.currentScene = sceneTable
end

function game.disposeScene(sceneTable)
	if game.currentScene == sceneTable or game.lastScene == sceneTable then
		return
	else
		sceneTable.sceneInstance:dispose()
		_G[sceneTable.sceneName] = nil
	end
end

globalAssets = {}
Gdx = luajava.bindClass(className.Gdx)
