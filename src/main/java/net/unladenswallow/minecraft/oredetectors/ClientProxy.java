package net.unladenswallow.minecraft.oredetectors;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.unladenswallow.minecraft.oredetectors.item.ItemOreDetector;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        registerDetectorWithVariants(ModOreDetectors.diamondDetector, "diamond_detector");
        registerDetectorWithVariants(ModOreDetectors.emeraldDetector, "emerald_detector");
        registerDetectorWithVariants(ModOreDetectors.redstoneDetector, "redstone_detector");
        registerDetectorWithVariants(ModOreDetectors.goldDetector, "gold_detector");
        registerDetectorWithVariants(ModOreDetectors.lapisDetector, "lapis_detector");
        registerDetectorWithVariants(ModOreDetectors.ironDetector, "iron_detector");
        registerDetectorWithVariants(ModOreDetectors.quartzDetector, "quartz_detector");
//        registerDetectorWithVariants(ModOreDetectors.ironBarsDetector, "ironbar_detector");

    }

    private void registerDetectorWithVariants(ItemOreDetector detector, String itemName) {
        registerItem(detector, itemName);
        MinecraftForge.EVENT_BUS.register(detector);
	}

	protected void registerItem(Item item, String name) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
    		.register(item, 0, new ModelResourceLocation(new ResourceLocation(ModOreDetectors.MODID, name), "inventory"));
    }
    
    protected void registerItemFromBlock(Block block, String name) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
    		.register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(new ResourceLocation(ModOreDetectors.MODID, name), "inventory"));
    	
    }
    
    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
    
}
