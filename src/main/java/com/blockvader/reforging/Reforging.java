package com.blockvader.reforging;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import com.blockvader.reforging.client.ClientProxy;
import com.blockvader.reforging.client.ReforgingScreen;
import com.blockvader.reforging.init.ModContainers;
import com.blockvader.reforging.network.ReforgingPacketHandler;


@Mod(Reforging.MOD_ID)
public class Reforging
{
	public static final String MOD_ID = "reforging";

    public Reforging() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new Attributes());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        ReforgingPacketHandler.register();
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
    	ScreenManager.registerFactory(ModContainers.REFORGING_MENU, ReforgingScreen::new);
        ClientProxy.registerItemModels();
        ClientProxy.registerEntityModels();
    }
}
