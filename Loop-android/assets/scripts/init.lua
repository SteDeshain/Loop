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
