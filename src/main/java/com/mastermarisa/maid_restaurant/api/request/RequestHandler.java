package com.mastermarisa.maid_restaurant.api.request;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public abstract class RequestHandler <T extends IRequest> implements INBTSerializable<CompoundTag> {
    protected LinkedList<T> requests = new LinkedList<>();
    public boolean accept = true;

    public LinkedList<T> getRequests() { return requests; }

    public int size() { return requests.size(); }

    public void add(T request) {
        requests.add(request);
    }

    public @Nullable T getFirst() {
        if (requests.isEmpty()) return null;
        return requests.getFirst();
    }

    public @Nullable T getFirstAvailable(ServerLevel level, EntityMaid maid) {
        if (requests.isEmpty()) return null;
        for (var request : requests) {
            if (request.checkEnableConditions(level, maid)) {
                return request;
            }
        }
        return null;
    }

    public @Nullable T removeFirst() {
        if (requests.isEmpty()) return null;
        return requests.removeFirst();
    }

    public @Nullable T getAt(int index) {
        if (requests.size() <= index) return null;
        return requests.get(index);
    }

    public @Nullable T removeAt(int index) {
        if (requests.size() <= index) return null;
        return requests.remove(index);
    }

    public boolean remove(T value) {
        return requests.remove(value);
    }

    public void clear() { requests.clear(); }

    public List<T> toList() { return requests.stream().toList(); }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        tag.putInt("length",requests.size());
        for (T request : requests) listTag.add(request.serializeNBT(provider));
        tag.put("requests",listTag);
        tag.putBoolean("accept",accept);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        if (!tag.contains("length")) return;
        requests = new LinkedList<>();
        ListTag listTag = tag.getList("requests", Tag.TAG_COMPOUND);
        for (int i = 0;i < tag.getInt("length");i++) {
            requests.add(fromCompound(provider,listTag.getCompound(i)));
        }
        accept = tag.getBoolean("accept");
    }

    protected abstract T fromCompound(HolderLookup.Provider provider, CompoundTag tag);
}
