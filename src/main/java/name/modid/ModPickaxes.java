package name.modid;

import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModPickaxes {

    // Matériau custom pour toutes les pioches du mod
    public static final ToolMaterial LUNA_MATERIAL = new ToolMaterial() {
        @Override public int getDurability() { return 9999; }
        @Override public float getMiningSpeedMultiplier() { return 12.0f; } // vitesse rapide
        @Override public float getAttackDamage() { return 2.0f; }
        @Override public int getMiningLevel() { return 3; }
        @Override public int getEnchantability() { return 15; }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(ModBlocks.COMMON_ORE.asItem());
        }
    };

    public static final PickaxeItem DEFAULT_PICKAXE = new LunaPickaxe(
            LUNA_MATERIAL, 1, -2.8f,
            new Item.Settings(),
            1.0f,   // luck x1
            0,      // pas d'explosion
            0.0f
    );

    public static final PickaxeItem NOSTALGIC_AXE = new LunaPickaxe(
            LUNA_MATERIAL, 1, -2.8f,
            new Item.Settings(),
            1.5f,   // luck x1.5
            1,      // explosion cube 3x3x3
            1/25f   // 1 chance sur 25
    );

    public static final PickaxeItem NILAXE = new LunaPickaxe(
            LUNA_MATERIAL, 1, -2.8f,
            new Item.Settings(),
            2.5f,   // luck x2.5
            2,      // explosion boules 8 directions
            1/20f   // 1 chance sur 20
    );

    public static void registerPickaxes() {
        Registry.register(Registries.ITEM,
                new Identifier("gregvregs_rng", "default_pickaxe"), DEFAULT_PICKAXE);
        Registry.register(Registries.ITEM,
                new Identifier("gregvregs_rng", "nostalgic_axe"), NOSTALGIC_AXE);
        Registry.register(Registries.ITEM,
                new Identifier("gregvregs_rng", "nilaxe"), NILAXE);
    }
}