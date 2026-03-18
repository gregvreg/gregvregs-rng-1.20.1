package name.modid;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemTeleport extends Item {

    public static final RegistryKey<World> LUNA_REFUGE_KEY = RegistryKey.of(
            RegistryKeys.WORLD,
            new Identifier("gregvregs_rng", "luna_refuge")
    );

    public ItemTeleport(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        ServerPlayerEntity player = (ServerPlayerEntity) user;
        ServerWorld lunaWorld = player.getServer().getWorld(LUNA_REFUGE_KEY);

        if (lunaWorld == null) {
            player.sendMessage(
                    Text.literal("§cErreur : Luna Refuge introuvable !"),
                    false
            );
            return TypedActionResult.fail(user.getStackInHand(hand));
        }

        if (world.getRegistryKey().equals(LUNA_REFUGE_KEY)) {
            ServerWorld overworld = player.getServer().getOverworld();
            player.teleport(
                    overworld,
                    player.getX(), player.getY(), player.getZ(),
                    player.getYaw(), player.getPitch()
            );
            player.sendMessage(Text.literal("§bRetour dans l'Overworld..."), false);
        } else {
            player.teleport(
                    lunaWorld,
                    0.5, 66.0, 0.5,
                    player.getYaw(), player.getPitch()
            );
            player.sendMessage(Text.literal("§5Bienvenue dans Luna Refuge !"), false);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}