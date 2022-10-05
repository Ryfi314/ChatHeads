package me.ryfi.chatheads

import me.ryfi.chatheads.util.Version
import net.fabricmc.api.ModInitializer

object ChatHeads : ModInitializer {
    @JvmStatic
    var REPO_URL = "https://raw.githubusercontent.com/Ryfi314/ChatHeads/1.19.2"

    override fun onInitialize() {
        Version.checkForUpdates()
    }

}