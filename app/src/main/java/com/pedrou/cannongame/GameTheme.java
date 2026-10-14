// GameTheme.java
// Holds the colours of the two visual themes of the game.
// [BONUS] item 6 "Modo Noturno / Modo Dia (troca de tema dinamica)".
package com.pedrou.cannongame;

public class GameTheme {

    public final String name;          // label shown on the HUD button
    public final boolean dark;         // true for the night theme

    public final int skyColor;         // background of the play field
    public final int horizonColor;     // band behind the targets
    public final int textColor;        // HUD text
    public final int cannonColor;      // cannon barrel and base
    public final int blockerColor;     // blocker
    public final int targetEvenColor;  // even-numbered targets
    public final int targetOddColor;   // odd-numbered targets
    public final int particleColor;    // explosion particles
    public final int buttonColor;      // HUD buttons
    public final int buttonTextColor;  // HUD button text

    private GameTheme(String name, boolean dark, int skyColor,
                      int horizonColor, int textColor, int cannonColor,
                      int blockerColor, int targetEvenColor,
                      int targetOddColor, int particleColor, int buttonColor,
                      int buttonTextColor) {
        this.name = name;
        this.dark = dark;
        this.skyColor = skyColor;
        this.horizonColor = horizonColor;
        this.textColor = textColor;
        this.cannonColor = cannonColor;
        this.blockerColor = blockerColor;
        this.targetEvenColor = targetEvenColor;
        this.targetOddColor = targetOddColor;
        this.particleColor = particleColor;
        this.buttonColor = buttonColor;
        this.buttonTextColor = buttonTextColor;
    }

    // daytime palette - keeps the colours used by the book (dark / light)
    public static final GameTheme DAY = new GameTheme(
            "DAY", false,
            0xFFEAF4FF, // sky
            0xFFBFE3FF, // horizon
            0xFF102030, // text
            0xFF101010, // cannon
            0xFF203040, // blocker
            0xFF1976D2, // target (even) - R.color.dark
            0xFFFFE100, // target (odd)  - R.color.light
            0xFF1976D2, // particles
            0xFFDDEBFF, // button background
            0xFF102030  // button text
    );

    // night-time palette
    public static final GameTheme NIGHT = new GameTheme(
            "NIGHT", true,
            0xFF05070F, // sky
            0xFF111A33, // horizon
            0xFFE8F1FF, // text
            0xFFD8E4F0, // cannon
            0xFF8A1C1C, // blocker
            0xFF00E5FF, // target (even)
            0xFFFF4D6D, // target (odd)
            0xFFFFC857, // particles
            0xFF16233F, // button background
            0xFFE8F1FF  // button text
    );

    public static final GameTheme[] ALL = { DAY, NIGHT };

    // returns the theme that follows the given one in the ALL array
    public static GameTheme next(GameTheme current) {
        for (int i = 0; i < ALL.length; i++) {
            if (ALL[i] == current) {
                return ALL[(i + 1) % ALL.length];
            }
        }
        return DAY;
    }
}
