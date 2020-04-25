package eu.hywse.lib.bukkit.item;

import static eu.hywse.lib.bukkit.WseTextUtil.c;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

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
  }

  public static ItemStack empty() {
    return new WseItem(Material.GRAY_STAINED_GLASS_PANE)
        .setDisplayName(" ")
        .setAmount(1)
        .build();
  }

  public static WseItem fromBlock(org.bukkit.block.Block block) {
    return new WseItem(block.getType())
        .setDamage(block.getData())
        .setAmount(1);
  }

  private void changeItemMeta(Consumer<ItemMeta> meta) {
    final ItemMeta itemMeta = this.item.getItemMeta();
    meta.accept(itemMeta);
    this.item.setItemMeta(itemMeta);
  }

  public WseItem setDamage(int damage) {
    changeItemMeta(meta -> {
      if (meta instanceof Damageable) {
        ((Damageable) meta).setDamage(damage);
      }
    });
    return this;
  }

  public WseItem addEnchantment(Enchantment enchantment, int level) {
    this.item.addUnsafeEnchantment(enchantment, level);
    return this;
  }

  public WseItem setSkullOwner(OfflinePlayer player) {
    if (this.item.getType() == Material.PLAYER_HEAD) {
      changeItemMeta(meta -> {
        if (meta instanceof SkullMeta) {
          ((SkullMeta) meta).setOwningPlayer(player);
        }
      });
    }
    return this;
  }

  public WseItem setLeatherColor(Color color) {
    changeItemMeta(meta -> {
      if (meta instanceof LeatherArmorMeta) {
        ((LeatherArmorMeta) meta).setColor(color);
      }
    });
    return this;
  }

  public WseItem addItemFlag(ItemFlag itemflag) {
    changeItemMeta(meta -> meta.addItemFlags(itemflag));
    return this;
  }

  public WseItem addLore(String loreasstring) {
    this.lore.add(c(loreasstring));
    return this;
  }

  public ItemStack build() {
    changeItemMeta(meta -> {
      if (meta != null && this.lore.size() > 0) {
        meta.setLore(this.lore);
      }
    });
    return this.item;
  }


  public WseItem addPotionEffect(PotionEffect effect) {
    return addPotionEffect(effect, true);
  }

  public WseItem addPotionEffect(PotionEffect effect, boolean overwrite) {
    changeItemMeta(meta -> {
      if (meta instanceof PotionMeta) {
        ((PotionMeta) meta).addCustomEffect(effect, overwrite);
      }
    });
    return this;
  }

  public String getDisplayName() {
    final ItemMeta itemMeta = this.item.getItemMeta();
    if (itemMeta == null) {
      return this.item.getType().name();
    }
    return itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : itemMeta.getLocalizedName();
  }

  public WseItem setDisplayName(String display) {
    changeItemMeta(meta -> meta.setDisplayName(c(display)));
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

  public WseItem setSkullTexture(String texture) {
    if (texture.isEmpty()) {
      return this;
    }

    this.item.setType(Material.PLAYER_HEAD);

    byte[] encodedData = texture.getBytes();

    ItemMeta headMeta = this.item.getItemMeta();
    if (headMeta == null) {
      return this;
    }

    // Generate game profile
    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
    // insert texture
    profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

    Field profileField = null;
    try {
      profileField = headMeta.getClass().getDeclaredField("profile");
    } catch (NoSuchFieldException | SecurityException e) {
      e.printStackTrace();
    }

    if (profileField == null) {
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
