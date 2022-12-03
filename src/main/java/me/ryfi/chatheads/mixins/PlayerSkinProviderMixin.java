package me.ryfi.chatheads.mixins;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ryfi.chatheads.ChatHeads;
import me.ryfi.chatheads.util.IPlayerSkinProvider;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Mixin(PlayerSkinProvider.class)
public class PlayerSkinProviderMixin implements IPlayerSkinProvider {

    @Shadow
    @Final
    private File skinCacheDir;

    @Shadow
    @Final
    private TextureManager textureManager;
    // Это простая систама плащей, интересный факт, в plasmovoice это тоже присутствует :)
    // Список установленных плащей можно найти на гитхабе /capes/
    // Ryfi_Coder
    @Inject(at = @At("RETURN"), method = "loadSkin(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/texture/PlayerSkinProvider$SkinTextureAvailableCallback;Z)V")
    public void loadSkin(GameProfile profile, PlayerSkinProvider.SkinTextureAvailableCallback callback, boolean requireSecure, CallbackInfo ci) {
        Runnable runnable = () -> {
            final HttpURLConnection connection;
            try {
                URL url = new URL(ChatHeads.getREPO_URL() + "/capes/" + profile.getName() + ".png");
                MinecraftProfileTexture texture = new MinecraftProfileTexture(url.toString(), new HashMap<>());
                String string = Hashing.sha1().hashUnencodedChars(texture.getHash()).toString();
                Identifier identifier = new Identifier("skins/" + string);
                File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
                File file2 = new File(file, string);
                if (file2.exists()) {
                    if (System.currentTimeMillis() - file2.lastModified() < 86400L) {

                        RenderSystem.recordRenderCall(() -> {
                            PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(file2, texture.getUrl(), DefaultSkinHelper.getTexture(), false, () -> {
                                if (callback != null) {
                                    callback.onSkinTextureAvailable(MinecraftProfileTexture.Type.CAPE, identifier, texture);
                                }
                            });
                            this.textureManager.registerTexture(identifier, playerSkinTexture);
                        });
                        return;
                    } else {
                        file2.delete();
                    }
                }

                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                connection.setUseCaches(false);

                if (connection.getResponseCode() == 200) {
                    RenderSystem.recordRenderCall(() -> {
                        PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(file2, texture.getUrl(), DefaultSkinHelper.getTexture(), false, () -> {
                            if (callback != null) {
                                callback.onSkinTextureAvailable(MinecraftProfileTexture.Type.CAPE, identifier, texture);
                            }
                        });
                        this.textureManager.registerTexture(identifier, playerSkinTexture);
                    });

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Util.getMainWorkerExecutor().execute(runnable);

    }

    @Nullable
    @Override
    public File getSkinCacheDir() {
        return skinCacheDir;
    }
}
