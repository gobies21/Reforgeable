package net.gobies.reforgeable.init;

import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.util.Quality;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class RFDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Quality>> QUALITY;

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(UnaryOperator<DataComponentType.Builder<T>> builder) {
        return DATA_COMPONENT_TYPES.register("quality", () -> builder.apply(DataComponentType.builder()).build());
    }

    static {
        DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Reforgeable.MOD_ID);
        QUALITY = register(builder -> builder.persistent(Quality.CODEC).networkSynchronized(Quality.STREAM_CODEC));
    }
}
