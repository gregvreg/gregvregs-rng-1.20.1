package name.modid;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.Map;

public class HudOverlay {

    public static boolean visible = true;

    private static final int COLOR_RARE   = 0xFFAA00FF;
    private static final int COLOR_COMMON = 0xFFAAAAAA;
    private static final int COLOR_TITLE  = 0xFFFFFFFF;
    private static final int COLOR_BG     = 0xAA000000;

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!visible) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            if (!client.player.getWorld().getRegistryKey()
                    .equals(ItemTeleport.LUNA_REFUGE_KEY)) return;

            // Ne rien afficher si aucun bloc miné
            long nonZero = ModStats.COUNTS.values().stream().filter(v -> v > 0).count();
            if (nonZero == 0) return;

            int x = 8;
            int y = 8;
            int lineH = 11;
            int padding = 4;

            int panelH = padding + lineH + ((int) nonZero * lineH) + padding;
            int panelW = 140;

            drawContext.fill(x - padding, y - padding,
                    x + panelW, y + panelH, COLOR_BG);

            drawContext.drawTextWithShadow(client.textRenderer,
                    "§f§lGregvregs RNG", x, y, COLOR_TITLE);
            y += lineH + 2;

            drawContext.fill(x - padding, y, x + panelW, y + 1, 0x55FFFFFF);
            y += 4;

            for (Map.Entry<String, Integer> entry : ModStats.COUNTS.entrySet()) {
                String id = entry.getKey();
                int count = entry.getValue();

                if (count == 0) continue;   

                int color;
                String label;

                switch (id) {
                    case "rare_ore"   -> { color = COLOR_RARE;   label = "Rare Crystal"; }
                    case "common_ore" -> { color = COLOR_COMMON; label = "Common Stone"; }
                    case "moon_stone" -> { color = 0xFFCCCCCC;   label = "Moon Stone";   }
                    default           -> { color = COLOR_TITLE;  label = id; }
                }

                drawContext.drawTextWithShadow(client.textRenderer,
                        label, x, y, color);

                String countStr = String.valueOf(count);
                int textW = client.textRenderer.getWidth(countStr);
                drawContext.drawTextWithShadow(client.textRenderer,
                        countStr, x + panelW - textW, y, color);

                y += lineH;
            }
        });
    }
}