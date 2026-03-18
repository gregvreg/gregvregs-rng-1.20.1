package name.modid;

import net.fabricmc.api.ClientModInitializer;

public class GregvregsRngClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		HudOverlay.register();
		ModKeybinds.register();
	}
}