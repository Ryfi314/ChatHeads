package me.ryfi.chatheads.mixins;

import me.ryfi.chatheads.chat.ChatManager;
import me.ryfi.chatheads.util.Version;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(at = @At("HEAD"), method = "onGameMessage")
    public void onChatMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        ChatManager.handleChatMessage(packet.content().getString());
    }

    @Inject(at = @At("TAIL"), method = "onGameJoin")
    private void onJoin(GameJoinS2CPacket packet, CallbackInfo ci){
        if(Version.hasUpdates()){
            MinecraftClient.getInstance().player.sendMessage(
                    Text.of(
                          "Вышла новая версия ChatHeads!\nОбновитесь!"
                    ),
                    false
            );
        }
    }

}
