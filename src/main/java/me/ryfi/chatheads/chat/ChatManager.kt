package me.ryfi.chatheads.chat

import me.ryfi.chatheads.ChatHeads
import me.ryfi.chatheads.ChatHeads.messageCache
import me.ryfi.chatheads.util.chunkedMessage
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack


object ChatManager {


    @JvmStatic
    fun handleChatMessage(rawMessage: String) {

        val networkHandler = MinecraftClient.getInstance().networkHandler ?: return

        var name = ""
        var nameIndex = 1
        for (word in rawMessage.split(Regex("(§.)|[^\\w]"))) {
            if (word.isEmpty()) continue

            val playerEntry = networkHandler.getPlayerListEntry(word)
            if (playerEntry != null) {
                name = playerEntry.profile.name
                break
            }
            nameIndex++
        }

        if (name.isEmpty()) return
        if (name.length > 16 || name.length < 4) return

        val words = rawMessage.split(" ")
        val cookedMessage = words.toTypedArray().copyOfRange(nameIndex, words.size).joinToString(" ")

        if (cookedMessage.contains(name)) return

        val maxLineSize = ChatHeads.settings.maxLineSize

        if (rawMessage.length > maxLineSize) {
            messageCache.put(name, cookedMessage.chunkedMessage(maxLineSize))
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
        if (messages.isEmpty()) return

        val g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f)
        val j = (g * 255.0f).toInt() shl 24
        val chatTextSize = ChatHeads.settings.chatTextSize
        val height = messages.size * (0.3f * chatTextSize) + entity.height + 0.2f

        matrices?.push()
        matrices?.translate(0.0, height, 0.0)
        matrices?.multiply(renderDispatcher.rotation)
        matrices?.scale(
            (-0.025f * chatTextSize).toFloat(), (-0.025f * chatTextSize).toFloat(),
            (0.025f * chatTextSize).toFloat()
        )
        var i = 0f
        val textRenderer = MinecraftClient.getInstance().textRenderer
        for (msg in messages) {

            textRenderer.draw(
                msg,
                (-textRenderer.getWidth(msg)) / 2f,
                i,
                ChatHeads.settings.chatTextColor,
                false,
                matrices?.peek()?.positionMatrix,
                vertexConsumerProvider,
                false,
                j,
                light
            )
            i += (10 * chatTextSize).toFloat()
        }

        matrices?.pop()
    }

}