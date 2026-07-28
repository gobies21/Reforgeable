package net.gobies.reforgeable.init;

import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.client.ReforgingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RFMenus {
    public static final DeferredRegister<MenuType<?>> MENUS;
    public static final RegistryObject<MenuType<ReforgingMenu>> REFORGING_STATION;

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

    static {
        MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Reforgeable.MOD_ID);
        REFORGING_STATION = MENUS.register("reforging_station", () -> IForgeMenuType.create((windowId, inv, data) -> new ReforgingMenu(windowId, inv)));
    }

}