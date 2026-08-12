package net.gobies.reforgeable.init;

import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.block.ReforgingStationBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RFBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReforgingStationBlockEntity>> REFORGING_STATION;

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    static {
        BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Reforgeable.MOD_ID);
        REFORGING_STATION = BLOCK_ENTITIES.register("reforging_station", () -> BlockEntityType.Builder.of(ReforgingStationBlockEntity::new, RFBlocks.ReforgingStation.get()).build(null));
    }
}