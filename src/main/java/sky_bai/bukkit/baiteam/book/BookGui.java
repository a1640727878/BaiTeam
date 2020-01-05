package sky_bai.bukkit.baiteam.book;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;

import net.md_5.bungee.api.chat.BaseComponent;
import sky_bai.bukkit.baiteam.BaiTeam;

public class BookGui {
	private ItemStack bookItemStack;
	private BookMeta bookMeta;
	
	private BookGui() {
		bookItemStack = new ItemStack(Material.WRITTEN_BOOK);
		bookMeta = (BookMeta) bookItemStack.getItemMeta();
		bookMeta.setAuthor("Admin");
		bookMeta.setTitle("BookGui");
		bookMeta.setGeneration(Generation.TATTERED);
	}
	
	public static BookGui getBookGui() {
		return new BookGui();
	}	
	
	public void setPage(int page, BaseComponent... data) {
		bookMeta.spigot().setPage(page, data);
	}
	
	public BookGui addPage(BaseComponent... strings) {
		bookMeta.spigot().addPage(strings);
		return this;
	}
	
	public void openBook(Player player) {
		bookItemStack.setItemMeta(bookMeta);
		BaiTeam.getBaiTeam().openBook(bookItemStack, player);
	}
}
