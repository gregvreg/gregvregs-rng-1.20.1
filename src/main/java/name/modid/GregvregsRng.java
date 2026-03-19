package name.modid;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GregvregsRng implements ModInitializer {
	public static final String MOD_ID = "gregvregs_rng";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.registerBlocks();
		ModItems.registerItems();
		RngMiningListener.register();
		LunaSpawnGenerator.register();
		ModPickaxes.registerPickaxes();
		LOGGER.info("GregvregsRng mod loaded !");
	}
}