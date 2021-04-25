package me.jonaqhan.npic.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.jonaqhan.npic.Main;
import me.jonaqhan.npic.npc.Creation;
import me.jonaqhan.npic.utils.Chat;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PlayerConnection;

public class NPC implements CommandExecutor, TabCompleter {

	public Main plugin;
	List<String> tab = new ArrayList<String>();

	public NPC(Main plugin) {
		this.plugin = plugin;

		plugin.getCommand("npc").setExecutor(this);

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {

		if (args.length == 1) {
			tab.clear();
			tab.add("add");
			tab.add("remove");
			return tab;
		}

		if (args.length == 2) {
			tab.clear();
			tab.add("<name>");
			return tab;
		}

		if (args[0].equalsIgnoreCase("add")) {

			if (args.length == 3) {
				tab.clear();
				tab.add("<skin>");
				return tab;
			}

		}

		return null;

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {

		if (!(sender instanceof Player))
			return true;

		Player p = (Player) sender;

		if (args.length == 0) {
			p.sendMessage(Chat.tools("&c/npc add/remove"));
			return true;
		}

		if (args[0].equalsIgnoreCase("add")) {

			if (args.length == 1) {
				p.sendMessage(Chat.tools("&c/npc add <name> <skin>"));
				return true;
			}

			if (args.length == 2) {
				p.sendMessage(Chat.tools("&c/npc add <name> <skin>"));
				return true;
			}

			if (args.length > 3) {
				p.sendMessage(Chat.tools("&c/npc add <name> <skin>"));
				return true;
			}

			if (args[2] != null) {

				String name = Chat.tools(args[1]);
				String skin = args[2];

				Creation.Create(p, name, skin);
			}
			return false;
		}

		if (args[0].equalsIgnoreCase("remove")) {

			if (args.length == 1) {
				p.sendMessage(Chat.tools("&c/npc remove <name>"));
				return true;

			}

			if (args.length > 2) {
				p.sendMessage(Chat.tools("&c/npc remove <name>"));
				return true;

			}

			String name = Chat.tools(args[1]);

			File directory = new File(plugin.getDataFolder() + File.separator + "NPC");
			File[] files = directory.listFiles();
			Boolean succes = false;

			for (File file : files) {

				if (file.getName().equalsIgnoreCase(name + ".yml")) {
					file.delete();

					for (EntityPlayer npc : Creation.NPCL) {

						if (npc.getName().equalsIgnoreCase(name)) {
							p.sendMessage(Chat.tools("&6The NPC " + name + "&6 is removed"));
							succes = true;

							for (Player player : Bukkit.getOnlinePlayers()) {
								PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

								connection.sendPacket(new PacketPlayOutPlayerInfo(
										PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
								connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
							}
						}

					}

				}

			}

			if (succes == false)
				p.sendMessage(Chat.tools("&cNo NPC found with the name&b " + name));

		}
		return false;
	}

}
