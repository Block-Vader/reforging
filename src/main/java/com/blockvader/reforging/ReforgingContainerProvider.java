package com.blockvader.reforging;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.ItemStackHandler;

public class ReforgingContainerProvider implements INamedContainerProvider{

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity p_createMenu_3_)
	{
		return new ReforgingContainer(windowId, new ItemStackHandler(2), inventory);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent("container." + Reforging.MOD_ID + ".reforging_menu");
	}

}
