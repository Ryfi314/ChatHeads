package me.ryfi.chatheads.mixins;

import me.ryfi.chatheads.ChatHeads;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ChatOptionsScreen;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleOptionsScreen.class)
public class SimpleOptionsScreenMixin extends Screen {


    protected SimpleOptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    public void init(CallbackInfo ci) {
        if (((SimpleOptionsScreen) (Object) this) instanceof ChatOptionsScreen) {

            this.addDrawableChild(
                    new ButtonWidget(
                            20,
                            this.height - 27,
                            width / 6,
                            20,
                            Text.translatable("text.autoconfig.chatheads.title"),
                            button ->
                                    client.setScreen(ChatHeads.getConfigScreen(((SimpleOptionsScreen) (Object) this)))
                    )
            );


        }
    }


}
