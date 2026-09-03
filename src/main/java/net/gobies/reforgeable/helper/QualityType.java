package net.gobies.reforgeable.helper;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.HashSet;
import java.util.Set;

public enum QualityType {
    NONE("none"),
    WEAPON("weapon"),
    TOOL("tool"),
    BOW("bow"),
    SHIELD("shield"),
    ROD("rod"),
    HELMET("helmet"),
    CHESTPLATE("chestplate"),
    LEGGINGS("leggings"),
    BOOTS("boots"),
    PET("pet"),
    MAGIC("magic"), // Irons spells compat
    CURIO("curio"), // Curios compat
    BLACKLIST("none"); // Blacklist holder

    QualityType(String key) {
        this.key = key;
    }

    public final String key;
    public final Set<Item> items = new HashSet<>();
    public final Set<TagKey<Item>> tags = new HashSet<>();

    public void clear() {
        this.items.clear();
        this.tags.clear();
    }
}