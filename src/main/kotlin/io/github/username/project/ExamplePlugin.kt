package io.github.username.project

import net.minecraft.core.Holder
import net.minecraft.core.IRegistry
import net.minecraft.world.level.biome.BiomeBase
import net.minecraft.world.level.chunk.PalettedContainerRO
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.v1_21_R1.CraftChunkSnapshot
import org.bukkit.craftbukkit.v1_21_R1.util.CraftNamespacedKey
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.common.util.unsafeLazy
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.module.nms.nmsProxy

object ExamplePlugin : Plugin() {

    override fun onEnable() {
        info("Successfully running ExamplePlugin!")
    }
}

/**
 *
 *
 *
 *     public static Biome minecraftToBukkit(BiomeBase minecraft) {
 *         Preconditions.checkArgument(minecraft != null);
 *
 *         IRegistry<BiomeBase> registry =
 *              CraftRegistry.getMinecraftRegistry(Registries.aF);
 *
 *         Biome bukkit = (Biome)Registry.BIOME
 *              .get(CraftNamespacedKey.fromMinecraft(
 *                  ((ResourceKey)registry.d(minecraft).orElseThrow()).a()
 *              ));
 *
 *         return bukkit == null ? Biome.CUSTOM : bukkit;
 *         //因此 org.bukkit.block.Biome#getKey() 并没法获取命名空间 ID.
 *     }
 */





abstract class API {
    abstract fun getBiome(location: Location): NamespacedKey

    companion object {
        val instance by unsafeLazy { nmsProxy<API>() }
    }
}

class APIImpl : API() {
    override fun getBiome(location: Location): NamespacedKey {
        val x = location.blockX
        val y = location.blockY
        val z = location.blockZ

        val chunk = (location.chunk.chunkSnapshot as CraftChunkSnapshot)
        //直接把 bukkit 的区块快照转成 CraftBukkit 的区块快照
        //
        // org.bukkit.craftbukkit.v1_21_R1.CraftChunkSnapshot
        // implements org.bukkit.ChunkSnapshot
        //
        // CraftChunkSnapshot 是 ChunkSnapshot 在 craftbukkit 服务端内部的实现

        val biomeRegistry = chunk.getProperty<IRegistry<BiomeBase>>("biomeRegistry")
        // 获取注册信息
        val biomePCR = chunk.getProperty<Array<PalettedContainerRO<Holder<BiomeBase>>>>("biome")
        //获取储存生物群系的容器

        val castBiomePCR = biomePCR?.get(y - chunk.getProperty<Int>("minHeight")!! shr 4)
        val biome = castBiomePCR?.get(x shr 2, (y and 15) shr 2, z shr 2)?.value()
        //从容器中提取生物群系对象，做一些计算(由bukkit提供)

        val resourceKey = biomeRegistry?.getResourceKey(biome)?.get()
        //一个存储资源的键的对象，包括命名空间 ID 和位置(?)

        val key = CraftNamespacedKey.fromMinecraft(resourceKey?.registry())
        //CraftBukkit 直接把 nms 的命名空间 ID 转成 bukkit 的命名空间ID

        return key
    }
}