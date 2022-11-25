package me.ryfi.chatheads

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.mojang.brigadier.CommandDispatcher
import me.ryfi.chatheads.util.Settings
import me.ryfi.chatheads.util.Version
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.impl.networking.NetworkingImpl
import net.fabricmc.fabric.impl.networking.server.ServerPlayNetworkAddon
import net.fabricmc.fabric.impl.screenhandler.Networking
import net.minecraft.client.gui.screen.Screen
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import java.util.concurrent.TimeUnit

object ChatHeads : ClientModInitializer {
    @JvmStatic
    var REPO_URL = "https://raw.githubusercontent.com/Ryfi314/ChatHeads/1.19.2"

    // Список сообщений
    lateinit var messageCache: Cache<String, List<String>>

    @JvmStatic
    lateinit var settings: Settings

    override fun onInitializeClient() {
        Version.checkForUpdates()
        AutoConfig.register(
            Settings::class.java
        ) { definition: Config?, configClass: Class<Settings?>? ->
            JanksonConfigSerializer(
                definition,
                configClass
            )
        }

        val configHolder = AutoConfig.getConfigHolder(Settings::class.java)
        configHolder
            .registerSaveListener { cfgh,
                                    newSettings ->

                messageCache =
                    CacheBuilder.newBuilder().expireAfterWrite(newSettings.chatRenderDelay, TimeUnit.SECONDS).build()
                ActionResult.SUCCESS
            }

        settings = configHolder.config

        messageCache = CacheBuilder.newBuilder().expireAfterWrite(settings.chatRenderDelay, TimeUnit.SECONDS).build()
        
    }

    @JvmStatic
    fun getConfigScreen(parent: Screen): Screen {
        return AutoConfig.getConfigScreen(Settings::class.java, parent).get()
    }


}