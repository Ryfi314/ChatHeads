package me.ryfi.chatheads.chat

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import me.ryfi.chatheads.util.chunkedMessage
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import java.util.concurrent.TimeUnit

object ChatManager {

    // Список сообщений
    var messageCache: Cache<String, List<String>> = CacheBuilder
        .newBuilder()
        .expireAfterWrite(3, TimeUnit.SECONDS)
        .build()


    @JvmStatic
    fun handleChatMessage(rawMessage: String) {

        val rawName = rawMessage.split(" ")[0]
        val name = rawName
            .replaceFirst("<", "")
            .replaceFirst(">:", "")

        if (name.length > 16 || name.length < 4) return

        val cookedMessage = rawMessage
            .replaceFirst(rawName, "")
            .replaceFirst(" ", "")

        if (rawMessage.length > 25) {
            messageCache.put(name, cookedMessage.chunkedMessage(25))
        } else {
            messageCache.put(name, listOf(cookedMessage))
        }
    }


    private fun getMessages(name: String): List<String>? {
        return messageCache.getIfPresent(name)
    }

    @JvmStatic
    fun render(
        renderDispatcher: EntityRenderDispatcher,
        entity: AbstractClientPlayerEntity?,
        matrices: MatrixStack?,
        vertexConsumerProvider: VertexConsumerProvider?,
        light: Int
    ) {
        val name = entity?.name ?: return
        val messages = getMessages(name.string) ?: return

        val g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f)
        val j = (g * 255.0f).toInt() shl 24

        val height = messages.size * 0.3f + entity.height + 0.2f

        matrices?.push()
        matrices?.translate(0.0, height.toDouble(), 0.0)
        matrices?.multiply(renderDispatcher.rotation)
        matrices?.scale(-0.025f, -0.025f, 0.025f)
        var i = 0f
        val textRenderer = MinecraftClient.getInstance().textRenderer
        for (msg in messages) {

            textRenderer.draw(
                msg,
                (-textRenderer.getWidth(msg)) / 2f,
                i,
                0xffffff,
                false,
                matrices?.peek()?.positionMatrix,
                vertexConsumerProvider,
                false,
                j,
                light
            )
            i += 10f
        }

        matrices?.pop()
    }

}