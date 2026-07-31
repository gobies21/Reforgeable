package net.gobies.reforgeable;

import com.mojang.logging.LogUtils;
import net.gobies.reforgeable.compat.curios.CuriosCompat;
import net.gobies.reforgeable.compat.curios.CuriosEvents;
import net.gobies.reforgeable.config.ClientConfig;
import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.events.ReforgingEvents;
import net.gobies.reforgeable.init.RFRenders;
import net.gobies.reforgeable.init.RFBlockEntities;
import net.gobies.reforgeable.init.RFBlocks;
import net.gobies.reforgeable.init.RFMenus;
import net.gobies.reforgeable.network.PacketHandler;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Reforgeable.MOD_ID)
public class Reforgeable {
    public static final String MOD_ID = "reforgeable";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Reforgeable() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        RFBlocks.register(modBus);
        RFBlockEntities.register(modBus);
        RFMenus.register(modBus);
        ReforgingEvents.register();
        PacketHandler.registerMessages();
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
        modBus.addListener(this::registerRenders);
        modBus.addListener(this::addCreativeTab);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "reforgeable/common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "reforgeable/client.toml");
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        if (CuriosCompat.isLoaded()) {
            CuriosEvents.loadCompat();
        }
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> RFRenders.registerScreen(event));
    }

    private void registerRenders(final EntityRenderersEvent.RegisterRenderers event) {
        RFRenders.registerRenderers(event);
    }

    private void addCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(RFBlocks.ReforgingStation.get());
        }
    }
}
