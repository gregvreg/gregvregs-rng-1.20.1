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

    public static final Block GARNET = new Block(
            FabricBlockSettings.copyOf(Blocks.STONE).strength(1.5f, 4.0f)
    );

    public static final Block ULTRANIUM = new Block(
            FabricBlockSettings.copyOf(Blocks.AMETHYST_BLOCK).strength(3.0f, 8.0f)
    );

    public static void registerBlocks() {
        Registry.register(Registries.BLOCK,
                new Identifier("gregvregs_rng", "moon_stone"), MOON_STONE);
        Registry.register(Registries.ITEM,
                new Identifier("gregvregs_rng", "moon_stone"),
                new BlockItem(MOON_STONE, new Item.Settings()));

        Registry.register(Registries.BLOCK,
                new Identifier("gregvregs_rng", "garnet"), GARNET);
        Registry.register(Registries.ITEM,
                new Identifier("gregvregs_rng", "garnet"),
                new BlockItem(GARNET, new Item.Settings()));

        Registry.register(Registries.BLOCK,
                new Identifier("gregvregs_rng", "ultranium"), ULTRANIUM);
        Registry.register(Registries.ITEM,
                new Identifier("gregvregs_rng", "ultranium"),
                new BlockItem(ULTRANIUM, new Item.Settings()));
    }
}