package me.ryfi.chatheads.util

import java.io.File

interface IPlayerSkinProvider {
    fun getSkinCacheDir(): File?
}