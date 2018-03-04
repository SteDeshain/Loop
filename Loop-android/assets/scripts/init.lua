require "tools/log"
require "tools/className"

init = {}
game = {}
init.fileName, game.gameInstance = ...

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
