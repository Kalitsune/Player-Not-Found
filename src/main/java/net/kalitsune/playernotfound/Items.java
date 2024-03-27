package net.kalitsune.playernotfound;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class Items {
    public static ItemStack hiderClub;

    public static ItemStack seekerClub;

    public static void init() {
        hiderClub = new ItemStack(Objects.requireNonNull(Material.getMaterial(Stores.config.getString("items.hider_club.material", "STICK"))));
        hiderClub.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
        ItemMeta im = hiderClub.getItemMeta();
        if (im != null) {
            im.setDisplayName(Stores.config.getString("items.hider_club.name", "§8Hider Club"));
            // set the lore or set "Right click to blind the seekers!" if the lore is empty
            List<String> lore = Stores.config.getStringList("items.hider_club.lore");
            im.setLore(lore.isEmpty() ? List.of("Right click to blind the seekers!") : lore);
        }
        hiderClub.setItemMeta(im);

        seekerClub = new ItemStack(Objects.requireNonNull(Material.getMaterial(Stores.config.getString("items.seeker_club.material", "BLAZE_ROD"))));
        seekerClub.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
        ItemMeta im2 = seekerClub.getItemMeta();
        if (im2 != null) {
            im2.setDisplayName(Stores.config.getString("items.seeker_club.name", "§6Seeker Club"));
            // set the lore or set "Right click to capture the hiders!" if the lore is empty
            List<String> lore = Stores.config.getStringList("items.seeker_club.lore");
            im2.setLore(lore.isEmpty() ? List.of("Right click to capture the hiders!") : lore);
        }
        seekerClub.setItemMeta(im2);
    }
}
