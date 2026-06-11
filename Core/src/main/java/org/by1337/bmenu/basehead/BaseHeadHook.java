package org.by1337.bmenu.basehead;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.by1337.bmenu.BMenuApi;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Base64;
import java.util.UUID;

public class BaseHeadHook {

    public static ItemStack getItem(String argument) {
        if (argument == null) {
            BMenuApi.getMessage().error("material is null!");
            return new ItemStack(Material.PLAYER_HEAD);
        }

        if (argument.startsWith("basehead-")) {
            try {
                return SkullUtils.getSkull(argument.replace("basehead-", ""));
            } catch (Exception exception) {
                BMenuApi.getMessage().error(
                        "Something went wrong while trying to get base64 head: " + argument,
                        exception
                );
                return new ItemStack(Material.PLAYER_HEAD);
            }
        }

        try {
            return new ItemStack(Material.valueOf(argument));
        } catch (IllegalArgumentException e) {
            BMenuApi.getMessage().error(e);
            return new ItemStack(Material.DIRT);
        }
    }

    public static class SkullUtils {
        private static final boolean NEW_SKULL_API;

        static {
            boolean found = false;
            try {
                Class.forName("org.bukkit.profile.PlayerProfile");
                found = true;
            } catch (ClassNotFoundException ignored) {
            }
            NEW_SKULL_API = found;
        }

        @NotNull
        public static ItemStack getSkull(@NotNull String base64) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);

            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta == null)
                return head;

            try {
                String decoded = new String(Base64.getDecoder().decode(base64));
                String textureUrl = decoded.split("\"url\":\"")[1].split("\"")[0];

                if (NEW_SKULL_API) {
                    applyNewSkullApi(meta, textureUrl);
                } else {
                    applyOldSkullApi(meta, textureUrl);
                }

                head.setItemMeta(meta);
            } catch (Exception e) {
                BMenuApi.getMessage().error(e);
            }

            return head;
        }

        private static void applyNewSkullApi(SkullMeta meta, String textureUrl) throws Exception {
            Class<?> playerProfileClass = Class.forName("org.bukkit.profile.PlayerProfile");
            Class<?> playerTexturesClass = Class.forName("org.bukkit.profile.PlayerTextures");

            Method createPlayerProfile = org.bukkit.Bukkit.class.getMethod("createPlayerProfile", UUID.class);
            Object profile = createPlayerProfile.invoke(null, UUID.randomUUID());

            Method getTextures = playerProfileClass.getMethod("getTextures");
            Object textures = getTextures.invoke(profile);

            Method setSkin = playerTexturesClass.getMethod("setSkin", java.net.URL.class);
            setSkin.invoke(textures, new URI(textureUrl).toURL());

            Method setTextures = playerProfileClass.getMethod("setTextures", playerTexturesClass);
            setTextures.invoke(profile, textures);

            Method setOwnerProfile = SkullMeta.class.getMethod("setOwnerProfile", playerProfileClass);
            setOwnerProfile.invoke(meta, profile);
        }

        private static void applyOldSkullApi(SkullMeta meta, String textureUrl) throws Exception {
            com.mojang.authlib.GameProfile gameProfile = new com.mojang.authlib.GameProfile(UUID.randomUUID(), "BAuctionHead");
            String textureJson = "{textures:{SKIN:{url:\"" + textureUrl + "\"}}}";
            byte[] encodedData = Base64.getEncoder().encode(textureJson.getBytes("UTF-8"));
            gameProfile.getProperties().put("textures", new com.mojang.authlib.properties.Property("textures", new String(encodedData, "UTF-8")));
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, gameProfile);
        }
    }
}