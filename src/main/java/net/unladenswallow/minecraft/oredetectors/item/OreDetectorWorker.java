package net.unladenswallow.minecraft.oredetectors.item;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.unladenswallow.minecraft.oredetectors.FFLogger;

public class OreDetectorWorker implements Runnable {

	private EntityPlayer player;
	private Block oreBlock;
	private BlockPos startPos;
	private int searchRadius = -1;
	private int searchHeight = -1;
	private boolean isWorking = false;
	private BlockPos lastFoundBlockPos;
	
	public void init(EntityPlayer player, Block oreBlock, int searchRadius, int searchHeight) {
		this.player = player;
		this.oreBlock = oreBlock;
		this.startPos = player.getPosition();
		this.searchRadius = searchRadius;
		this.searchHeight = searchHeight;
		if (this.searchHeight > this.searchRadius) {
			FFLogger.warning("OreDetector search height cannot be greater than search radius.  Setting height = " + this.searchRadius);
			this.searchHeight = this.searchRadius;
		}
	}

	@Override
	public void run() {
		if (player == null || oreBlock == null || startPos == null || searchRadius < 1 || searchHeight < 1) {
			FFLogger.warning("OreDetectorWorker run(): Somebody tried to run me before I was initialized properly.");
		} else {
			this.setWorking(true);
			lastFoundBlockPos = find();
			this.setWorking(false);
		}
	}

	/**
	 * This is a very inefficient linear search that more or less finds the closest matching
	 * block in the configured radius.
	 * 
	 * @return
	 */
	public BlockPos find() {
//		FFLogger.info("OreDetectorWorker find: searching radius / height " + this.searchRadius + "/" + this.searchHeight);
		World world = Minecraft.getMinecraft().theWorld;
		BlockPos nearest = null;
		int startX = startPos.getX();
		int startY = startPos.getY();
		int startZ = startPos.getZ();
		
//		int count = 0;
		
		for (int r = 1; r <= this.searchRadius; r++) {
//			FFLogger.info("OreDetectorWorker find: r = " + r);
			/* For each radius, we are basically searching the hollow cube at that radius.
			 * That means 6 faces to search, while avoiding searching the edges multiple times.
			 * If the searchHeight is less than the searchRadius, then we will skip the
			 * Y faces for values of r > searchHeight.
			 */
			int maxYsearch = Math.min(r, searchHeight);
			// Search the + and - z faces
			for (int x = -1*r; x <= r && nearest == null; x++) {
				for (int y = -1 * maxYsearch; y <= maxYsearch && nearest == null; y++) {
//					FFLogger.info("[" + x + "," + y + "," + r + "]");
//					FFLogger.info("[" + x + "," + y + "," + -1*r + "]");
//					count+=2;
					if (world.getBlockState(new BlockPos(startX + x, startY + y, startZ + r)).getBlock() == this.oreBlock) {
						nearest = new BlockPos(startX + x, startY + y, startZ + r);
					} else if (world.getBlockState(new BlockPos(startX + x, startY + y, startZ - r)).getBlock() == this.oreBlock) {
						nearest = new BlockPos(startX + x, startY + y, startZ - r);
					}
				}
			}
			// Search the + and - x faces
			for (int z = -1*(r-1); z <= (r-1) && nearest == null; z++) {
				for (int y = -1 * maxYsearch; y <= maxYsearch && nearest == null; y++) {
					//					FFLogger.info("[" + r + "," + y + "," + z + "]");
					//					FFLogger.info("[" + -1*r + "," + y + "," + z + "]");
					//					count+=2;
					if (world.getBlockState(new BlockPos(startX + r, startY + y, startZ + z)).getBlock() == this.oreBlock) {
						nearest = new BlockPos(startX + r, startY + y, startZ + z);
					} else if (world.getBlockState(new BlockPos(startX - r, startY + y, startZ + z)).getBlock() == this.oreBlock) {
						nearest = new BlockPos(startX - r, startY + y, startZ + z);
					}
				}
			}
			// Search the + and - y faces if we haven't already
			if (r <= searchHeight) {
				for (int x = -1*(r-1); x <= (r-1) && nearest == null; x++) {
					for (int z = -1*(r-1); z <= (r-1) && nearest == null; z++) {
						//						FFLogger.info("[" + x + "," + r + "," + z + "]");
						//						FFLogger.info("[" + x + "," + -1*r + "," + z + "]");
						//						count+=2;
						if (world.getBlockState(new BlockPos(startX + x, startY + r, startZ + z)).getBlock() == this.oreBlock) {
							nearest = new BlockPos(startX + x, startY + r, startZ + z);
						} else if (world.getBlockState(new BlockPos(startX + x, startY - r, startZ + z)).getBlock() == this.oreBlock) {
							nearest = new BlockPos(startX + x, startY - r, startZ + z);
						}
					}
				}
			}
		}
//		FFLogger.info("Count = " + count);
//		FFLogger.info((nearest == null) ? "Nothing found" : "Found " + (new ItemStack(this.oreBlock)).getDisplayName() + " at " + nearest.toString());
		return nearest;
	}
	
	private void setWorking(boolean isWorking) {
		this.isWorking = isWorking;
	}
	
	public boolean isWorking() {
		return this.isWorking;
	}

	public boolean hasDetectedBlock() {
		return lastFoundBlockPos != null;
	}
	
	public BlockPos getLastFoundBlockPos() {
		return lastFoundBlockPos;
	}

}
