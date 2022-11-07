package net.sinender.bungeemsg;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.sinender.bungeemsg.commands.MsgCommand;
import net.sinender.bungeemsg.pubsub.PubSub;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class BungeeMsg extends Plugin {
    public static BungeeMsg instance;
    public static Configuration config;
    public static PubSub pubsub;
    public static BungeeMsg getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            new File(getDataFolder(), "config.yml");
            pubsub = new PubSub(this);
            pubsub.registerListener("msgVerify", args -> {
                String sender = args[0];
                String target = args[1];
                String msg = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (player.getName().equalsIgnoreCase(target)) {
                        player.sendMessage("§dFrom §e" + sender + "§7: §f" + msg);
                        pubsub.publish("msgSent", sender, target, msg);
                        return;
                    }
                }
            });
            pubsub.registerListener("msgSent", args -> {
                String sender = args[0];
                String target = args[1];
                String msg = args[2];
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (player.getName().equalsIgnoreCase(sender)) {
                        player.sendMessage("§dTo §e" + target + "§7: §f" + msg);
                        return;
                    }
                }
            });
            ProxyServer.getInstance().getPluginManager().registerCommand(this, new MsgCommand());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
