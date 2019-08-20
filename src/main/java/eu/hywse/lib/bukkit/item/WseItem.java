package eu.hywse.lib.bukkit.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

import static eu.hywse.lib.bukkit.WseTextUtil.c;

/**
 * @author hyWse
 * @version 3
 */

public class WseItem {

    private ItemStack item;
    private ArrayList<String> lore;

    public WseItem(Material material) {
        this.item = new ItemStack(material);

        this.lore = new ArrayList<>();
        this.lore.clear();
    }

    public static ItemStack empty() {
        return new WseItem(Material.STAINED_GLASS_PANE).setDurability(7).setDisplayName(" ").setAmount(1).build();
    }

    public static WseItem fromBlock(org.bukkit.block.Block block) {
        return new WseItem(block.getType()).setDurability(block.getData()).setAmount(1);
    }

    public WseItem setDurability(int durability) {
        this.item.setDurability((short) durability);
        return this;
    }

    public WseItem addEnchantment(Enchantment enchantment, int level) {
        this.item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public WseItem setSkullOwner(OfflinePlayer player) {
        if (this.item.getType() == Material.SKULL_ITEM) {
            SkullMeta m = (SkullMeta) this.item.getItemMeta();
            m.setOwningPlayer(player);
            this.item.setItemMeta(m);
        }
        return this;
    }

    public WseItem setLeatherColor(Color color) {
        LeatherArmorMeta m = (LeatherArmorMeta) this.item.getItemMeta();
        m.setColor(color);
        this.item.setItemMeta(m);
        return this;
    }

    public WseItem addItemFlag(ItemFlag itemflag) {
        ItemMeta meta = this.item.getItemMeta();
        meta.addItemFlags(itemflag);
        this.item.setItemMeta(meta);
        return this;
    }

    public WseItem addLore(String loreasstring) {
        this.lore.add(c(loreasstring));
        return this;
    }

    public ItemStack build() {
        ItemMeta meta = this.item.getItemMeta();

        if (this.lore.size() > 0) {
            meta.setLore(this.lore);
        }

        this.item.setItemMeta(meta);
        return this.item;
    }

    public WseItem addPotionEffect(PotionEffect effect) {
        PotionMeta meta = (PotionMeta) this.item.getItemMeta();
        meta.addCustomEffect(effect, true);
        this.item.setItemMeta(meta);
        return this;
    }

    public String getDisplayName() {
        return this.item.hasItemMeta() ? this.item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name() : item.getType().name();
    }

    public WseItem setDisplayName(String display) {
        ItemMeta m = this.item.getItemMeta();
        m.setDisplayName(c(display));
        this.item.setItemMeta(m);
        return this;
    }

    public int getAmount() {
        return item.getAmount();
    }

    public WseItem setAmount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public String getLore(int num) {
        if (this.lore.size() > num) {
            return this.lore.get(num);
        }
        return "";
    }

    public WseItem editLore(int num, String loreAsString) {
        if (this.lore.size() >= num) {
            this.lore.set(num, loreAsString);
        }

        return this;
    }

    public WseItem clearLore() {
        this.lore.clear();
        return this;
    }

    public WseItem setSpawnType(EntityType entityType) {
        if (this.item.getType() == Material.MONSTER_EGG) {
            SpawnEggMeta spawnEggMeta = (SpawnEggMeta) this.item.getItemMeta();
            spawnEggMeta.setSpawnedType(entityType);
            this.item.setItemMeta(spawnEggMeta);
        }

        return this;
    }

    public WseItem setSkullTexture(String texture) {
        this.item.setType(Material.SKULL_ITEM);
        this.item.setDurability((short) 3);
        if (texture.isEmpty()) {
            return this;
        }

        byte[] encodedData = texture.getBytes();

        ItemMeta headMeta = this.item.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        if(profileField == null) {
            System.out.println("[WseLib] Field \"profile\" not found!");
            return this;
        }
        profileField.setAccessible(true);

        try {
            profileField.set(headMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        this.item.setItemMeta(headMeta);
        return this;
    }


}
