package dev.example.bf3weather;

import me.cominixo.betterf3.config.ModConfigFile;
import me.cominixo.betterf3.modules.BaseModule;
import me.cominixo.betterf3.modules.WeatherModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class WeatherExtension implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Wait until every mod (including BetterF3) has finished initializing.
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            boolean already = BaseModule.allModules.stream().anyMatch(m -> m instanceof WeatherModule);
            if (already) {
                return;
            }

            new WeatherModule().init(); // registers itself in BetterF3's module lists

            // BetterF3 loaded its config before this module existed,
            // so load it again to apply the saved settings to Weather.
            try {
                ModConfigFile.load(ModConfigFile.FileType.JSON);
            } catch (Exception e) {
                System.err.println("[BetterF3 Weather] Could not reload BetterF3 config: " + e);
            }
        });
    }
}