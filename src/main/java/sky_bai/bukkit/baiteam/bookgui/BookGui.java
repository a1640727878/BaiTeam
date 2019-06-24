package sky_bai.bukkit.baiteam.bookgui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;

import net.md_5.bungee.api.chat.BaseComponent;

public class BookGui {
	private static  BookGuiOpenBookNMS bookGuiOpenBookNMS;
	private ItemStack bookItemStack;
	private BookMeta bookMeta;

	private BookGui() {
		reset();
	}

	public void reset() {
		bookItemStack = new ItemStack(Material.WRITTEN_BOOK);
		bookMeta = (BookMeta) bookItemStack.getItemMeta();
		bookMeta.setAuthor("Admin");
		bookMeta.setTitle("BookGui");
		bookMeta.setGeneration(Generation.TATTERED);
	}
	
	public static void setBookGuiOpenBookNMS(BookGuiOpenBookNMS bookGuiOpenBookNMS) {
		BookGui.bookGuiOpenBookNMS = bookGuiOpenBookNMS;
	}
	
	public static BookGui getBookGui() {
		return new BookGui();
	}	
	
	public BookGui addPage(BaseComponent... strings) {
		bookMeta.spigot().addPage(strings);
		return this;
	}
	
	public void openBook(Player player) {
		bookItemStack.setItemMeta(bookMeta);
		bookGuiOpenBookNMS.openBook(player, bookItemStack);
	}
}
