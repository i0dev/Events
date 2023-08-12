package com.i0dev.events.util;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

//Simple, bare-bones cuboid class. Original idea from bergerkiller on the bukkit forumns (http://forums.bukkit.org/threads/region-general-api-for-creating-cuboids.34644/).
public class Cuboid {

    public int xMin, xMax, yMin, yMax, zMin, zMax;
    public World world;

    public Cuboid(Location point1, Location point2) {
        xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        yMin = Math.min(point1.getBlockY(), point2.getBlockY());
        yMax = Math.max(point1.getBlockY(), point2.getBlockY());
        zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
        world = point1.getWorld();
    }

    public Cuboid(BlockVector3 point1, BlockVector3 point2, World world) {
        xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        yMin = Math.min(point1.getBlockY(), point2.getBlockY());
        yMax = Math.max(point1.getBlockY(), point2.getBlockY());
        zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
        this.world = world;
    }

    public static Cuboid deserialize(JavaPlugin p, String s) {
        if (s == null) {
            return null;
        }
        String s1 = s.split("@")[0];
        World world;

        try {
            world = p.getServer().getWorld(s.split("@")[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            world = p.getServer().getWorlds().get(0);
        }
        String s2 = s1.split(":")[0];

        int xMin, xMax, yMin, yMax, zMin, zMax;
        String[] sR = s2.split(",");

        xMin = Integer.parseInt(sR[0]);
        yMin = Integer.parseInt(sR[2]);
        zMin = Integer.parseInt(sR[1]);

        if (s1.contains(":")) {
            String s3 = s1.split(":")[1];

            String[] sR1 = s3.split(",");

            xMax = Integer.parseInt(sR1[0]);
            yMax = Integer.parseInt(sR1[2]);
            zMax = Integer.parseInt(sR1[1]);
        } else {
            xMax = xMin;
            yMax = yMin;
            zMax = zMin;
        }
        return new Cuboid(xMin, xMax, yMin, yMax, zMin, zMax, world);
    }

    public boolean contains(Location loc) {
        if (loc.getWorld() != this.world) return false;
        if (loc.getBlockX() < xMin) return false;
        if (loc.getBlockX() > xMax) return false;
        if (loc.getBlockY() < yMin) return false;
        if (loc.getBlockY() > yMax) return false;
        if (loc.getBlockZ() < zMin) return false;
        if (loc.getBlockZ() > zMax) return false;
        return true;
    }

    public List<Block> getAllBlocks(boolean excludeAir) {
        List<Block> blocks = new ArrayList<>();
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (!excludeAir || !block.getType().isAir()) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    public Cuboid(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, World world) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.zMin = zMin;
        this.zMax = zMax;
        this.world = world;
    }

    public boolean isInRegion(Location loc) {
        if (loc.getWorld() != this.world) return false;
        if (loc.getBlockX() < xMin) return false;
        if (loc.getBlockX() > xMax) return false;
        if (loc.getBlockY() < yMin) return false;
        if (loc.getBlockY() > yMax) return false;
        if (loc.getBlockZ() < zMin) return false;
        return loc.getBlockZ() <= zMax;
    }

    public String serialize() {
        return "" + xMin + "," + zMin + "," + yMin + ":" + xMax + "," + zMax + "," + yMax + "@" + world.getName();
    }

    @Override
    public String toString() {
        return serialize();
    }

    public int getXWidth() {
        return xMax - xMin;
    }

    public int getZWidth() {
        return zMax - zMin;
    }

    public int getHeight() {
        return yMax - yMin;
    }

    public int getArea() {
        return getHeight() * getXWidth() * getZWidth();
    }

    // Gets all the block locations that are within the cuboid
    public List<Location> getAllCuboidLocations() {
        List<Location> ret = new ArrayList<>();
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    ret.add(new Location(world, x, y, z));
                }
            }
        }
        return ret;
    }
}