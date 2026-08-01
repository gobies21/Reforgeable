package net.gobies.reforgeable.init;

import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.block.ReforgingStationBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RFBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reforgeable.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reforgeable.MOD_ID);

    public static final RegistryObject<Block> ReforgingStation;
    public static final RegistryObject<Item> ReforgingStationItem;

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }

    static {
        ReforgingStation = BLOCKS.register("reforging_station", () -> new ReforgingStationBlock(BlockBehaviour.Properties.of().strength(3.0F, 10.0F).requiresCorrectToolForDrops().sound(SoundType.ANVIL)));
        ReforgingStationItem = ITEMS.register("reforging_station", () -> new BlockItem(ReforgingStation.get(), new Item.Properties()));
    }
}
