package com.stedeshain.loop.Engine.Component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sun.istack.internal.NotNull;

public class CheckBox extends CheckableButton
{
	public CheckBox(@NotNull String text, @NotNull BitmapFont font, @NotNull TextureRegion uncheckedPic,
			@NotNull TextureRegion checkedPic)
	{
		super(text, font, uncheckedPic, checkedPic);
	}

	public CheckBox(@NotNull TextureRegion picture, @NotNull Color lineColor, int lineWidth)
	{
		super(picture);
		setLineColor(lineColor);
		setLineWidth(lineWidth);
	}

	@Override
	protected void onClick()
	{
		toggleCheck();
		super.onClick();
	}
}
