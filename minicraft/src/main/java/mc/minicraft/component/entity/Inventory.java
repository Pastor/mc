package mc.minicraft.component.entity;

import mc.minicraft.component.item.Item;
import mc.minicraft.component.item.ResourceItem;
import mc.minicraft.component.item.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public final class Inventory {
    public List<Item> items = new ArrayList<>();

    public void add(Item item) {
        add(items.size(), item);
    }

    public void add(int slot, Item item) {
        if (item instanceof ResourceItem) {
            ResourceItem toTake = (ResourceItem) item;
            ResourceItem has = findResource(toTake.resource);
            if (has == null) {
                items.add(slot, toTake);
            } else {
                has.count += toTake.count;
            }
        } else {
            items.add(slot, item);
        }
    }

    private ResourceItem findResource(Resource resource) {
        for (Item item : items) {
            if (item instanceof ResourceItem) {
                ResourceItem has = (ResourceItem) item;
                if (has.resource == resource)
                    return has;
            }
        }
        return null;
    }

    public boolean hasResources(Resource r, int count) {
        ResourceItem ri = findResource(r);
        return ri != null && ri.count >= count;
    }

    public boolean removeResource(Resource r, int count) {
        ResourceItem ri = findResource(r);
        if (ri == null)
            return false;
        if (ri.count < count)
            return false;
        ri.count -= count;
        if (ri.count <= 0)
            items.remove(ri);
        return true;
    }

    public int count(Item item) {
        if (item instanceof ResourceItem) {
            ResourceItem ri = findResource(((ResourceItem) item).resource);
            if (ri != null) return ri.count;
        } else {
            int count = 0;
            for (Item item1 : items) {
                if (item1.matches(item))
                    count++;
            }
            return count;
        }
        return 0;
    }
}
