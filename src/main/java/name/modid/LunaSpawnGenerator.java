package name.modid;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;

import java.io.InputStream;

public class LunaSpawnGenerator {

    private static boolean generated = false;

    public static void register() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (!world.getRegistryKey().equals(ItemTeleport.LUNA_REFUGE_KEY)) return;
            if (generated) return;
            generated = true;

            try {
                InputStream stream = LunaSpawnGenerator.class.getResourceAsStream(
                        "/data/gregvregs_rng/structures/luna_spawn.nbt"
                );

                if (stream == null) {
                    GregvregsRng.LOGGER.error("luna_spawn.nbt introuvable !");
                    return;
                }

                NbtCompound nbt = NbtIo.readCompressed(stream);

                // Utiliser le StructureTemplateManager du serveur
                StructureTemplate template = server.getStructureTemplateManager()
                        .createTemplate(nbt);

                BlockPos pos = new BlockPos(
                        -template.getSize().getX() / 2,
                        63,
                        -template.getSize().getZ() / 2
                );

                StructurePlacementData placementData = new StructurePlacementData();
                template.place(world, pos, pos, placementData, world.getRandom(), 2);

                GregvregsRng.LOGGER.info("Luna Spawn généré !");

            } catch (Exception e) {
                GregvregsRng.LOGGER.error("Erreur luna_spawn : " + e.getMessage());
            }
        });
    }
}