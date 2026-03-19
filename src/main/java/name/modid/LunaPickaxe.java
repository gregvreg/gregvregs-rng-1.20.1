package name.modid;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class LunaPickaxe extends PickaxeItem {

    public final float luckMultiplier;
    public final int explosionType;
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

        boolean isModBlock = state.isOf(ModBlocks.MOON_STONE)
                || state.isOf(ModBlocks.GARNET)
                || state.isOf(ModBlocks.ULTRANIUM);
        if (!isModBlock) return super.postMine(stack, world, state, pos, miner);

        ServerWorld serverWorld = (ServerWorld) world;
        ServerPlayerEntity player = (ServerPlayerEntity) miner;

        if (explosionType > 0 && RANDOM.nextFloat() < procRate) {
            if (explosionType == 1) {
                explodeCube(serverWorld, player, pos);
            } else if (explosionType == 2) {
                explodeBalls(serverWorld, player, pos);
            }
        }

        return super.postMine(stack, world, state, pos, miner);
    }

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

    private void explodeBalls(ServerWorld world, ServerPlayerEntity player, BlockPos center) {
        int[][] directions = {
                {1,0,0},{-1,0,0},{0,0,1},{0,0,-1},
                {0,1,0},{0,-1,0},
                {1,0,1},{1,0,-1},{-1,0,1},{-1,0,-1},
                {1,1,0},{1,-1,0},{-1,1,0},{-1,-1,0},
                {0,1,1},{0,1,-1},{0,-1,1},{0,-1,-1},
                {1,1,1},{1,1,-1},{1,-1,1},{1,-1,-1},
                {-1,1,1},{-1,1,-1},{-1,-1,1},{-1,-1,-1}
        };

        java.util.List<int[]> dirList = new java.util.ArrayList<>(java.util.Arrays.asList(directions));
        java.util.Collections.shuffle(dirList, RANDOM);

        for (int i = 0; i < 8; i++) {
            int[] dir = dirList.get(i);
            for (int dist = 1; dist <= 5; dist++) {
                BlockPos target = center.add(dir[0]*dist, dir[1]*dist, dir[2]*dist);
                if (world.getBlockState(target).isAir()) break;
                mineBlock(world, player, target);
            }
        }
    }

    private void mineBlock(ServerWorld world, ServerPlayerEntity player, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!state.isOf(ModBlocks.MOON_STONE)
                && !state.isOf(ModBlocks.GARNET)
                && !state.isOf(ModBlocks.ULTRANIUM)) return;

        BlockPos feet = player.getBlockPos();
        BlockPos head = feet.up();
        if (pos.equals(feet) || pos.equals(head)) return;

        world.breakBlock(pos, true, player);
    }
}