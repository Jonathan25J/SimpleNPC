package me.jonaqhan.npic;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.jonaqhan.npic.commands.NPC;
import me.jonaqhan.npic.events.Join;
import me.jonaqhan.npic.npc.Creation;
import me.jonaqhan.npic.utils.Chat;

public class Main extends JavaPlugin implements Listener {

	public void onEnable() {
		new NPC(this);
		new Creation(this);
		Bukkit.getPluginManager().registerEvents(new Join(), this);

		File directory = new File(getDataFolder() + File.separator + "NPC");

		File[] files = directory.listFiles();

		if (files != null) {

			for (File file : files) {
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				String name = Chat.tools(file.getName().replace(".yml", ""));
				String skin = config.getString("data.skin");

				Creation.Load(file, name, skin);
			}
		}

	}

	public void onDisable() {

		Creation.unLoad();

	}
}
