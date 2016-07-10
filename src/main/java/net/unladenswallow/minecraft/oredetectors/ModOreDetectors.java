package net.unladenswallow.minecraft.oredetectors;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.unladenswallow.minecraft.oredetectors.item.ItemOreDetector;


@Mod(modid = ModOreDetectors.MODID, useMetadata = true, acceptedMinecraftVersions="[1.8,1.9)", acceptableRemoteVersions="[1.8,1.9)")
public class ModOreDetectors {

	public static final String MODID = "mod_oredetectors";
	
	@SidedProxy(clientSide="net.unladenswallow.minecraft.oredetectors.ClientProxy", serverSide="net.unladenswallow.minecraft.oredetectors.ServerProxy")
	public static CommonProxy proxy;
	
	public static ItemOreDetector diamondDetector;
	public static ItemOreDetector emeraldDetector;
	public static ItemOreDetector redstoneDetector;
	public static ItemOreDetector goldDetector;
	public static ItemOreDetector lapisDetector;
	public static ItemOreDetector ironDetector;
	public static ItemOreDetector quartzDetector;
	public static ItemOreDetector ironBarsDetector;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent preInitEvent) {
		ModOreDetectors.proxy.preInit(preInitEvent);
		
		diamondDetector = new ItemOreDetector("diamond_detector", Blocks.diamond_ore, Items.diamond);
		emeraldDetector = new ItemOreDetector("emerald_detector", Blocks.emerald_ore, Items.emerald);
		redstoneDetector = new ItemOreDetector("redstone_detector", Blocks.redstone_ore, Items.redstone);
		goldDetector = new ItemOreDetector("gold_detector", Blocks.gold_ore, Items.gold_ingot);
		lapisDetector = new ItemOreDetector("lapis_detector", Blocks.lapis_ore, Item.getItemFromBlock(Blocks.lapis_block));
		ironDetector = new ItemOreDetector("iron_detector", Blocks.iron_ore, Items.iron_ingot);
		quartzDetector = new ItemOreDetector("quartz_detector", Blocks.quartz_ore, Items.quartz);
		ironBarsDetector = new ItemOreDetector("ironbar_detector", Blocks.iron_bars, Item.getItemFromBlock(Blocks.iron_bars));
		
		
		GameRegistry.registerItem(diamondDetector, "diamond_detector");
		GameRegistry.registerItem(emeraldDetector, "emerald_detector");
		GameRegistry.registerItem(redstoneDetector, "redstone_detector");
		GameRegistry.registerItem(goldDetector, "gold_detector");
		GameRegistry.registerItem(lapisDetector, "lapis_detector");
		GameRegistry.registerItem(ironDetector, "iron_detector");
		GameRegistry.registerItem(quartzDetector, "quartz_detector");
		GameRegistry.registerItem(ironBarsDetector, "ironbar_detector");

	}
	
	@EventHandler
	public void init (FMLInitializationEvent event) {
		ModOreDetectors.proxy.init(event);
		FFLogger.info("Initializing " + ModOreDetectors.MODID);
		addRecipes();
		addSmelting();
	}
	
	private void addRecipes() {
		diamondDetector.registerRecipe();
		emeraldDetector.registerRecipe();
		redstoneDetector.registerRecipe();
		goldDetector.registerRecipe();
		lapisDetector.registerRecipe();
		ironDetector.registerRecipe();
		quartzDetector.registerRecipe();
}
	
	private void addSmelting() {
	}

}
