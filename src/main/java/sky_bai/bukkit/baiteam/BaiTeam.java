package sky_bai.bukkit.baiteam;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;

import sky_bai.bukkit.baiteam.config.BTConfig;
import sky_bai.bukkit.baiteam.config.DefaultConfig;
import sky_bai.bukkit.baiteam.config.MessageConfig;
import sky_bai.bukkit.baiteam.team.TeamManager;
import sky_bai.bukkit.baiteam.team.TeamTeleport;

public final class BaiTeam extends JavaPlugin {

	private static BaiTeam baiTeam;
	private static TeamManager teamManager;
	public static Object obj;

	private ProtocolManager protocolManager;
	
	@Override
	public void onEnable() {
		baiTeam = this;
		protocolManager = ProtocolLibrary.getProtocolManager();
		BTConfig.setConfig(new DefaultConfig());
		BTConfig.setMessage(new MessageConfig());

		teamManager = new TeamManager();
		
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			getLogger().info("检测到 PAPI ,注册PAPI变量中...");
			new BaiTeamPlaceholderAPI().register();
			getLogger().info("注册完成!!");
		}

		BaiCommand command = new BaiCommand();
		getCommand("BaiTeam").setExecutor(command);
		getCommand("BaiTeam").setTabCompleter(command);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Long time : TeamTeleport.UuidTime.keySet()) {
					if (System.currentTimeMillis() - time >= BTConfig.getConfig().getConfig().getLong("ExpiredTime",20000)) {
						String uuid = TeamTeleport.UuidTime.get(time);
						TeamTeleport.LocationMap.remove(uuid);
						TeamTeleport.TeleportPlayer.remove(uuid);
						TeamTeleport.UuidTime.remove(time);
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 10, 10);
	}

	public Object toObject(byte[] bytes) {
		Object obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = ois.readObject();
			ois.close();
			bis.close();
		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return obj;
	}

	@Override
	public void onDisable() {

	}

	public static BaiTeam getBaiTeam() {
		return baiTeam;
	}

	public static TeamManager getTeamManager() {
		return teamManager;
	}
	
	private void openBookPacketContainer(Player player) {
		PacketContainer pc = protocolManager.createPacket(PacketType.Play.Client.BLOCK_PLACE);
		pc.getHands().write(0, EnumWrappers.Hand.MAIN_HAND);
		pc.getLongs().write(0, System.currentTimeMillis());
		try {
			protocolManager.recieveClientPacket(player, pc);
		} catch (IllegalAccessException | InvocationTargetException e) {
		}
	}
	
	public void openBook(ItemStack book, Player player) {
        if (!book.getType().equals(Material.WRITTEN_BOOK)) {
            return;
        }
        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, book);
        openBookPacketContainer(player);
        player.getInventory().setItem(slot, old);
	}
}
