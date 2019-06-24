package sky_bai.bukkit.baiteam.bookgui.nms;

import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_13_R2.EnumHand;
import net.minecraft.server.v1_13_R2.MinecraftKey;
import net.minecraft.server.v1_13_R2.PacketDataSerializer;
import net.minecraft.server.v1_13_R2.PacketPlayOutCustomPayload;
import sky_bai.bukkit.baiteam.bookgui.BookGuiOpenBookNMS;

public class BookGuiOpenBook_v1_13_R2 implements BookGuiOpenBookNMS{

	@Override
	public void openBook(Player player, ItemStack bookStack) {
		final int playerHeldSlot = player.getInventory().getHeldItemSlot();
		final ItemStack oldItemStack = player.getInventory().getItem(playerHeldSlot);
		player.getInventory().setItem(playerHeldSlot, bookStack);
		final PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload(MinecraftKey.a("minecraft:book_open"), new PacketDataSerializer(Unpooled.buffer()).a(EnumHand.MAIN_HAND));
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		player.getInventory().setItem(playerHeldSlot, oldItemStack);
	}

}
