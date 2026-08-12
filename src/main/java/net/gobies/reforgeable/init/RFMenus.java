package net.gobies.reforgeable.init;

import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.client.ReforgingMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;

public class RFMenus {
    public static final DeferredRegister<MenuType<?>> MENUS;
    public static final DeferredHolder<MenuType<?>, MenuType<ReforgingMenu>> REFORGING_STATION;


    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

    static {
        MENUS = DeferredRegister.create(Registries.MENU, Reforgeable.MOD_ID);
        REFORGING_STATION = MENUS.register("reforging_station", () -> IMenuTypeExtension.create((windowId, inv, data) -> new ReforgingMenu(windowId, inv)));
    }

}