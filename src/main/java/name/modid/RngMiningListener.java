package name.modid;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RngMiningListener {

    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Random RANDOM = new Random();
    private static final Set<BlockPos> EXCAVATED = new HashSet<>();

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {

            if (world.isClient()) return;
            if (!world.getRegistryKey().equals(ItemTeleport.LUNA_REFUGE_KEY)) return;

            ServerWorld serverWorld = (ServerWorld) world;
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

            ItemStack tool = serverPlayer.getMainHandStack();
            if (!(tool.getItem() instanceof LunaPickaxe)) return;

            if (state.isOf(ModBlocks.MOON_STONE)) {
                ModStats.increment("moon_stone");
            } else if (state.isOf(ModBlocks.GARNET)) {
                ModStats.increment("garnet");
            } else if (state.isOf(ModBlocks.ULTRANIUM)) {
                ModStats.increment("ultranium");
            } else {
                return;
            }

            EXCAVATED.add(pos.toImmutable());

            float luckMultiplier = getLuckMultiplier(serverPlayer);

            for (Direction dir : DIRECTIONS) {
                BlockPos neighbor = pos.offset(dir);
                BlockState current = serverWorld.getBlockState(neighbor);

                if (neighbor.getY() >= 62) continue;
                if (EXCAVATED.contains(neighbor)) continue;
                if (!current.isAir()) continue;

                Block newBlock = rollRng(serverPlayer, luckMultiplier);
                serverWorld.setBlockState(neighbor, newBlock.getDefaultState());
            }
        });
    }

    private static float getLuckMultiplier(ServerPlayerEntity player) {
        ItemStack main = player.getMainHandStack();
        if (main.getItem() instanceof LunaPickaxe pickaxe) {
            return pickaxe.luckMultiplier;
        }
        return 1.0f;
    }

    private static Block rollRng(ServerPlayerEntity player, float luckMultiplier) {
        float rareChance = luckMultiplier / 50.0f;
        if (RANDOM.nextFloat() < rareChance) {
            player.sendMessage(
                    Text.literal("§d✦ §5ULTRANIUM §d✦ §fTu as trouvé de l'§5Ultranium§f !"),
                    false
            );
            player.getWorld().playSound(
                    null,
                    player.getBlockPos(),
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                    SoundCategory.PLAYERS,
                    1.0f, 0.5f
            );
            return ModBlocks.ULTRANIUM;
        }

        float commonChance = luckMultiplier / 3.0f;
        if (RANDOM.nextFloat() < commonChance) {
            return ModBlocks.GARNET;
        }

        return ModBlocks.MOON_STONE;
    }
}