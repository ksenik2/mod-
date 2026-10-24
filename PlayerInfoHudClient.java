package dev.playerinfohud;

import dev.playerinfohud.config.ModConfig;
import dev.playerinfohud.esp.EspHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

public class PlayerInfoHudClient implements ClientModInitializer {
	public static final String MOD_ID = "playerinfohud";

	@Override
	public void onInitializeClient() {
		ModConfig.get(); // wczytaj / utworz config

		HudElementRegistry.addLast(
				Identifier.fromNamespaceAndPath(MOD_ID, "esp"),
				new EspHud()
		);
		HudElementRegistry.addLast(
				Identifier.fromNamespaceAndPath(MOD_ID, "target_player_info"),
				new TargetPlayerHud()
		);
	}
}
