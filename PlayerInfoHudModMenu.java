package dev.playerinfohud;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.playerinfohud.config.ConfigScreen;

/**
 * Wpis do Mod Menu - przycisk konfiguracji przy modzie.
 * Klasa jest ladowana tylko gdy Mod Menu jest zainstalowany.
 */
public class PlayerInfoHudModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ConfigScreen::new;
	}
}
