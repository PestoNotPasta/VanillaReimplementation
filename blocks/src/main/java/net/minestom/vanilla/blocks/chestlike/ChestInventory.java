package net.minestom.vanilla.blocks.chestlike;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.vanilla.blocks.ChestLikeBlockHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChestInventory extends Inventory {

    private static final Map<Instance, Map<Point, ChestInventory>> INSTANCE2CHESTS = new ConcurrentHashMap<>();

    protected final ItemStack[] items;
    protected final Instance instance;
    protected final Point pos;

    private ChestInventory(Instance instance, Point pos) {
        super(InventoryType.CHEST_3_ROW, Component.text("chest"));

        this.instance = instance;
        this.pos = pos;

        // Set items
        List<ItemStack> itemsList = instance.getBlock(pos).getTag(ChestLikeBlockHandler.TAG_ITEMS);
        if (itemsList == null) {
            ItemStack[] newItems = new ItemStack[3 * 9];
            Arrays.fill(newItems, ItemStack.AIR);
            this.items = newItems;
        } else {
            this.items = itemsList.toArray(ItemStack[]::new);
        }
    }

    public static ChestInventory from(Instance instance, Point pos) {
        return INSTANCE2CHESTS.computeIfAbsent(instance, k -> new WeakHashMap<>())
                .computeIfAbsent(pos, k -> new ChestInventory(instance, pos));
    }

    public static @Nullable ItemStack[] remove(Instance instance, Point pos) {
        return INSTANCE2CHESTS.computeIfAbsent(instance, k -> new WeakHashMap<>())
                .remove(pos)
                .getItemStacks();
    }

    @Override
    public @NotNull ItemStack getItemStack(int slot) {
        ItemStack item = items[slot];

        if (item == null) {
            return ItemStack.AIR;
        }

        return item;
    }

    @Override
    public void setItemStack(int slot, @NotNull ItemStack itemStack) {
        items[slot] = itemStack;
        instance.setBlock(pos, instance.getBlock(pos).withTag(ChestLikeBlockHandler.TAG_ITEMS, List.of(items)));
    }

    @Override
    public @NotNull ItemStack[] getItemStacks() {
        return items;
    }
}