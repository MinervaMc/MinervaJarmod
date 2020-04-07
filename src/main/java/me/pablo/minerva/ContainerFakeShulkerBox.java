package me.pablo.minerva;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ShulkerBoxContainer;
import net.minecraft.item.ItemStack;

public class ContainerFakeShulkerBox extends ShulkerBoxContainer {

    private final InventoryFakeShulkerBox inventory;

    public ContainerFakeShulkerBox(int windowId,
                                   PlayerInventory playerInventoryIn,
                                   InventoryFakeShulkerBox inventoryIn) {
        super(windowId, playerInventoryIn, inventoryIn);
        this.inventory = inventoryIn;
    }

    public InventoryFakeShulkerBox getShulkerBoxInventory() {
        return inventory;
    }

    public boolean canInteract(ItemStack itemStack) {
        return inventory.getShulker() != itemStack;
    }

    public void afterClose(PlayerEntity player) {
        inventory.parent.reopen(player);
    }
}
