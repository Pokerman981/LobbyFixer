/*
 * Copyright (c) 2020. Troy Gidney
 * All rights reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * File Last Modified: 9/7/20, 2:27 PM
 * File: Main.java
 * Project: LobbyFixer
 */

package me.pokerman981;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class Main extends JavaPlugin implements CommandExecutor {

    private LuckPerms luckPerms;

    @Override
    public void onEnable() {

        if (!Bukkit.getPluginManager().isPluginEnabled("LuckPerms"))
            throw new RuntimeException("Unable to find LuckPerms plugin");

        this.luckPerms = LuckPermsProvider.get();

        this.getCommand("fixrank").setExecutor((commandSender, command, s, strings) -> {
            Player player = (Player) commandSender;
            User user = this.luckPerms.getUserManager().getUser(player.getUniqueId());

            Object[] objects = {0, "null"};

            List<Node> list = user.
                    getNodes()
                    .stream().filter(node -> node.getKey().contains("group.")).collect(Collectors.toList());

            list.forEach(node -> {
                Group group = this.luckPerms.getGroupManager().getGroup(node.getKey().replace("group.", ""));
                if (group == null) return;
                if (node.getContexts().isEmpty()) return;
                if (group.getWeight().orElse(0) > (int) objects[0]) {
                    objects[0] = group.getWeight().getAsInt();
                    objects[1] = group.getName();
                }
            });

            if (objects[1] != "null") {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                        "lp user %player% parent set %group% server=lobby"
                                .replace("%player%", player.getUniqueId().toString())
                                .replace("%group%", objects[1].toString()));

                System.out.println("[FixRank] Set %player%'s to group %group% in context server=lobby"
                        .replace("%player%", player.getName() + "(" + player.getUniqueId().toString() + ")")
                        .replace("%group%", objects[1].toString()));
            } else {
                System.out.println("Could not find %player%'s highest context rank!"
                        .replace("%player%", player.getName() + "(" + player.getUniqueId().toString() + ")"));
            }
            return true;
        });

    }


}
