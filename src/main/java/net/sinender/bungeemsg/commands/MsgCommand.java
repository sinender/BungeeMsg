package net.sinender.bungeemsg.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.sinender.bungeemsg.BungeeMsg;

import java.util.Arrays;

public class MsgCommand extends Command {
    public MsgCommand() {
        super("msg");
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        String target = args[0];
        String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.getName().equalsIgnoreCase(target)) {
                player.sendMessage("§dFrom §e" + sender.getName() + "§7: §f" + msg);
                sender.sendMessage("§dTo §e" + target + "§7: §f" + msg);
                return;
            }
        }
        BungeeMsg.pubsub.publish("msgVerify", sender.getName(), target, msg);
    }
}

