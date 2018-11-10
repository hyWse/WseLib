package eu.hywse.libv1_7.bukkit.item;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static eu.hywse.libv1_7.bukkit.WseTextUtil.*;

public class WseItemUtil {

    /**
     * Sets a setting to an item, which is visible and readable by the lore
     *
     * @param item  ItemStack | Item
     * @param key   String | Key
     * @param value String | Value
     */
    public static void updateItemInfo(ItemStack item, String key, String value) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();

        boolean c = false;

        if (lore.size() > 0) {
            for (int i = 0; i < lore.size(); i++) {
                String line = ncc(lore.get(i));

                if (line.startsWith(ncc(key))) {
                    lore.set(i, c(key + value));
                    c = true;
                    break;
                }
            }
        }

        if (!c) {
            lore.add(c(key + value));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    /**
     * Reads a setting from a lore
     *
     * @param item ItemStack | Item
     * @param key  String | Key
     * @return String | Value
     */
    public static String getItemInfo(ItemStack item, String key) {
        if (item == null) return null;

        List<String> lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();

        String uncolKey = ncc(key);

        for (String aLore : lore) {
            String line = ncc(aLore);

            if (line.startsWith(uncolKey)) {
                return line.substring(uncolKey.length());
            }
        }

        return null;
    }

    /**
     * Reads a setting from a lore
     *
     * @param item ItemStack | Item
     * @param key  String | Key
     * @param value Object | Value
     */
    public static void updateInfo(ItemStack item, String key, Object value) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList();

        boolean c = false;

        if (lore.size() > 0) {
            for (int i = 0; i < lore.size(); i++) {
                String line = nc(c(lore.get(i)));
                if (line.toLowerCase().startsWith(nc(c(key.toLowerCase())))) {
                    lore.set(i, c(key + value.toString()));
                    c = true;
                    break;
                }
            }
        }

        if (!c) {
            lore.add(c(key + value));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    /**
     * Deletes a setting from a lore
     *
     * @param key  String | Key
     * @param stack ItemStack
     */
    public static void deleteInfo(ItemStack stack, String key) {
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = stack.getItemMeta().hasLore() ? stack.getItemMeta().getLore() : new ArrayList();

        if (lore.size() > 0) {
            for(Iterator<String> it = lore.iterator(); it.hasNext();) {
                String line = nc(c(it.next()));
                if (line.toLowerCase().startsWith(nc(c(key.toLowerCase())))) {
                    it.remove();
                    break;
                }
            }
        }

        meta.setLore(lore);
        stack.setItemMeta(meta);
    }


    public static String getItemString(ItemStack itemStack) {
        getYaml().set("item", itemStack);
        return getYaml().saveToString();
    }

    public ItemStack getItemStack(String string) {
        try {
            getYaml().loadFromString(string);
        } catch (InvalidConfigurationException e) {
            return null;
        }

        return getYaml().getItemStack("item");
    }

    public String getItemStringArray(ItemStack[] itemStack) {
        getYaml().set("item", itemStack);
        return getYaml().saveToString();
    }

    public ItemStack[] getItemStackArray(String string) {
        try {
            getYaml().loadFromString(string);
        } catch (InvalidConfigurationException e) {
            return null;
        }

        Object o = getYaml().get("item");

        if (!(o instanceof ArrayList)) {
            return null;
        }

        ArrayList<ItemStack> stacks = (ArrayList<ItemStack>) o;
        return stacks.toArray(new ItemStack[stacks.size()]);
    }

    private static YamlConfiguration _yaml = null;
    private static YamlConfiguration getYaml() {
        if(_yaml == null) {
            _yaml = new YamlConfiguration();
        }
        return _yaml;
    }

}
