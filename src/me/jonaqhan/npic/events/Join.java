package me.jonaqhan.npic.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.jonaqhan.npic.npc.Creation;

public class Join implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (Creation.getNPCs() == null)
			return;

		if (Creation.getNPCs().isEmpty())
			return;

		Creation.addPacketOnJoin(e.getPlayer());

	}

}
