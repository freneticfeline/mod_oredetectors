package net.unladenswallow.minecraft.oredetectors;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.unladenswallow.minecraft.oredetectors.item.ItemOreDetector;


@Mod(modid = ModOreDetectors.MODID, useMetadata = true, acceptedMinecraftVersions="[1.12,1.13)", acceptableRemoteVersions="[1.12,1.13)")
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
//	public static ItemOreDetector ironBarsDetector;
	
	public static SoundEvent pingSoundEvent;
	public static SoundEvent chargeSoundEvent;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent preInitEvent) {
	    MinecraftForge.EVENT_BUS.register(this);
		ModOreDetectors.proxy.preInit(preInitEvent);
		
		diamondDetector = new ItemOreDetector("diamond_detector", Blocks.DIAMOND_ORE, Items.DIAMOND);
		emeraldDetector = new ItemOreDetector("emerald_detector", Blocks.EMERALD_ORE, Items.EMERALD);
		redstoneDetector = new ItemOreDetector("redstone_detector", Blocks.REDSTONE_ORE, Items.REDSTONE);
		goldDetector = new ItemOreDetector("gold_detector", Blocks.GOLD_ORE, Items.GOLD_INGOT);
		lapisDetector = new ItemOreDetector("lapis_detector", Blocks.LAPIS_ORE, Item.getItemFromBlock(Blocks.LAPIS_BLOCK));
		ironDetector = new ItemOreDetector("iron_detector", Blocks.IRON_ORE, Items.IRON_INGOT);
		quartzDetector = new ItemOreDetector("quartz_detector", Blocks.QUARTZ_ORE, Items.QUARTZ);
//		ironBarsDetector = new ItemOreDetector("ironbar_detector", Blocks.iron_bars, Item.getItemFromBlock(Blocks.iron_bars));
		
        pingSoundEvent = new SoundEvent(new ResourceLocation(MODID, "oreDetectorPing")).setRegistryName(MODID + ":oreDetectorPing");
        chargeSoundEvent = new SoundEvent(new ResourceLocation(MODID, "oreDetectorCharge")).setRegistryName(MODID + ":oreDetectorCharge");
		
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
	    event.getRegistry().registerAll(diamondDetector, emeraldDetector, redstoneDetector,
	            goldDetector, lapisDetector, ironDetector, quartzDetector);
	}
	
	@SubscribeEvent
	public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
	    event.getRegistry().registerAll(pingSoundEvent, chargeSoundEvent);
	}
	
	@EventHandler
	public void init (FMLInitializationEvent event) {
		ModOreDetectors.proxy.init(event);
		FFLogger.info("Initializing " + ModOreDetectors.MODID);
		addSmelting();
	}
	
	private void addSmelting() {
	}

}
