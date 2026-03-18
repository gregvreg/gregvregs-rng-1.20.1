package name.modid;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item TEXT_ITEM = new Item(new Item.Settings());
    public static final Item TELEPORT_ITEM = new ItemTeleport(new Item.Settings());

    public static void registerItems() {
        Registry.register(
                Registries.ITEM,
                new Identifier("gregvregs_rng", "text_item"),
                TEXT_ITEM
        );
        Registry.register(
                Registries.ITEM,
                new Identifier("gregvregs_rng", "teleport_item"),
                TELEPORT_ITEM
        );
    }
}