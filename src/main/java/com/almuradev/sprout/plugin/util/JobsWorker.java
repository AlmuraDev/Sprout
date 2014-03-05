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
package com.almuradev.sprout.plugin.util;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.almuradev.sprout.plugin.SproutConfiguration;

public class JobsWorker {
	public static void jobsBreak(Player sPlayer, Block block) {
		if (SproutConfiguration.jobsEnabled) {
			me.zford.jobs.Player player = me.zford.jobs.bukkit.BukkitUtil.wrapPlayer(sPlayer);
			if (!sPlayer.getGameMode().equals(GameMode.CREATIVE)) {
				if (me.zford.jobs.Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld())) {
					double multiplier = me.zford.jobs.config.ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
					me.zford.jobs.container.JobsPlayer jPlayer = me.zford.jobs.Jobs.getPlayerManager().getJobsPlayer(player.getName());
					me.zford.jobs.Jobs.action(jPlayer, new me.zford.jobs.bukkit.actions.BlockActionInfo(block,  me.zford.jobs.container.ActionType.BREAK), multiplier);
				}
			}
		}
	}

	public static void jobsPlace(Player sPlayer, Block block) {
		me.zford.jobs.Player player = me.zford.jobs.bukkit.BukkitUtil.wrapPlayer(sPlayer);
		if (!sPlayer.getGameMode().equals(GameMode.CREATIVE)) {
			if (me.zford.jobs.Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld())) {
				// restricted area multiplier
				double multiplier = me.zford.jobs.config.ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
				me.zford.jobs.container.JobsPlayer jPlayer = me.zford.jobs.Jobs.getPlayerManager().getJobsPlayer(player.getName());
				me.zford.jobs.Jobs.action(jPlayer, new me.zford.jobs.bukkit.actions.BlockActionInfo(block, me.zford.jobs.container.ActionType.PLACE), multiplier);
			}
		}
	}
}
