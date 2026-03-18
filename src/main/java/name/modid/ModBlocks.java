package name.modid;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block MOON_STONE = new Block(
            FabricBlockSettings.copyOf(Blocks.STONE).strength(2.0f, 6.0f)
    );

    public static final Block COMMON_ORE = new Block(
            FabricBlockSettings.copyOf(Blocks.STONE).strength(1.5f, 4.0f)
    );

    public static final Block RARE_ORE = new Block(
            FabricBlockSettings.copyOf(Blocks.AMETHYST_BLOCK).strength(3.0f, 8.0f)
    );

    public static void registerBlocks() {
        Registry.register(Registries.BLOCK,
                new Identifier("gregvregs_rng", "moon_stone"), MOON_STONE);
        Registry.register(Registries.ITEM,
                new Identifier("gregvregs_rng", "moon_stone"),
                new BlockItem(MOON_STONE, new Item.Settings()));

        Registry.register(Registries.BLOCK,
                new Identifier("gregvregs_rng", "common_ore"), COMMON_ORE);
        Registry.register(Registries.ITEM,
                new Identifier("gregvregs_rng", "common_ore"),
                new BlockItem(COMMON_ORE, new Item.Settings()));

        Registry.register(Registries.BLOCK,
                new Identifier("gregvregs_rng", "rare_ore"), RARE_ORE);
        Registry.register(Registries.ITEM,
                new Identifier("gregvregs_rng", "rare_ore"),
                new BlockItem(RARE_ORE, new Item.Settings()));
    }
}