package me.cominixo.betterf3.modules;

import me.cominixo.betterf3.utils.DebugLine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.level.biome.Biome;

public class WeatherModule extends BaseModule {

    private final DebugLine state = new DebugLine("weather");
    private final DebugLine biomeLine = new DebugLine("weather_biome");
    private final DebugLine precip = new DebugLine("weather_precip");
    private final DebugLine rain = new DebugLine("weather_rain");
    private final DebugLine thunder = new DebugLine("weather_thunder");

    public WeatherModule() {
        this.defaultNameColor = TextColor.fromRgb(0x55FFFF);
        this.defaultValueColor = TextColor.fromRgb(0xFFFFFF);
        this.nameColor = this.defaultNameColor;
        this.valueColor = this.defaultValueColor;

        this.lines.add(this.state);
        this.lines.add(this.biomeLine);
        this.lines.add(this.precip);
        this.lines.add(this.rain);
        this.lines.add(this.thunder);
    }

    @Override
    public boolean updatesEveryFrame() {
        return true;
    }

    @Override
    public void update(Minecraft client) {
        ClientLevel level = client.level;
        if (level == null || client.player == null) {
            return;
        }

        BlockPos pos = client.player.blockPosition();
        Holder<Biome> holder = level.getBiome(pos);
        Biome biome = holder.value();
        Biome.Precipitation type = biome.getPrecipitationAt(pos, level.getSeaLevel());

        float rainLevel = level.getRainLevel(1.0F);
        float thunderLevel = level.getThunderLevel(1.0F);
        boolean stormy = rainLevel > 0.2F;
        boolean thundering = thunderLevel > 0.9F;

        String text;
        if (!stormy) {
            text = "Clear";
        } else if (type == Biome.Precipitation.SNOW) {
            text = thundering ? "Thundersnow" : "Snowing";
        } else if (type == Biome.Precipitation.RAIN) {
            text = thundering ? "Thunderstorm" : "Raining";
        } else {
            text = "Dry (no precipitation here)";
        }

        String precipText = switch (type) {
            case SNOW -> "Snow";
            case RAIN -> "Rain";
            default -> "None";
        };

        this.state.value(text);
        this.biomeLine.value(prettyName(holder.getRegisteredName()));
        this.precip.value(precipText);
        this.rain.value(Math.round(rainLevel * 100) + "%");
        this.thunder.value(Math.round(thunderLevel * 100) + "%");
    }

    /** "minecraft:snowy_plains" -> "Snowy Plains" */
    private static String prettyName(String registeredName) {
        String path = registeredName.contains(":")
                ? registeredName.substring(registeredName.indexOf(':') + 1)
                : registeredName;
        StringBuilder out = new StringBuilder();
        for (String word : path.split("_")) {
            if (word.isEmpty()) continue;
            if (out.length() > 0) out.append(' ');
            out.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return out.toString();
    }
}