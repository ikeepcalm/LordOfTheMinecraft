package dev.ua.ikeepcalm.entities.custom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

public record CustomLocation(World world, Material type, BlockData data, int x, int y, int z) {

    public Location toLocation() {
        return new Location(world, x, y, z);
    }

    public CustomLocation(Location location) {
        this(location.getWorld(), location.getBlock().getType(), location.getBlock().getBlockData(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomLocation that = (CustomLocation) o;
        return x == that.x && y == that.y && z == that.z && world.equals(that.world);
    }

    @Override
    public String toString() {
        return "CustomLocation{" +
               "x=" + x +
               ", y=" + y +
               ", z=" + z +
               '}';
    }

    @Override
    public int hashCode() {
        return (world.hashCode() * 31 + x) * 31 + y;
    }
}
