local M = {}
local moduleName = "log"
_ENV[moduleName] = M;

setmetatable(M, {__index = _ENV})
_ENV = M

require "tools/className"
lutils = require(className.lutils)

function debug(message)
	lutils.debug(message)
end

function info(message)
	lutils.info(message)
end

function error(message)
	lutils.error(message)
end