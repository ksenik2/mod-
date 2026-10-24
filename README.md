# Player Info HUD (Fabric, Minecraft 26.3)

Mod kliencki. Gdy celownik jest na innym graczu, obok niego pojawia sie maly panel:

- nick
- serca (np. `♥ 8.5 / 10`, zlote gdy gracz ma absorpcje)
- armor (punkty pancerza)
- przedmiot w glownej rece (z ikona, iloscia i paskiem wytrzymalosci)
- przedmiot w drugiej rece - tylko jesli cos w niej jest

## Wymagania

- Java 25
- Fabric Loader 0.19.5+
- Fabric API 0.165.0+26.3 (lub nowsze dla 26.3)

## Budowanie

```
./gradlew build
```

Gotowy plik: `build/libs/playerinfohud-1.0.0.jar` (wrzuc do `.minecraft/mods`).
Test w grze z IDE/konsoli: `./gradlew runClient`.

## Dostosowanie

W `TargetPlayerHud.java`:
- polozenie panelu: zmienne `x` i `y` w `extractRenderState`
- kolory: stale `BG`, `RED`, `GOLD`, `ARMOR` itd.
