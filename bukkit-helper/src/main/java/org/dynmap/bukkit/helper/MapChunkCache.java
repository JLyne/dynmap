package org.dynmap.bukkit.helper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.dynmap.DynmapChunk;
import org.dynmap.common.BiomeMap;
import org.dynmap.common.chunk.GenericChunk;
import org.dynmap.common.chunk.GenericChunkCache;
import org.dynmap.common.chunk.GenericMapChunkCache;

import net.minecraft.nbt.CompoundTag;
import org.dynmap.renderer.DynmapBlockState;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * Container for managing chunks - dependent upon using chunk snapshots, since rendering is off server thread
 */
public class MapChunkCache extends GenericMapChunkCache {
    private static final AsyncChunkProvider provider = new AsyncChunkProvider();
    private World w;
    /**
     * Construct empty cache
     */
    public MapChunkCache(GenericChunkCache cc) {
        super(cc);
    }

    // Load generic chunk from existing and already loaded chunk
    @Override
    protected Supplier<GenericChunk> getLoadedChunkAsync(DynmapChunk chunk) {
        Supplier<CompoundTag> supplier = provider.getLoadedChunk((CraftWorld) w, chunk.x, chunk.z);
        return () -> {
            CompoundTag nbt = supplier.get();
            return nbt != null ? parseChunkFromNBT(new NBT.NBTCompound(nbt)) : null;
        };
    }

    // Load generic chunk from unloaded chunk
    @Override
    protected Supplier<GenericChunk> loadChunkAsync(DynmapChunk chunk){
        try {
            CompletableFuture<CompoundTag> nbt = provider.getChunk(((CraftWorld) w).getHandle(), chunk.x, chunk.z);
            return () -> {
                CompoundTag compound;
                try {
                    compound = nbt.get();
                } catch (InterruptedException e) {
                    return null;
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
                return compound == null ? null : parseChunkFromNBT(new NBT.NBTCompound(compound));
            };
        } catch (InvocationTargetException | IllegalAccessException ignored) {
            return () -> null;
        }
    }

    // 26.3+ chunk palettes omit the properties of a block in its default state and otherwise list all of them.
    // Resolve the entry through the game registry (default state + explicit properties) and map the exact
    // BlockState to its Dynmap state, instead of guessing from the Dynmap name table.
    private static final ConcurrentHashMap<String, DynmapBlockState> paletteStateCache = new ConcurrentHashMap<>();

    @Override
    protected DynmapBlockState lookupBlockState(String name, String statestr) {
        String key = ((statestr == null) || statestr.isEmpty()) ? name : name + "[" + statestr + "]";
        DynmapBlockState dbs = paletteStateCache.get(key);
        if (dbs != null) return dbs;
        BlockState bs = resolveGameBlockState(name, statestr);
        if ((bs != null) && (BukkitVersionHelper.dataToState != null)) {
            dbs = BukkitVersionHelper.dataToState.get(bs);
        }
        if (dbs == null) {
            dbs = super.lookupBlockState(name, statestr);
        }
        if (dbs != null) {
            paletteStateCache.put(key, dbs);
        }
        return dbs;
    }

    private static BlockState resolveGameBlockState(String name, String statestr) {
        Identifier id = Identifier.tryParse(name);
        if (id == null) return null;
        Optional<Block> blk = BuiltInRegistries.BLOCK.getOptional(id);
        if (!blk.isPresent()) return null;
        BlockState bs = blk.get().defaultBlockState();
        if ((statestr != null) && (!statestr.isEmpty())) {
            StateDefinition<Block, BlockState> def = blk.get().getStateDefinition();
            for (String kv : statestr.split(",")) {
                int eq = kv.indexOf('=');
                if (eq <= 0) continue;
                Property<?> p = def.getProperty(kv.substring(0, eq).trim());
                if (p != null) {
                    bs = withValue(bs, p, kv.substring(eq + 1).trim());
                }
            }
        }
        return bs;
    }

    private static <T extends Comparable<T>> BlockState withValue(BlockState bs, Property<T> p, String value) {
        Optional<T> v = p.getValue(value);
        return v.isPresent() ? bs.setValue(p, v.get()) : bs;
    }

    public void setChunks(BukkitWorld dw, List<DynmapChunk> chunks) {
        this.w = dw.getWorld();
        super.setChunks(dw, chunks);
    }

    @Override
    public int getFoliageColor(BiomeMap bm, int[] colormap, int x, int z) {
		return bm.<Biome>getBiomeObject().map(Biome::getSpecialEffects)
                .flatMap(BiomeSpecialEffects::foliageColorOverride).orElse(colormap[bm.biomeLookup()]);
    }

    @Override
    public int getGrassColor(BiomeMap bm, int[] colormap, int x, int z) {
        BiomeSpecialEffects fog = bm.<Biome>getBiomeObject().map(Biome::getSpecialEffects).orElse(null);
        if (fog == null) return colormap[bm.biomeLookup()];
        return fog.grassColorModifier().modifyColor(x, z, fog.grassColorOverride().orElse(colormap[bm.biomeLookup()]));
    }
}
