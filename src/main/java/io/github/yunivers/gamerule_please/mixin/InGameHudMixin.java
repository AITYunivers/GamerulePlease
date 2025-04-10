package io.github.yunivers.gamerule_please.mixin;

import io.github.yunivers.gamerule_please.GamerulePlease;
import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.ScreenScaler;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin extends DrawContext
{
    private boolean originalDebugHudValue;
    @Shadow private Minecraft minecraft;

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
            shift = At.Shift.AFTER, // As to not conflict with BetterF3
            ordinal = 2),
        remap = false)
    private void doRender(float tickDelta, boolean screenOpen, int mouseX, int mouseY, CallbackInfo ci) {
        if (Config.Gamerules.misc.reducedDebugInfo && minecraft.options.debugHud)
        {
            originalDebugHudValue = true;
            minecraft.options.debugHud = false;
            renderDebug();
        }

        if ((Config.Gamerules.misc.showCoordinates || Config.Gamerules.misc.showDaysPlayed) && !screenOpen)
            renderCoordinatesAndDays();
    }

    @Unique
    private void renderDebug()
    {
        ScreenScaler screenScaler = new ScreenScaler(this.minecraft.options, this.minecraft.displayWidth, this.minecraft.displayHeight);
        int scaledWidth = screenScaler.getScaledWidth();

        GL11.glPushMatrix();
        if (Minecraft.failedSessionCheckTime > 0L) {
            GL11.glTranslatef(0.0F, 32.0F, 0.0F);
        }

        TextRenderer textRenderer = this.minecraft.textRenderer;
        textRenderer.drawWithShadow("Minecraft Beta 1.7.3 (" + this.minecraft.debugText + ")", 2, 2, 16777215);
        textRenderer.drawWithShadow(this.minecraft.getRenderChunkDebugInfo(), 2, 12, 16777215);
        textRenderer.drawWithShadow(this.minecraft.getRenderEntityDebugInfo(), 2, 22, 16777215);
        textRenderer.drawWithShadow(this.minecraft.getWorldDebugInfo(), 2, 32, 16777215);
        long var26 = Runtime.getRuntime().maxMemory();
        long var37 = Runtime.getRuntime().totalMemory();
        long var46 = Runtime.getRuntime().freeMemory();
        long var21 = var37 - var46;
        String var23 = "Used memory: " + var21 * 100L / var26 + "% (" + var21 / 1024L / 1024L + "MB) of " + var26 / 1024L / 1024L + "MB";
        this.drawTextWithShadow(textRenderer, var23, scaledWidth - textRenderer.getWidth(var23) - 2, 2, 14737632);
        var23 = "Allocated memory: " + var37 * 100L / var26 + "% (" + var37 / 1024L / 1024L + "MB)";
        this.drawTextWithShadow(textRenderer, var23, scaledWidth - textRenderer.getWidth(var23) - 2, 12, 14737632);
        GL11.glPopMatrix();
    }

    @Unique
    private void renderCoordinatesAndDays()
    {
        if (originalDebugHudValue || minecraft.options.debugHud)
            return;

        TextRenderer textRenderer = this.minecraft.textRenderer;
        int y = 34;
        int bgColor = 0xB1000000;
        if (Config.Gamerules.misc.showCoordinates)
        {
            String coordinateText = "Position: " + (int)minecraft.player.x + ", " + (int)(minecraft.player.y - minecraft.player.standingEyeHeight) + ", " + (int)minecraft.player.z;
            int coordinateWidth = textRenderer.getWidth(coordinateText);
            int coordinateHeight = textRenderer.splitAndGetHeight(coordinateText, coordinateWidth) / 2;
            fill(0, y, coordinateWidth + 4, y + coordinateHeight + 4, bgColor);
            textRenderer.drawWithShadow(coordinateText, 2, y + 2, 0xFFFFFF);
            y += coordinateHeight + 4;
        }
        if (Config.Gamerules.misc.showDaysPlayed && !minecraft.world.isRemote)
        {
            String coordinateText = "Days played: " + GamerulePlease.currentDays;
            int coordinateWidth = textRenderer.getWidth(coordinateText);
            int coordinateHeight = textRenderer.splitAndGetHeight(coordinateText, coordinateWidth) / 2;
            fill(0, y, coordinateWidth + 4, y + coordinateHeight + 4, bgColor);
            textRenderer.drawWithShadow(coordinateText, 2, y + 2, 0xFFFFFF);
        }
    }

    @Inject(
        method = "render",
        at = @At("TAIL")
    )
    private void afterRender(CallbackInfo ci)
    {
        if (originalDebugHudValue)
            minecraft.options.debugHud = true;
        originalDebugHudValue = false;
    }
}
