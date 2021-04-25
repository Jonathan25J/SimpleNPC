package me.jonaqhan.npic.npc;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.jonaqhan.npic.Main;
import me.jonaqhan.npic.utils.Chat;
import net.minecraft.server.v1_16_R3.DataWatcherRegistry;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.WorldServer;

public class Creation {

	public static Main plugin;

	@SuppressWarnings("static-access")
	public Creation(Main plugin) {
		this.plugin = plugin;

	}

	public static List<EntityPlayer> NPCL = new ArrayList<EntityPlayer>();

	public static void Create(Player p, String name, String skin) {
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer world = ((CraftWorld) Bukkit.getWorld(p.getWorld().getName())).getHandle();
		GameProfile profile = new GameProfile(UUID.randomUUID(), name);
		EntityPlayer npc = new EntityPlayer(server, world, profile, new PlayerInteractManager(world));
		npc.setLocation(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(),
				p.getLocation().getYaw(), p.getLocation().getPitch());

		String[] info = getSkin(p, skin);
		profile.getProperties().put("textures", new Property("textures", info[0], info[1]));

		Data store = new Data(name, p.getName(), skin, p.getLocation().getX(), p.getLocation().getY(),
				p.getLocation().getZ(), p.getLocation().getPitch(), p.getLocation().getYaw(),
				p.getLocation().getWorld().getName(), info[0], info[1]);

		File file = new File(plugin.getDataFolder() + File.separator + "NPC" + File.separator,
				name.toString() + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		if (file.exists()) {

			p.sendMessage(Chat.tools("&cThere is already an NPC with that name!"));
			return;
		}

		if (!file.exists()) {
			try {
				config.createSection("data");
				config.set("data" + ".name", store.getNpcName());
				config.set("data" + ".creator", store.getCreator());
				config.set("data" + ".skin", store.getSkin());
				config.set("data" + ".x", store.getX());
				config.set("data" + ".y", store.getY());
				config.set("data" + ".z", store.getZ());
				config.set("data" + ".pitch", store.getPitch());
				config.set("data" + ".yaw", store.getYaw());
				config.set("data" + ".world", store.getWorld());
				config.set("data" + ".texture", store.getTexture());
				config.set("data" + ".signature", store.getSignature());

				config.save(file);
				NPCL.add(npc);
				addPacket(npc);

				p.sendMessage(Chat.tools("&aNPC " + name + Chat.tools("&a created!")));

			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	private static String[] getSkin(Player p, String name) {
		try {
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			InputStreamReader reader = new InputStreamReader(url.openStream());
			String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

			URL url2 = new URL(
					"https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
			InputStreamReader reader2 = new InputStreamReader(url2.openStream());
			JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray()
					.get(0).getAsJsonObject();

			String texture = property.get("value").getAsString();
			String signature = property.get("signature").getAsString();

			return new String[] { texture, signature };

		} catch (Exception e) {
			EntityPlayer player = ((CraftPlayer) p).getHandle();
			GameProfile profile = player.getProfile();
			Property property = profile.getProperties().get("textures").iterator().next();
			String texture = property.getValue();
			String signature = property.getSignature();
			return new String[] { texture, signature };
		}

	}

	public static void addPacket(EntityPlayer npc) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
			npc.getDataWatcher().set(DataWatcherRegistry.a.a(16), (byte) 127);
			connection.sendPacket(
					new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
			connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
			connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
			connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));

		}

	}

	public static void addPacketOnJoin(Player p) {
		for (EntityPlayer npc : NPCL) {

			PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
			npc.getDataWatcher().set(DataWatcherRegistry.a.a(16), (byte) 127);
			connection.sendPacket(
					new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
			connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
			connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
			connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
		}

	}

	public static void Load(File f, String name, String skin) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);

		Data load = new Data(name, skin, config.getString("data.creator"), config.getDouble("data.x"),
				config.getDouble("data.y"), config.getDouble("data.z"), (float) config.getDouble("data.pitch"),
				(float) config.getDouble("data.yaw"), config.getString("data.world"), config.getString("data.texture"),
				config.getString("data.signature"));
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer world = ((CraftWorld) Bukkit.getWorld(load.getWorld())).getHandle();
		GameProfile profile = new GameProfile(UUID.randomUUID(), name);
		EntityPlayer npc = new EntityPlayer(server, world, profile, new PlayerInteractManager(world));
		npc.setLocation(load.getX(), load.getY(), load.getZ(), load.getYaw(), load.getPitch());

		String[] info = getSkin(null, skin);
		profile.getProperties().put("textures", new Property("textures", info[0], info[1]));

		NPCL.add(npc);
		addPacket(npc);
	}

	public static void unLoad() {
		if (!Bukkit.getOnlinePlayers().isEmpty()) {

			for (Player p : Bukkit.getOnlinePlayers()) {

				for (EntityPlayer npc : Creation.NPCL) {
					PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
					connection.sendPacket(new PacketPlayOutPlayerInfo(
							PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
					connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));

				}

			}

		}

	}

	public static List<EntityPlayer> getNPCs() {
		return NPCL;

	}
}