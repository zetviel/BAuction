package org.by1337.bauction.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.by1337.bauction.Main;
import org.by1337.bauction.api.auc.ItemHolder;
import org.by1337.blib.chat.placeholder.Placeholder;
import org.by1337.blib.command.CommandException;
import org.by1337.bmenu.menu.*;
import org.jetbrains.annotations.Nullable;

public class ItemViewerMenu extends Menu {
    private final Cache cache;
    private ItemStack itemStack;
    private Object data;
    private static boolean seenIllegalCash;

    public ItemViewerMenu(MenuSetting setting, Player player, @Nullable Menu previousMenu, MenuLoader menuLoader) {
        super(setting, player, previousMenu, menuLoader);
        if (setting.getCache() == null) {
            cache = new Cache();
            setting.setCache(cache);
        } else if (setting.getCache() instanceof Cache) {
            this.cache = (Cache) setting.getCache();
        } else {
            if (!seenIllegalCash) {
                Main.getMessage().error("Illegal cache type '%s'! Excepted %s", setting.getCache().getClass(), Cache.class);
                seenIllegalCash = true;
            }
            cache = new Cache();
        }
        if (previousMenu != null) {
            MenuItem item = previousMenu.getLastClickedItem();
            if (item != null && item.getData() instanceof ItemHolder) {
                ItemHolder itemHolder = (ItemHolder) item.getData();
                data = item.getData();
                itemStack = itemHolder.getItemStack();
                if (data instanceof Placeholder) {
                    registerPlaceholders((Placeholder) data);
                }
            } else {
                Menu m = this;
                while (!(m instanceof ItemHolder) && m != null) {
                    m = m.getPreviousMenu();
                }
                if (m instanceof ItemHolder) {
                    itemStack = ((ItemHolder) m).getItemStack();
                }
                if (m != null && m != this) {
                    registerPlaceholders(m);
                }
            }
        }
    }

    @Override
    protected void generate() {
        if (itemStack != null) {
            customItems.clear();
            MenuItem item = cache.getItem().build(this, itemStack);
            item.getItemStack().setAmount(itemStack.getAmount());
            item.setData(data);
            customItems.add(item);
        }
    }

    @Override
    protected boolean runCommand(String[] cmd) throws CommandException {
        return false;
    }

    private class Cache {
        private MenuItemBuilder item;

        public MenuItemBuilder getItem() {
            if (item == null) {
                item = setting.getContext().getAs("item", MenuItemBuilder.class);
            }
            return item;
        }
    }
}
