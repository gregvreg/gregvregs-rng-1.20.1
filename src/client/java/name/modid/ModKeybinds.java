package name.modid;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {

    public static void register() {
        KeyBinding toggleHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gregvregs_rng.toggle_hud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "Gregvregs RNG"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHud.wasPressed()) {
                HudOverlay.visible = !HudOverlay.visible;
            }
        });
    }
}