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
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.Hand;
import com.comphenix.protocol.wrappers.MinecraftKey;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import net.minecraft.server.v1_12_R1.EntityArrow.PickupStatus;
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
					if (System.currentTimeMillis() - time >= BTConfig.getConfig().getConfig().getLong("ExpiredTime", 20000)) {
						String uuid = TeamTeleport.UuidTime.get(time);
						TeamTeleport.LocationMap.remove(uuid);
						TeamTeleport.TeleportPlayer.remove(uuid);
						TeamTeleport.UuidTime.remove(time);
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 10, 10);

		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.CUSTOM_PAYLOAD) {
			@Override
			public void onPacketSending(PacketEvent event) {
				System.out.println(event);
				System.out.println(event.getPacket());
				System.out.println(event.getPacket().getSpecificModifier(Object.class));
				System.out.println(event.getPacket().getSpecificModifier(Object.class).getValues());
				System.out.println(event.getPacket().getStrings());
				System.out.println(event.getPacket().getStrings().getValues());
				System.out.println(event.getPacket().getModifier().withType(ByteBuf.class));
				System.out.println(event.getPacket().getModifier().withType(ByteBuf.class).getValues());
				System.out.println(ByteBufUtil.hexDump((ByteBuf) event.getPacket().getModifier().withType(ByteBuf.class).getValues().get(0)));
			}
		});
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

	private void openBookPacketContainer_V1_9(Player player) {
		PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.CUSTOM_PAYLOAD);
		pc.getStrings().write(0, "MC|BOpen");
		Object pds = MinecraftReflection.getPacketDataSerializer(Unpooled.buffer().writeByte(0));
		pc.getModifier().withType(ByteBuf.class).write(0, pds);

		try {
			protocolManager.sendServerPacket(player, pc);
		} catch (InvocationTargetException e) {
		}
	}

	private void openBookPacketContainer_V1_13(Player player) {
		PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.CUSTOM_PAYLOAD);
		pc.getMinecraftKeys().write(0, new MinecraftKey("book_open"));
		Object pds = MinecraftReflection.getPacketDataSerializer(Unpooled.buffer().writeByte(0));
		pc.getModifier().withType(ByteBuf.class).write(0, pds);

		try {
			protocolManager.sendServerPacket(player, pc);
		} catch (InvocationTargetException e) {
		}
	}

	private void openBookPacketContainer_V1_14(Player player) {
		PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.OPEN_BOOK);
		pc.getHands().write(0, Hand.MAIN_HAND);
		try {
			protocolManager.sendServerPacket(player, pc);
		} catch (InvocationTargetException e) {
		}
	}

	private void openBookMinecraftVersion(Player player) {
		MinecraftVersion version = protocolManager.getMinecraftVersion();
		if (version.compareTo(new MinecraftVersion("1.9")) >= 0 && version.compareTo(new MinecraftVersion("1.12.2")) == 0) {
			openBookPacketContainer_V1_9(player);
			return;
		} else if (version.compareTo(new MinecraftVersion("1.13")) >= 0 && version.compareTo(new MinecraftVersion("1.13.2")) == 0 ) {
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
