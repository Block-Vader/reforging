package com.blockvader.reforging.client;

import com.blockvader.reforging.init.ModEffects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;

public class StunedScreen extends ChatScreen{

	public StunedScreen() {
		super("");
	}
	
	@Override
	public boolean shouldCloseOnEsc()
	{
		return false;
	}
	
	//Copy of net.minecraft.client.gui.screen.SleepInMultiplayerScreen.keyPressed
	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_)
	{
		if (p_keyPressed_1_ == 257 || p_keyPressed_1_ == 335)
		{
			String s = this.inputField.getText().trim();
			if (!s.isEmpty())
			{
				this.sendMessage(s);
			}
			this.inputField.setText("");
			this.minecraft.ingameGUI.getChatGUI().resetScroll();
			return true;
		}
		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}
	
	@Override
	public void tick()
	{
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null)
		{
			if (!player.isPotionActive(ModEffects.STUN))
			{
				this.onClose();
				return;
			}
		}
		super.tick();
	}
}