package net.gobies.reforgeable.init;

import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.block.ReforgingStationBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RFBlocks {
    public static final DeferredRegister<Block> BLOCKS;
    public static final DeferredRegister<Item> ITEMS;

    public static final DeferredHolder<Block, ReforgingStationBlock> ReforgingStation;
    public static final DeferredHolder<Item, Item> ReforgingStationItem;

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }

    static {
        BLOCKS = DeferredRegister.create(Registries.BLOCK, Reforgeable.MOD_ID);
        ITEMS = DeferredRegister.create(Registries.ITEM, Reforgeable.MOD_ID);
        ReforgingStation = BLOCKS.register("reforging_station", () -> new ReforgingStationBlock(BlockBehaviour.Properties.of().strength(3.0F, 10.0F).requiresCorrectToolForDrops().sound(SoundType.ANVIL)));
        ReforgingStationItem = ITEMS.register("reforging_station", () -> new BlockItem(ReforgingStation.get(), new Item.Properties()));
    }
}
