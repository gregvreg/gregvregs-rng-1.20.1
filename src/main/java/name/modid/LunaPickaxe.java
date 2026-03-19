package name.modid;

import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

public class LunaPickaxe extends PickaxeItem {

    public final float luckMultiplier;
    public final int explosionType;
    public final float procRate;

    public LunaPickaxe(ToolMaterial material, int attackDamage, float attackSpeed,
                       Settings settings, float luckMultiplier, int explosionType, float procRate) {
        super(material, attackDamage, attackSpeed, settings);
        this.luckMultiplier = luckMultiplier;
        this.explosionType = explosionType;
        this.procRate = procRate;
    }
}