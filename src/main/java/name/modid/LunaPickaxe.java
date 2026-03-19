package name.modid;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class LunaPickaxe extends PickaxeItem {

    public final float luckMultiplier;
    public final int explosionType; // 0 = aucun, 1 = cube 3x3x3, 2 = boules 8 directions
    public final float procRate;

    private static final Random RANDOM = new Random();

    public LunaPickaxe(ToolMaterial material, int attackDamage, float attackSpeed,
                       Settings settings, float luckMultiplier, int explosionType, float procRate) {
        super(material, attackDamage, attackSpeed, settings);
        this.luckMultiplier = luckMultiplier;
        this.explosionType = explosionType;
        this.procRate = procRate;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state,
                            BlockPos pos, PlayerEntity miner) {
        if (world.isClient()) return super.postMine(stack, world, state, pos, miner);
        if (!world.getRegistryKey().equals(ItemTeleport.LUNA_REFUGE_KEY))
            return super.postMine(stack, world, state, pos, miner);

        // Vérifier si c'est un bloc du mod
        boolean isModBlock = state.isOf(ModBlocks.MOON_STONE)
                || state.isOf(ModBlocks.COMMON_ORE)
                || state.isOf(ModBlocks.RARE_ORE);
        if (!isModBlock) return super.postMine(stack, world, state, pos, miner);

        ServerWorld serverWorld = (ServerWorld) world;
        ServerPlayerEntity player = (ServerPlayerEntity) miner;

        // Proc explosion
        if (explosionType > 0 && RANDOM.nextFloat() < procRate) {
            if (explosionType == 1) {
                explodeCube(serverWorld, player, pos);
            } else if (explosionType == 2) {
                explodeBalls(serverWorld, player, pos);
            }
        }

        return super.postMine(stack, world, state, pos, miner);
    }

    // Nostalgic Axe — cube 3x3x3
    private void explodeCube(ServerWorld world, ServerPlayerEntity player, BlockPos center) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos target = center.add(x, y, z);
                    if (target.equals(center)) continue;
                    mineBlock(world, player, target);
                }
            }
        }
    }

    // Nilaxe — 8 boules dans toutes les directions + diagonales sur 5 blocs
    private void explodeBalls(ServerWorld world, ServerPlayerEntity player, BlockPos center) {
        // Les 8 directions diagonales horizontales + haut/bas = 26 directions
        int[][] directions = {
                {1,0,0},{-1,0,0},{0,0,1},{0,0,-1},   // 4 cardinales
                {0,1,0},{0,-1,0},                       // haut/bas
                {1,0,1},{1,0,-1},{-1,0,1},{-1,0,-1},   // 4 diagonales horizontales
                {1,1,0},{1,-1,0},{-1,1,0},{-1,-1,0},   // 4 diagonales verticales
                {0,1,1},{0,1,-1},{0,-1,1},{0,-1,-1},   // 4 autres diagonales verticales
                {1,1,1},{1,1,-1},{1,-1,1},{1,-1,-1},   // 4 coins
                {-1,1,1},{-1,1,-1},{-1,-1,1},{-1,-1,-1} // 4 autres coins
        };

        // Choisir 8 directions aléatoires parmi les 26
        java.util.List<int[]> dirList = new java.util.ArrayList<>(java.util.Arrays.asList(directions));
        java.util.Collections.shuffle(dirList, RANDOM);

        for (int i = 0; i < 8; i++) {
            int[] dir = dirList.get(i);
            // Chaque boule parcourt 5 blocs dans sa direction
            for (int dist = 1; dist <= 5; dist++) {
                BlockPos target = center.add(
                        dir[0] * dist,
                        dir[1] * dist,
                        dir[2] * dist
                );
                // Stoppe si elle touche de l'air (déjà creusé)
                if (world.getBlockState(target).isAir()) break;
                mineBlock(world, player, target);
            }
        }
    }

    // Mine un bloc et déclenche le RNG dessus
    private void mineBlock(ServerWorld world, ServerPlayerEntity player, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        // Ne mine que les blocs du mod
        if (!state.isOf(ModBlocks.MOON_STONE)
                && !state.isOf(ModBlocks.COMMON_ORE)
                && !state.isOf(ModBlocks.RARE_ORE)) return;

        // Empêcher de miner là où le joueur se trouve
        BlockPos feet = player.getBlockPos();
        BlockPos head = feet.up();
        if (pos.equals(feet) || pos.equals(head)) return;

        // Casser le bloc
        world.breakBlock(pos, true, player);
    }
}