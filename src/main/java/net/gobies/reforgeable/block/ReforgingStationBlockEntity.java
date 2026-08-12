package net.gobies.reforgeable.block;

import net.gobies.reforgeable.client.ReforgingMenu;
import net.gobies.reforgeable.init.RFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class ReforgingStationBlockEntity extends BlockEntity implements MenuProvider {
    public ReforgingStationBlockEntity(BlockPos pos, BlockState state) {
        super(RFBlockEntities.REFORGING_STATION.get(), pos, state);
    }

    private final SimpleContainer inventory = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            ReforgingStationBlockEntity.this.setChanged();
            if (ReforgingStationBlockEntity.this.level != null && !ReforgingStationBlockEntity.this.level.isClientSide) {
                ReforgingStationBlockEntity.this.level.sendBlockUpdated(ReforgingStationBlockEntity.this.worldPosition, ReforgingStationBlockEntity.this.getBlockState(), ReforgingStationBlockEntity.this.getBlockState(), 3);
            }
        }
    };

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    public Container getInventory() {
        return this.inventory;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.reforgeable.reforging_station");
    }

    @Override
    public void onDataPacket(@NotNull Connection connection, @NotNull ClientboundBlockEntityDataPacket packet, @NotNull HolderLookup.Provider registries) {
        CompoundTag tag = packet.getTag();
        this.loadAdditional(tag, registries);

        if (this.level != null && this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ReforgingMenu(containerId, playerInventory, this.inventory, ContainerLevelAccess.create(Objects.requireNonNull(this.level), this.worldPosition));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        NonNullList<ItemStack> itemsList = NonNullList.withSize(this.inventory.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            itemsList.set(i, this.inventory.getItem(i));
        }

        ContainerHelper.saveAllItems(tag, itemsList, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        NonNullList<ItemStack> itemsList = NonNullList.withSize(this.inventory.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, itemsList, registries);

        for (int i = 0; i < itemsList.size(); i++) {
            this.inventory.setItem(i, itemsList.get(i));
        }
    }
}