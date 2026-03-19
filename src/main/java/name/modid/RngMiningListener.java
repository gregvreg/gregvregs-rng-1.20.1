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
    public static final Set<BlockPos> EXCAVATED = new HashSet<>();

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {

            if (world.isClient()) return;
            if (!world.getRegistryKey().equals(ItemTeleport.LUNA_REFUGE_KEY)) return;

            ServerWorld serverWorld = (ServerWorld) world;
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

            ItemStack tool = serverPlayer.getMainHandStack();
            if (!(tool.getItem() instanceof LunaPickaxe pickaxe)) return;

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

            float luckMultiplier = pickaxe.luckMultiplier;

            // Explosion proc
            if (pickaxe.explosionType > 0 && RANDOM.nextFloat() < pickaxe.procRate) {
                if (pickaxe.explosionType == 1) {
                    explodeCube(serverWorld, serverPlayer, pos, luckMultiplier);
                } else if (pickaxe.explosionType == 2) {
                    explodeBalls(serverWorld, serverPlayer, pos, luckMultiplier);
                }
            }

            // Régénération RNG autour du bloc miné par le joueur
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

    // Nostalgic Axe — mine tout le cube 3x3x3 d'abord, puis génère le RNG
    private static void explodeCube(ServerWorld world, ServerPlayerEntity player,
                                    BlockPos center, float luckMultiplier) {
        // Étape 1 — miner tous les blocs
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos target = center.add(x, y, z);
                    if (target.equals(center)) continue;
                    if (target.getY() > 62) continue;

                    BlockState state = world.getBlockState(target);
                    if (!state.isOf(ModBlocks.MOON_STONE)
                            && !state.isOf(ModBlocks.GARNET)
                            && !state.isOf(ModBlocks.ULTRANIUM)) continue;

                    BlockPos feet = player.getBlockPos();
                    if (target.equals(feet) || target.equals(feet.up())) continue;

                    if (state.isOf(ModBlocks.MOON_STONE)) ModStats.increment("moon_stone");
                    else if (state.isOf(ModBlocks.GARNET)) ModStats.increment("garnet");
                    else if (state.isOf(ModBlocks.ULTRANIUM)) ModStats.increment("ultranium");

                    world.breakBlock(target, true, player);
                    EXCAVATED.add(target.toImmutable());
                }
            }
        }

        // Étape 2 — générer RNG autour de chaque bloc miné
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos mined = center.add(x, y, z);
                    if (!EXCAVATED.contains(mined.toImmutable())) continue;

                    for (Direction dir : Direction.values()) {
                        BlockPos neighbor = mined.offset(dir);
                        BlockState current = world.getBlockState(neighbor);

                        if (neighbor.getY() >= 62) continue;
                        if (EXCAVATED.contains(neighbor)) continue;
                        if (!current.isAir()) continue;

                        world.setBlockState(neighbor, rollRng(player, luckMultiplier).getDefaultState());
                    }
                }
            }
        }
    }

    // Nilaxe — 8 boules horizontales, mine tout d'abord puis génère RNG
    private static void explodeBalls(ServerWorld world, ServerPlayerEntity player,
                                     BlockPos center, float luckMultiplier) {
        int[][] directions = {
                {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1},
                {1, 0, 1}, {1, 0, -1}, {-1, 0, 1}, {-1, 0, -1}
        };

        // Étape 1 — miner tous les blocs sur les 8 chemins
        for (int[] dir : directions) {
            for (int dist = 1; dist <= 5; dist++) {
                BlockPos target = new BlockPos(
                        center.getX() + dir[0] * dist,
                        center.getY(),
                        center.getZ() + dir[2] * dist
                );
                if (target.getY() >= 62) continue;

                BlockState state = world.getBlockState(target);
                if (!state.isOf(ModBlocks.MOON_STONE)
                        && !state.isOf(ModBlocks.GARNET)
                        && !state.isOf(ModBlocks.ULTRANIUM)) continue;

                BlockPos feet = player.getBlockPos();
                if (target.equals(feet) || target.equals(feet.up())) continue;

                if (state.isOf(ModBlocks.MOON_STONE)) ModStats.increment("moon_stone");
                else if (state.isOf(ModBlocks.GARNET)) ModStats.increment("garnet");
                else if (state.isOf(ModBlocks.ULTRANIUM)) ModStats.increment("ultranium");

                world.breakBlock(target, true, player);
                EXCAVATED.add(target.toImmutable());
            }
        }

        // Étape 2 — générer RNG autour de chaque bloc miné
        for (int[] dir : directions) {
            for (int dist = 1; dist <= 5; dist++) {
                BlockPos mined = new BlockPos(
                        center.getX() + dir[0] * dist,
                        center.getY(),
                        center.getZ() + dir[2] * dist
                );
                if (!EXCAVATED.contains(mined.toImmutable())) continue;

                for (Direction d : Direction.values()) {
                    BlockPos neighbor = mined.offset(d);
                    BlockState current = world.getBlockState(neighbor);

                    if (neighbor.getY() >= 62) continue;
                    if (EXCAVATED.contains(neighbor)) continue;
                    if (!current.isAir()) continue;

                    world.setBlockState(neighbor, rollRng(player, luckMultiplier).getDefaultState());
                }
            }
        }
    }

    private static boolean mineBlock(ServerWorld world, ServerPlayerEntity player,
                                     BlockPos pos, float luckMultiplier) {
        BlockState state = world.getBlockState(pos);
        if (!state.isOf(ModBlocks.MOON_STONE)
                && !state.isOf(ModBlocks.GARNET)
                && !state.isOf(ModBlocks.ULTRANIUM)) return false;

        BlockPos feet = player.getBlockPos();
        if (pos.equals(feet) || pos.equals(feet.up())) return false;

        if (state.isOf(ModBlocks.MOON_STONE)) ModStats.increment("moon_stone");
        else if (state.isOf(ModBlocks.GARNET)) ModStats.increment("garnet");
        else if (state.isOf(ModBlocks.ULTRANIUM)) ModStats.increment("ultranium");

        world.breakBlock(pos, true, player);
        EXCAVATED.add(pos.toImmutable());

        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.offset(dir);
            BlockState current = world.getBlockState(neighbor);

            if (neighbor.getY() >= 62) continue;
            if (EXCAVATED.contains(neighbor)) continue;
            if (!current.isAir()) continue;

            world.setBlockState(neighbor, rollRng(player, luckMultiplier).getDefaultState());
        }

        return true;
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