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

import java.util.Arrays;
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
                if (group.getWeight().orElse(0) > (int) objects[0]) {
                    objects[0] = group.getWeight().getAsInt();
                    objects[1] = group.getName();
                }
            });

            System.out.println("USER CURRENT PERMISSIONS");
            System.out.println(list);

            System.out.println("TOP FOUND RANK");
            System.out.println(Arrays.toString(objects));

            return true;
        });

    }



}
