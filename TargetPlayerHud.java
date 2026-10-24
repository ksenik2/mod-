package dev.playerinfohud;

import dev.playerinfohud.config.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Locale;

/**
 * Maly niebieski panel obok celownika, gdy patrzysz na innego gracza:
 * nick, serca (z paskiem), armor, przedmiot w glownej rece i - jesli cos w niej jest - w drugiej.
 */
public class TargetPlayerHud implements HudElement {
	private static final int BG_TOP = 0xE00D1F45;
	private static final int BG_BOTTOM = 0xE0050B1C;
	private static final int WHITE = 0xFFFFFFFF;
	private static final int ICE = 0xFFBFE7FF;
	private static final int DIM = 0xFF7FA7E6;
	private static final int ABSORB = 0xFF8B5CF6;

	private static final int PAD = 5;
	private static final int LINE = 11;
	private static final int ICON = 16;

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		ModConfig cfg = ModConfig.get();
		Minecraft mc = Minecraft.getInstance();
		if (!cfg.hudEnabled || mc.player == null || mc.level == null || mc.options.hideGui || mc.screen != null) {
			return;
		}

		HitResult hit = mc.hitResult;
		if (!(hit instanceof EntityHitResult entityHit)) {
			return;
		}
		if (!(entityHit.getEntity() instanceof Player target) || target == mc.player) {
			return;
		}

		Font font = mc.font;
		int accent = ModConfig.argb(ModConfig.PALETTE[cfg.hudColorIndex]);

		Component name = target.getName();
		float health = target.getHealth();
		float maxHealth = Math.max(1.0F, target.getMaxHealth());
		float absorption = target.getAbsorptionAmount();
		String hearts = "♥ " + fmt(health / 2.0F) + " / " + fmt(maxHealth / 2.0F)
				+ (absorption > 0.0F ? "  +" + fmt(absorption / 2.0F) : "");
		String armor = "⛨ " + target.getArmorValue();

		ItemStack mainHand = target.getMainHandItem();
		ItemStack offHand = target.getOffhandItem();
		boolean showMain = cfg.hudShowHands && !mainHand.isEmpty();
		boolean showOff = cfg.hudShowHands && !offHand.isEmpty();

		// ---- wymiary ----
		int width = Math.max(font.width(name), Math.max(font.width(hearts), font.width(armor)));
		width = Math.max(width, 70);
		if (showMain) width = Math.max(width, ICON + 4 + font.width(mainHand.getHoverName()));
		if (showOff) width = Math.max(width, ICON + 4 + font.width(offHand.getHoverName()));
		width += PAD * 2 + 3; // +3 na pasek akcentu

		int height = PAD * 2 + LINE * 2 + 6 /* pasek zycia */ + LINE
				+ (showMain ? ICON + 2 : 0) + (showOff ? ICON + 2 : 0);

		int x = graphics.guiWidth() / 2 + 14;
		int y = graphics.guiHeight() / 2 - height / 2;

		// ---- tlo: gradient + ramka + pasek akcentu ----
		for (int i = 0; i < height; i++) {
			graphics.fill(x, y + i, x + width, y + i + 1, lerpColor(BG_TOP, BG_BOTTOM, i / (float) Math.max(1, height - 1)));
		}
		graphics.fill(x - 1, y - 1, x + width + 1, y, 0xFF050B1C);
		graphics.fill(x - 1, y + height, x + width + 1, y + height + 1, 0xFF050B1C);
		graphics.fill(x - 1, y, x, y + height, 0xFF050B1C);
		graphics.fill(x + width, y, x + width + 1, y + height, 0xFF050B1C);
		graphics.fill(x, y, x + 3, y + height, accent);

		int cx = x + 3 + PAD;
		int cy = y + PAD;

		// ---- nick ----
		graphics.text(font, name, cx, cy, WHITE, true);
		cy += LINE;

		// ---- serca + pasek ----
		graphics.text(font, hearts, cx, cy, absorption > 0.0F ? ABSORB : ICE, true);
		cy += LINE;
		int barW = width - 3 - PAD * 2;
		float frac = Mth.clamp(health / maxHealth, 0.0F, 1.0F);
		graphics.fill(cx, cy, cx + barW, cy + 3, 0xFF14233F);
		graphics.fill(cx, cy, cx + Math.round(barW * frac), cy + 3, accent);
		if (absorption > 0.0F) {
			int extra = Math.round(barW * Mth.clamp(absorption / maxHealth, 0.0F, 1.0F));
			graphics.fill(cx, cy + 3, cx + extra, cy + 4, ABSORB);
		}
		cy += 6;

		// ---- armor ----
		graphics.text(font, armor, cx, cy, DIM, true);
		cy += LINE + 1;

		// ---- przedmioty ----
		if (showMain) {
			drawItemRow(graphics, font, mainHand, cx, cy);
			cy += ICON + 2;
		}
		if (showOff) {
			drawItemRow(graphics, font, offHand, cx, cy);
		}
	}

	private static void drawItemRow(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int x, int y) {
		graphics.item(stack, x, y);
		graphics.itemDecorations(font, stack, x, y);
		graphics.text(font, stack.getHoverName(), x + ICON + 4, y + (ICON - 8) / 2, WHITE, true);
	}

	private static String fmt(float value) {
		if (value == (int) value) {
			return Integer.toString((int) value);
		}
		return String.format(Locale.ROOT, "%.1f", value);
	}

	private static int lerpColor(int a, int b, float t) {
		int aa = (a >>> 24) & 0xFF, ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
		int ba = (b >>> 24) & 0xFF, br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
		return ((int) (aa + (ba - aa) * t) << 24) | ((int) (ar + (br - ar) * t) << 16)
				| ((int) (ag + (bg - ag) * t) << 8) | (int) (ab + (bb - ab) * t);
	}
}
