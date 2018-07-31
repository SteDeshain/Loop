package com.stedeshain.loop.Engine.Component;

import com.badlogic.gdx.utils.Array;
import com.stedeshain.loop.Engine.Utils.Utils;

/**
 * One Button can only belong to one ButtonGroup every time.
 * 
 * @author SteDeshain
 * 
 */
public class ButtonGroup
{
	private Array<Button> mButtons;

	public ButtonGroup()
	{
		mButtons = new Array<Button>();
	}

	public ButtonGroup(Array<Button> buttons)
	{
		this();
		for(int i = buttons.size - 1; i >= 0; i--)
		{
			Button curButton = buttons.get(i);
			if(curButton.getButtonGroup() == null)
			{
				mButtons.add(curButton);
				curButton.setButtonGroup(this);
			}
			else
			{
				Utils.debug("Attempt to add a Button(with name \"" + curButton.getName()
						+ "\") already in one ButtonGroup into another group.");
			}
		}
	}

	public ButtonGroup addButton(Button button)
	{
		if(button.getButtonGroup() == null)
		{
			mButtons.add(button);
			button.setButtonGroup(this);
		}
		else
		{
			Utils.debug("Attempt to add a Button(with name \"" + button.getName()
					+ "\") already in one ButtonGroup into another group.");
		}
		return this;
	}

	public ButtonGroup removeButton(Button button)
	{
		if(mButtons.removeValue(button, true))
		{
			button.setButtonGroup(null);
		}
		return this;
	}

	public ButtonGroup removeButton(String name)
	{
		for(int i = mButtons.size - 1; i >= 0; i--)
		{
			Button curButton = mButtons.get(i);
			if(curButton.matchName(name))
			{
				curButton.setButtonGroup(null);
				mButtons.removeIndex(i);
				break;
			}
		}
		return this;
	}

	public ButtonGroup removeButtons(String tag)
	{
		for(int i = mButtons.size - 1; i >= 0; i--)
		{
			Button curButton = mButtons.get(i);
			if(curButton.matchTag(tag))
			{
				curButton.setButtonGroup(null);
				mButtons.removeIndex(i);
			}
		}
		return this;
	}
	
	public void decheckAllOtherRadioButtons(RadioButton radioButton)
	{
		for(int i = mButtons.size - 1; i >= 0; i--)
		{
			Button curButton = mButtons.get(i);
			if(curButton instanceof RadioButton)
			{
				RadioButton curRadio = (RadioButton)curButton;
				if(curRadio != radioButton)
				{
					curRadio.decheck();
				}
			}
		}
	}
}
