package sky_bai.bukkit.baiteam.util;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.MinecraftKey;
import com.comphenix.protocol.wrappers.EnumWrappers.Hand;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import sky_bai.bukkit.baiteam.BaiTeam;

public class BTProtocol {
	private static ProtocolManager protocolManager = BaiTeam.getProtocolManager();
	
	private static void openBookPacketContainer_V1_9(Player player) {
		PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.CUSTOM_PAYLOAD);
		pc.getStrings().write(0, "MC|BOpen");
		Object pds = MinecraftReflection.getPacketDataSerializer(Unpooled.buffer().writeByte(0));
		pc.getModifier().withType(ByteBuf.class).write(0, pds);

		try {
			protocolManager.sendServerPacket(player, pc);
		} catch (InvocationTargetException e) {
		}
	}

	private static void openBookPacketContainer_V1_13(Player player) {
		PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.CUSTOM_PAYLOAD);
		pc.getMinecraftKeys().write(0, new MinecraftKey("book_open"));
		Object pds = MinecraftReflection.getPacketDataSerializer(Unpooled.buffer().writeByte(0));
		pc.getModifier().withType(ByteBuf.class).write(0, pds);

		try {
			protocolManager.sendServerPacket(player, pc);
		} catch (InvocationTargetException e) {
		}
	}

	private static void openBookPacketContainer_V1_14(Player player) {
		PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.OPEN_BOOK);
		pc.getHands().write(0, Hand.MAIN_HAND);
		try {
			protocolManager.sendServerPacket(player, pc);
		} catch (InvocationTargetException e) {
		}
	}

	private static void openBookMinecraftVersion(Player player) {
		MinecraftVersion version = protocolManager.getMinecraftVersion();
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
	
	public static void openBook(ItemStack book, Player player) {
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
