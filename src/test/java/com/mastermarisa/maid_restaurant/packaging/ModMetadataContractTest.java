package com.mastermarisa.maid_restaurant.packaging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.jupiter.api.Test;

public final class ModMetadataContractTest {
    ModMetadataContractTest() {
    }

    @Test
    void builtJarTargetsNeoForge1211() throws IOException {
        verify(
                Path.of(System.getProperty("maidRestaurant.jar")),
                System.getProperty("maidRestaurant.version")
        );
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected <jar-path> <mod-version>");
        }

        verify(Path.of(args[0]), args[1]);
    }

    private static void verify(Path jarPath, String expectedVersion) throws IOException {
        try (ZipFile jar = new ZipFile(jarPath.toFile())) {
            require(jar.getEntry("META-INF/mods.toml") == null,
                    "Legacy Forge metadata META-INF/mods.toml must not be packaged");

            ZipEntry metadataEntry = jar.getEntry("META-INF/neoforge.mods.toml");
            require(metadataEntry != null,
                    "NeoForge metadata META-INF/neoforge.mods.toml is missing");

            String metadata = new String(jar.getInputStream(metadataEntry).readAllBytes(), StandardCharsets.UTF_8);
            require(metadata.contains("license=\"BSD 3-Clause\""), "BSD 3-Clause license is missing");
            require(metadata.contains("modId=\"maid_restaurant\""), "Mod id is missing");
            require(metadata.contains("version=\"" + expectedVersion + "\""), "Mod version is stale");
            require(metadata.contains("modId=\"neoforge\""), "NeoForge dependency is missing");
            require(metadata.contains("versionRange=\"[1.21.1]\""), "Minecraft 1.21.1 range is missing");
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
