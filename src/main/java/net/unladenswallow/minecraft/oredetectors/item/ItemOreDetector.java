package net.unladenswallow.minecraft.oredetectors.item;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.unladenswallow.minecraft.oredetectors.ModOreDetectors;

public class ItemOreDetector extends Item {

	private static int DETECT_BLOCK_RADIUS = 40;
	private static int BOOST_BLOCK_RADIUS = 200;
	private static int DETECT_BLOCK_HEIGHT = 10;
	private static int BOOST_BLOCK_HEIGHT = 40;
	private static int MIN_PING_FREQ = 10; // in world ticks
	private static int MAX_PING_FREQ = 60; // in world ticks
	private static int ACTIVE_TICKS = 5;
	
	private Block oreBlock;
	private Item recipeItem;
	private OreDetectorWorker worker;
	private int slotDetectorLastSeen;
	private long tickOfLastPing = 0;
	
	private boolean pingActive = false;
	private boolean signalBoost = false;
	
	public ItemOreDetector(String unlocalizedName, Block oreBlock, Item recipeItem) {
		super();
		this.setUnlocalizedName(unlocalizedName);
	    this.setRegistryName(ModOreDetectors.MODID,unlocalizedName);
		this.oreBlock = oreBlock;
		this.recipeItem = recipeItem;
		this.setCreativeTab(CreativeTabs.MISC);
		this.setMaxDamage(32);
		this.setMaxStackSize(1);
		
		/* This is a hack, but I don't know how else to do this in 1.9.
		 * Abuse the "cast" property and the override in order to render the "active"
		 * model of the detector at the same time that the ping sound occurs.
		 */
        this.addPropertyOverride(new ResourceLocation("cast"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn)
            {
                return pingActive ? 1.0F : 0.0F;
            }
        });
	}
	
	protected Block getOreBlock() {
		return this.oreBlock;
	}
	
	@SubscribeEvent
	public void onPlayerTickEvent(PlayerTickEvent event) {
		if (event.side == Side.CLIENT) {
			Minecraft minecraft = Minecraft.getMinecraft();
			EntityPlayer player = event.player;
			if (!minecraft.isGamePaused() && player != null // Is a game actually in progress?
					&& event.phase == Phase.END // We don't want to trigger twice per tick
					&& player.worldObj.getWorldTime() % Math.min(MIN_PING_FREQ, ACTIVE_TICKS) == 0 // No point executing unless we're going to do something
					&& detectorInInventory(player) ) { // Only if this detector is in the player's inventory
//			    FFLogger.info("Checking");
			    this.pingActive = false;
				if (worker != null && worker.hasDetectedBlock()) {
					BlockPos nearestMatchingPos = worker.getLastFoundBlockPos();
//                    FFLogger.info("Got block from worker: " + nearestMatchingPos.toString());
					int pingFrequency = MAX_PING_FREQ;
					/* Ping whenever detector is in the inventory, but only exhibit homing
					 * behavior when the detector is being held
					 */
					if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == this) {
						pingFrequency =
							pingFrequencyFromDirection(player.rotationPitch,
							player.rotationYaw,
							pitchToBlock(player.getPosition().up(), nearestMatchingPos),
							yawToBlock(player.getPosition().up(), nearestMatchingPos));
					}
//					FFLogger.info("ItemOreDetector onPlayerTickEvent: pingFrequency = " + pingFrequency
//							+ "worldTime = " + player.worldObj.getWorldTime() + "tickOfLastPing = " + tickOfLastPing);
					if (player.worldObj.getWorldTime() >= tickOfLastPing + pingFrequency) {
						double distSq = player.getDistanceSqToCenter(nearestMatchingPos);
//						FFLogger.info("ItemOreDetector onPlayerTickEvent [%s]: "
//								+ "\ndistance to nearest ore block = " + distSq
//								+ "\nvolume = " + volumeFromDistance(distSq)
//								+ "\nplayerVector = " + player.getPosition().toString()
//								+ "\nplayer pitch/yaw = " + player.rotationPitch + " / " + player.rotationYaw
//								+ "\n\tnearest block pitch/yaw = " + pitchToBlock(player.getPosition().up(), nearestMatchingPos) + " / " + yawToBlock(player.getPosition().up(), nearestMatchingPos)
//								, event.side);
						player.playSound(ModOreDetectors.pingSoundEvent, volumeFromDistance(distSq) * 2.0f, 1.0f);
						tickOfLastPing = player.worldObj.getWorldTime();
						this.pingActive = true;
					}
				}
				/* To keep from bogging down the game, we only actually search for a block every few seconds.
				 * (We'll just keep pinging based on the last block that we found.) 
				 */
				if (player.worldObj.getWorldTime() % (MIN_PING_FREQ * 5) == 0  
						&& (worker == null || !worker.isWorking())) {
					int search_radius = this.signalBoost ? BOOST_BLOCK_RADIUS : DETECT_BLOCK_RADIUS;
					int search_height = this.signalBoost ? BOOST_BLOCK_HEIGHT : DETECT_BLOCK_HEIGHT;
					worker = new OreDetectorWorker();
					worker.init(player, this.getOreBlock(), search_radius, search_height);
					Thread workerThread = new Thread(worker);
					workerThread.setPriority(Thread.MIN_PRIORITY);
//					FFLogger.info("ItemOreDetector onPlayerTickEvent [%s]: staring worker", event.side);
					workerThread.start();
				}
			}
		}
	}

	private int pingFrequencyFromDirection(float playerPitch, float playerYaw, float pitchToBlock,
			float yawToBlock) {
		float dP = Math.abs(playerPitch - pitchToBlock);
		float dY = Math.abs((playerYaw % 360) - (yawToBlock % 360));
		if (dY > 180.0) {
			dY = Math.abs(360.0f - dY);
		}
//		FFLogger.info("ItemOreDetector pingFrequencyFromDirection: dP / dY = " + dP + " / " + dY);
		if (dP < 20 && dY < 15) {
			return MIN_PING_FREQ;
		} else if (dP < 25 && dY < 25) {
			return MIN_PING_FREQ + 5;
		} else if (dP < 30 && dY < 35) {
			return MIN_PING_FREQ + 10;
		} else if (dP < 35 && dY < 45) {
			return MIN_PING_FREQ + 15;
		} else if (dP < 40 && dY < 55) {
			return MIN_PING_FREQ + 20;
		} else if (dP < 45 && dY < 65) {
			return MIN_PING_FREQ + 25;
		} else {
			return MAX_PING_FREQ;
		}
	}

	private boolean detectorInInventory(EntityPlayer player) {
		InventoryPlayer inventory = player.inventory;
		// To save time, first check the last place we saw a detector
		if (inventory.mainInventory[slotDetectorLastSeen] != null && inventory.mainInventory[slotDetectorLastSeen].getItem() == this) {
//			FFLogger.info("Found detector in the last place we saw it");
			return true;
		}
		for (int i = 0; i < inventory.mainInventory.length; i++) {
			ItemStack curItemStack = inventory.mainInventory[i];
			if (curItemStack != null && curItemStack.getItem() == this) {
				slotDetectorLastSeen = i;
//				FFLogger.info("Found detector in slot " + i);
				return true;
			}
		}
		return false;
	}
	
	private float yawToBlock(BlockPos src, BlockPos dest) {
		double dx = src.getX() - dest.getX() - 0.5f;
		double dz = src.getZ() - dest.getZ() - 0.5f;
		// All this math means I don't really understand it and I'm hacking it until it works
		return (float) Math.abs((180.0d + (Math.atan2(-dx, dz) * 180/Math.PI)) % 360.0d);
	}

	private float pitchToBlock(BlockPos src, BlockPos dest) {
		double dx = src.getX() - dest.getX() - 0.5d;
		double dy = src.getY() - dest.getY() - 0.5d;
		double dz = src.getZ() - dest.getZ() - 0.5d;
		return (float) (Math.atan2(dy, Math.sqrt(dx*dx + dz*dz)) * 180.0d / Math.PI);
	}

	private float volumeFromDistance(double distanceSq) {
		/* The volume should be max (1.0) within 1 block of the target block, then drop to minimum (0.1) by
		 * the time we are about half of the search radius away from the target block, and remain at minimum
		 * until the block is no longer detected. 
		 */
		return Math.min(1.0f, // Make sure it never goes over 1.0
						Math.max(0.1f, // Make sure it never goes under 0.1
								 1.0f - (Math.min((float)DETECT_BLOCK_RADIUS, (float)(Math.sqrt(distanceSq)) - 3.0f) / 15) // Min volume by 18 blocks away
								)
						);
	}

	public void registerRecipe() {
		GameRegistry.addRecipe(new ItemStack(this),
				" C ",
				"RIR",
				" D ",
				'D', this.recipeItem,
				'C', Items.COMPASS,
				'I', Blocks.IRON_BLOCK,
				'R', Items.REPEATER);
	}
	
	@Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }

	@Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
//    	FFLogger.info("ItemOreDetector onItemRightClick");
        playerIn.setActiveHand(hand);
    	return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }
    
    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
    {
        if (player.worldObj.isRemote) {
//            FFLogger.info("ItemOreDetector onUsingTick: count = " + count);
            int ticksInUse = this.getMaxItemUseDuration(stack) - count;
            if (ticksInUse == 20) {
//                FFLogger.info("ItemOreDetector onUsingTick: playing sound");
                player.playSound(ModOreDetectors.chargeSoundEvent, 1.0f, 1.0f);
            } else if (ticksInUse == 40) {
                this.signalBoost = true;
                stack.damageItem(1, player);
            }
        }
    }
    
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase playerIn, int timeLeft) {
//    	FFLogger.info("ItemOreDetector onPlayerStoppedUsing");
    	this.signalBoost = false;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BLOCK;
    }


}
