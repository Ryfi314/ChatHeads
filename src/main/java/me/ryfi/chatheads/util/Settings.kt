package me.ryfi.chatheads.util

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry

@Config(name = "chatheads")
class Settings : ConfigData {
    var chatEnabled: Boolean = true

    @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
    var chatRenderDelay: Long = 3L

    @ConfigEntry.BoundedDiscrete(min = 5, max = 100)
    var maxLineSize: Int = 25

    @ConfigEntry.BoundedDiscrete(min = 1, max = 3)
    var chatTextSize: Double = 1.0

    @ConfigEntry.ColorPicker
    var chatTextColor: Int = 0xffffff
}