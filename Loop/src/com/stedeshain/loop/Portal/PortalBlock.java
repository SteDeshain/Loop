package com.stedeshain.loop.Portal;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.stedeshain.loop.Engine.Component.Body.BoxBody;

/**
 * A portal has a rectangle shape<br>
 * It behaves like BoxBody: will block other Body's movement
 * @author SteDeshain
 */
public class PortalBlock extends BoxBody
{
	public PortalBlock(Vector2 position, Vector2 size, float horizontalMargin, float verticalMargin,
			TextureRegion textureRegion)
	{
		super(position, size, horizontalMargin, verticalMargin, textureRegion);
	}
}
