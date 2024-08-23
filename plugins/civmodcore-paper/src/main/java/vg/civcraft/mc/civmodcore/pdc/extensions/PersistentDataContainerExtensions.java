package vg.civcraft.mc.civmodcore.pdc.extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import vg.civcraft.mc.civmodcore.nbt.NBTType;

/**
 * Set of extension methods for {@link PersistentDataContainer}.
 */
public final class PersistentDataContainerExtensions {

    /**
     * @param self The PersistentDataContainer to get the internal NBT of.
     * @return Returns the PDC's inner-map.
     */
    @NotNull
    public static Map<String, Tag> getRaw(@NotNull final PersistentDataContainer self) {
        return ((CraftPersistentDataContainer) self).getRaw();
    }

    /**
     * @param self The PersistentDataContainer to get the size of.
     * @return Returns the PDC's size.
     */
    public static int size(@NotNull final PersistentDataContainer self) {
        return getRaw(self).size();
    }

    /**
     * @param self The PersistentDataContainer to check whether the list is on.
     * @param key  The key of the list.
     * @return Returns true if a list is present at that key.
     */
    public static boolean hasList(@NotNull final PersistentDataContainer self,
                                  @NotNull final NamespacedKey key) {
        final var found = getRaw(self).get(key.toString());
        return found != null && found.getId() == NBTType.LIST;
    }

    /**
     * @param self The PersistentDataContainer to get the list from.
     * @param key  The key of the list.
     * @param type The type of the list elements.
     * @param <P>  The primitive type the list elements type.
     * @param <C>  The complex type of the list elements type.
     */
    public static <P, C> List<C> getList(@NotNull final PersistentDataContainer self,
                                         @NotNull final NamespacedKey key,
                                         @NotNull final PersistentDataType<P, C> type) {
        final var pdc = (CraftPersistentDataContainer) self;
        final var found = pdc.getRaw().get(key.toString());
        if (!(found instanceof ListTag tagList)) {
            return null;
        }
        final var typeRegistry = pdc.getDataTagTypeRegistry();
        final var result = new ArrayList<C>(tagList.size());
        for (final Tag nbtElement : tagList) {
            final P primitiveElement = typeRegistry.extract(type, nbtElement);
            final C complexElement = type.fromPrimitive(primitiveElement, pdc.getAdapterContext());
            result.add(Objects.requireNonNull(complexElement));
        }
        return result;
    }

    /**
     * @param self The PersistentDataContainer to set the list to.
     * @param key  The key of the list.
     * @param type The type of the list elements.
     * @param <P>  The primitive type the list elements type.
     * @param <C>  The complex type of the list elements type.
     * @param list The list to set.
     */
    public static <P, C> void setList(@NotNull final PersistentDataContainer self,
                                      @NotNull final NamespacedKey key,
                                      @NotNull final PersistentDataType<P, C> type,
                                      @NotNull final List<C> list) {
        final var pdc = (CraftPersistentDataContainer) self;
        final var typeRegistry = pdc.getDataTagTypeRegistry();
        final var result = new ListTag();
        for (final C complexElement : list) {
            final P primitiveElement = type.toPrimitive(complexElement, pdc.getAdapterContext());
            final Tag nbtElement = typeRegistry.wrap(type, primitiveElement);
            result.add(Objects.requireNonNull(nbtElement));
        }
        pdc.getRaw().put(key.toString(), result);
    }

}
