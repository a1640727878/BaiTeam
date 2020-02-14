package sky_bai.bukkit.baiteam;

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
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.MinecraftKey;
import com.comphenix.protocol.wrappers.EnumWrappers.Hand;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import sky_bai.bukkit.baiteam.command.BTCommandCMD;
import sky_bai.bukkit.baiteam.command.BTCommandPlayer;
import sky_bai.bukkit.baiteam.config.BTConfig;
import sky_bai.bukkit.baiteam.config.BTDefaultConfig;
import sky_bai.bukkit.baiteam.config.BTMessageConfig;
import sky_bai.bukkit.baiteam.config.gui.BTGAction;
import sky_bai.bukkit.baiteam.config.gui.BTGGui;
import sky_bai.bukkit.baiteam.config.gui.BTGModule;
import sky_bai.bukkit.baiteam.listener.BaiTeamListener;
import sky_bai.bukkit.baiteam.message.BTMessage;
import sky_bai.bukkit.baiteam.team.TeamManager;
import sky_bai.bukkit.baiteam.team.TeamTeleportExpired;
import sky_bai.bukkit.baiteam.util.BTPlaceholderAPI;

public final class BaiTeam extends JavaPlugin {

	private static BaiTeam baiTeam;

	private static TeamManager teamManager;

	private static ProtocolManager pm;

	@Override
	public void onEnable() {
		baiTeam = this;
		pm = ProtocolLibrary.getProtocolManager();

		BTConfig.setConfig(new BTDefaultConfig());
		BTConfig.setMessage(new BTMessageConfig());
		BTConfig.setGui(new BTGGui());
		BTConfig.setAction(new BTGAction());
		BTConfig.setModule(new BTGModule());

		teamManager = new TeamManager();

		getCommand("BaiTeam").setExecutor(new BTCommandPlayer());
		getCommand("BaiTeamCmd").setExecutor(new BTCommandCMD());

		BTMessage.setMesPrefix(BTConfig.getConfig().getConfig().getString("MesPrefix", "[BaiTeam] "));

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			getLogger().info("检测到 PAPI ,注册PAPI变量中...");
			new BTPlaceholderAPI().register();
			getLogger().info("注册完成!!");
		}

		Bukkit.getPluginManager().registerEvents(new BaiTeamListener(), this);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Long time : TeamTeleportExpired.UuidTime.keySet()) {
					if (System.currentTimeMillis() - time >= BTConfig.getConfig().getConfig().getLong("Time.Teleport.Expired", 20000)) {
						String uuid = TeamTeleportExpired.UuidTime.get(time);
						TeamTeleportExpired.LocationMap.remove(uuid);
						TeamTeleportExpired.TeleportPlayer.remove(uuid);
						TeamTeleportExpired.UuidTime.remove(time);
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 10, 10);
	}

	public static BaiTeam getInstance() {
		return baiTeam;
	}

	public static TeamManager getTeamManager() {
		return teamManager;
	}

	private void openBookPacketContainer_V1_9(Player player) {
		PacketContainer pc = pm.createPacket(PacketType.Play.Server.CUSTOM_PAYLOAD);
		pc.getStrings().write(0, "MC|BOpen");
		Object pds = MinecraftReflection.getPacketDataSerializer(Unpooled.buffer().writeByte(0));
		pc.getModifier().withType(ByteBuf.class).write(0, pds);

		try {
			pm.sendServerPacket(player, pc);
		} catch (InvocationTargetException e) {
		}
	}

	private void openBookPacketContainer_V1_13(Player player) {
		PacketContainer pc = pm.createPacket(PacketType.Play.Server.CUSTOM_PAYLOAD);
		pc.getMinecraftKeys().write(0, new MinecraftKey("book_open"));
		Object pds = MinecraftReflection.getPacketDataSerializer(Unpooled.buffer().writeByte(0));
		pc.getModifier().withType(ByteBuf.class).write(0, pds);

		try {
			pm.sendServerPacket(player, pc);
		} catch (InvocationTargetException e) {
		}
	}

	private void openBookPacketContainer_V1_14(Player player) {
		PacketContainer pc = pm.createPacket(PacketType.Play.Server.OPEN_BOOK);
		pc.getHands().write(0, Hand.MAIN_HAND);
		try {
			pm.sendServerPacket(player, pc);
		} catch (InvocationTargetException e) {
		}
	}

	private void openBookMinecraftVersion(Player player) {
		MinecraftVersion version = pm.getMinecraftVersion();
		if (version.compareTo(new MinecraftVersion("1.9")) >= 0 && version.compareTo(new MinecraftVersion("1.12.2")) == 0) {
			openBookPacketContainer_V1_9(player);
			return;
		} else if (version.compareTo(new MinecraftVersion("1.13")) >= 0 && version.compareTo(new MinecraftVersion("1.13.2")) == 0) {
			openBookPacketContainer_V1_13(player);
			return;
		} else if (version.compareTo(new MinecraftVersion("1.14")) >= 0) {
			openBookPacketContainer_V1_14(player);
		}
	}

	public void openBook(ItemStack book, Player player) {
		if (!book.getType().equals(Material.WRITTEN_BOOK)) {
			return;
		}
		int slot = player.getInventory().getHeldItemSlot();
		ItemStack old = player.getInventory().getItem(slot);
		player.getInventory().setItem(slot, book);
		openBookMinecraftVersion(player);
		player.getInventory().setItem(slot, old);
	}
}
