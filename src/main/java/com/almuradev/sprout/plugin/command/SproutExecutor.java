/*
 * This file is part of Sprout.
 *
 * © 2013 AlmuraDev <http://www.almuradev.com/>
 * Sprout is licensed under the GNU General Public License.
 *
 * Sprout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sprout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License. If not,
 * see <http://www.gnu.org/licenses/> for the GNU General Public License.
 */
package com.almuradev.sprout.plugin.command;

import com.almuradev.sprout.api.crop.Sprout;
import com.almuradev.sprout.api.util.Int21TripleHashed;
import com.almuradev.sprout.api.util.TInt21TripleObjectHashMap;
import com.almuradev.sprout.plugin.SproutPlugin;
import com.almuradev.sprout.plugin.crop.SimpleSprout;
import com.almuradev.sprout.plugin.task.GrowthTask;
import com.almuradev.sprout.plugin.thread.SaveThread;
import com.almuradev.sprout.plugin.thread.ThreadRegistry;

import gnu.trove.procedure.TLongObjectProcedure;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.block.SpoutBlock;

public class SproutExecutor implements CommandExecutor {
    private final SproutPlugin plugin;

    public SproutExecutor(final SproutPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "clear":
                    if (!checkPermission(sender, "sprout.clear")) {
                        sender.sendMessage("[Sprout] You do not have permission!");
                        return true;
                    }
                    switch (args.length) {
                        case 1:
                            plugin.getServer().getScheduler().cancelTasks(plugin);
                            for (World world : Bukkit.getWorlds()) {
                                clear(sender, world);
                            }
                            return true;
                        case 2:
                            final World world = Bukkit.getWorld(args[1]);
                            if (world == null) {
                                sender.sendMessage("[Sprout] World [" + args[1] + "] is not a valid world.");
                                return true;
                            }
                            plugin.getServer().getScheduler().cancelTasks(plugin);
                            clear(sender, world);
                            return true;
                    }
                case "info":
                    if (!checkPermission(sender, "sprout.info")) {
                        sender.sendMessage("[Sprout] You do not have permission!");
                        return true;
                    }
                    switch (args.length) {
                        case 1:
                            for (World world : Bukkit.getWorlds()) {
                                info(sender, world);
                            }
                            return true;
                        case 2:
                            final World world = Bukkit.getWorld(args[1]);
                            if (world == null) {
                                sender.sendMessage("[Sprout] World [" + args[1] + "] is not a valid world.");
                                return true;
                            }
                            info(sender, world);
                            return true;
                    }
                case "task":
                    if (!checkPermission(sender, "sprout.task")) {
                        sender.sendMessage("[Sprout] You do not have permission!");
                        return true;
                    }
                    switch (args.length) {
                        case 1:
                            for (World world : Bukkit.getWorlds()) {
                            	final Integer id = GrowthTask.WORLD_ID_MAP.get(world.getName());
                            	if (id != null) {
                            		Bukkit.getServer().broadcastMessage("Task Info: World: " + world.getName() + " Task ID: " + id);
                            		Bukkit.getServer().broadcastMessage("Task is Running: " + Bukkit.getScheduler().isCurrentlyRunning(id));
                            		Bukkit.getServer().broadcastMessage("Task is Queued: " + Bukkit.getScheduler().isQueued(id));
                            	} else {
                            		Bukkit.getServer().broadcastMessage("No task for World: " + world.getName());
                            	}
                            }
                            return true;
                    }
            }
        }
        return false;
    }

    private boolean checkPermission(final CommandSender sender, final String permission) {
        return !(sender instanceof Player) || sender.hasPermission(permission);
    }

    private void clear(final CommandSender sender, final World world) {
        final TInt21TripleObjectHashMap<?> registry = plugin.getWorldRegistry().get(world.getName());
        if (registry == null) {
            sender.sendMessage("[Sprout] World [" + world.getName() + "] has no registry of sprouts.");
            return;
        }
        sender.sendMessage("[Sprout] Clearing all sprouts for world [" + world.getName() + "]. WARNING: THIS MAY TAKE SOME TIME...");
        ((SaveThread) ThreadRegistry.get(world.getName())).clear();
        registry.getInternalMap().forEachEntry(new TLongObjectProcedure<Object>() {
            @Override
            public boolean execute(long l, Object o) {
                final Sprout sprout = (Sprout) o;
                if (sprout != null) {
                    final int x = Int21TripleHashed.key1(l);
                    final int y = Int21TripleHashed.key2(l);
                    final int z = Int21TripleHashed.key3(l);

                    final Block block = Bukkit.getWorld(world.getName()).getBlockAt(x, y, z);
                    ((SpoutBlock) block).setCustomBlock(null);
                    block.setType(Material.AIR);
                    ((SaveThread) ThreadRegistry.get(world.getName())).remove(l, (SimpleSprout) sprout);
                }
                return true;
            }
        });
        registry.clear();
        GrowthTask.schedule(plugin, false, world);
    }

    private void info(final CommandSender sender, final World world) {
        final TInt21TripleObjectHashMap<?> registry = plugin.getWorldRegistry().get(world.getName());
        int count;
        if (registry == null) {
            count = 0;
        } else {
            count = registry.size();
        }
        sender.sendMessage("[Sprout] World [" + world.getName() + "] has [" + count + "] sprout(s).");
    }
}
