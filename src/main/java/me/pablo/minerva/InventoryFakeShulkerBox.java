package me.pablo.minerva;

import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.ShulkerBoxContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class InventoryFakeShulkerBox extends Inventory implements INamedContainerProvider {

    private final ItemStack shulker;
    final ParentInventory parent;

    public InventoryFakeShulkerBox(Container parentContainer, ITextComponent parentName, ItemStack shulkerBox) {
        super(27);
        this.shulker = shulkerBox;
        ItemStackHelper.loadAllItems(shulkerBox.getOrCreateChildTag("BlockEntityTag"),
                                     super.inventoryContents);
        super.addListener(invBasic -> ItemStackHelper.saveAllItems(shulker.getOrCreateChildTag("BlockEntityTag"),
                                                                   InventoryFakeShulkerBox.super.inventoryContents));

        this.parent = getParentInventory(parentContainer, parentName);
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        ItemStackHelper.saveAllItems(shulker.getOrCreateChildTag("BlockEntityTag"),
                                     super.inventoryContents);
        super.closeInventory(player);
    }

    @Override
    public Container createMenu(int windowId,
                                   PlayerInventory playerInventory,
                                   PlayerEntity playerEntity) {
        return new ContainerFakeShulkerBox(windowId, playerInventory, this);
    }

    @Override
    public ITextComponent getDisplayName() {
        return shulker.getDisplayName();
    }

    public ItemStack getShulker() {
        return shulker;
    }

    public static boolean shouldOpenInventory(Container container, int slotId, int dragType, ClickType clickType) {
        if (dragType != 1 || clickType != ClickType.PICKUP || slotId < 0) return false;
        Slot slot = container.inventorySlots.get(slotId);
        return (slot.inventory instanceof PlayerInventory || slot.inventory instanceof EnderChestInventory)
                && Block.getBlockFromItem(slot.getStack().getItem()) instanceof ShulkerBoxBlock;
    }

    public static boolean canInteract(Container container, int slotId) {
        return slotId < 0 || !(container instanceof ContainerFakeShulkerBox) ||
                ((ContainerFakeShulkerBox) container).canInteract(container.inventorySlots.get(slotId).getStack());
    }

    private ParentInventory getParentInventory(Container parent, ITextComponent parentName) {
        if (parent instanceof ContainerFakeShulkerBox)
            return ((ContainerFakeShulkerBox) parent).getShulkerBoxInventory().parent;

        if (parent instanceof ChestContainer)
            return new ParentChest((ChestContainer) parent, parentName);

        return ParentInventory.NONE;
    }

    public interface ParentInventory {
        ParentInventory NONE = (pl) -> {};

        void reopen(PlayerEntity player);
    }

    private static class ParentChest implements ParentInventory {
        private ChestContainer parent;
        private ITextComponent parentName;

        public ParentChest(ChestContainer parent, ITextComponent parentName) {
            this.parent = parent;
            this.parentName = parentName == null ? new TranslationTextComponent("container.chest") : parentName;
        }

        @Override
        public void reopen(PlayerEntity player) {
            player.openContainer(new SimpleNamedContainerProvider((id, inv, p) ->
                new ChestContainer(parent.getType(),id ,inv, parent.getLowerChestInventory(), parent.numRows), parentName));
        }
    }
}
