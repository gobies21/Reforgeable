package net.gobies.reforgeable.client;

import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.init.RFBlocks;
import net.gobies.reforgeable.init.RFMenus;
import net.gobies.reforgeable.util.QualityUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.Container;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ReforgingMenu extends AbstractContainerMenu {

    private final Container reforgeInventory;
    private final ContainerLevelAccess access;
    private long lastReforgeGameTime = 0L;

    public ReforgingMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(2), ContainerLevelAccess.NULL);
    }

    public ReforgingMenu(int containerId, Inventory playerInventory, Container blockEntityInventory, ContainerLevelAccess access) {
        super(RFMenus.REFORGING_STATION.get(), containerId);
        this.reforgeInventory = blockEntityInventory;
        this.access = access;

        this.addSlot(new Slot(this.reforgeInventory, 0, 80, 19) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                if (stack.isEmpty() || !QualityUtil.isValidQualityItem(stack)) return false;
                List<? extends String> blacklist = CommonConfig.BLACKLIST_QUALITIES.get();
                if (blacklist == null || blacklist.isEmpty()) return true;

                String itemKey = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString();
                if (blacklist.contains(itemKey)) return false;

                for (TagKey<Item> tagKey : stack.getTags().toList()) {
                    if (blacklist.contains("#" + tagKey.location())) {
                        return false;
                    }
                }

                return true;
            }
        });

        this.addSlot(new Slot(this.reforgeInventory, 1, 80, 59) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return !QualityUtil.isValidQualityItem(stack);
            }
        });

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack activeItem = slot.getItem();
            itemstack = activeItem.copy();

            if (index < 2) {
                if (!this.moveItemStackTo(activeItem, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (QualityUtil.isValidQualityItem(activeItem)) {
                    if (!this.moveItemStackTo(activeItem, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(activeItem, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (activeItem.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, RFBlocks.ReforgingStation.get());
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
    }

    public boolean setCooldown(long currentLevelTime) {
        if (currentLevelTime - this.lastReforgeGameTime >= 2L) {
            this.lastReforgeGameTime = currentLevelTime;
            return true;
        }
        return false;
    }
}