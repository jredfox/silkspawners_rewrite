package com.EvilNotch.silkspawners;

import com.EvilNotch.lib.minecraft.BlockUtil;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.MinecraftUtil;
import com.EvilNotch.lib.minecraft.events.ClientBlockPlaceEvent;
import com.EvilNotch.lib.minecraft.registry.GeneralRegistry;
import com.EvilNotch.silkspawners.client.proxy.ServerProxy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = MainJava.MODID,name = "silkspawners", version = MainJava.VERSION,acceptableRemoteVersions = "*", dependencies = "required-after:evilnotchlib@[1.2.3]")
public class MainJava
{
    public static final String MODID = "silkspawners";
    public static final String VERSION = "1.7";
	@SidedProxy(clientSide = "com.EvilNotch.silkspawners.client.proxy.ClientProxy", serverSide = "com.EvilNotch.silkspawners.client.proxy.ServerProxy")
	public static ServerProxy proxy;
	public static String[] versionType = {"Beta","Alpha","Release","Indev","WIPING"};
    
	@EventHandler
	public void preinit(FMLPreInitializationEvent e)
	{
		 Config.loadConfig(e);
	     GeneralRegistry.registerGameRule("CustomPosSpawner", true);
	     GeneralRegistry.registerGameRule("MultiSpawnerCurrentIndex", false);
	     GeneralRegistry.registerGameRule("SpawnerSaveDelay", false);
	}
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init();
    	MinecraftForge.EVENT_BUS.register(new MainJava());
    }
   
	@SubscribeEvent
    public void drop(BlockEvent.BreakEvent e)
    {
    	Block b = e.getState().getBlock();
    	World w = e.getWorld();
    	BlockPos p = e.getPos();
    	EntityPlayer player = e.getPlayer();
    	if(b == null || w == null || p == null || w.isRemote || player == null)
    		return;
    	EnumHand hand = player.getActiveHand();
    	if(hand == null)
    		return;
    	ItemStack s = player.getHeldItem(hand);
    	if(s == null)
    		return;
    	int toollvl = s.getItem().getHarvestLevel(s, "pickaxe",player,e.getState());
    	if(s == null || EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByLocation("minecraft:silk_touch"), s) <= 0 
    			||  toollvl < BlockUtil.getHarvestLevel(e.getState()) )//|| player.isCreative())
    		return;
    	
    	TileEntity tile = w.getTileEntity(p);
    	if(tile == null)
			return;
    	if(b instanceof BlockMobSpawner || tile instanceof TileEntityMobSpawner)
    	{
    		int meta = b.getMetaFromState(e.getState());
    		if(meta < 0)
    			meta = 0;
    		ItemStack stack = new ItemStack(b,1,meta);
    		NBTTagCompound nbt = new NBTTagCompound();
    		tile.writeToNBT(nbt);
    		int delay = nbt.getInteger("Delay");
    		int max = nbt.getInteger("MaxSpawnDelay");
    		if(delay <= max && !w.getGameRules().getBoolean("SpawnerSaveDelay"))
    			nbt.setInteger("Delay", Config.default_Delay);//makes spanwers stack but, if custom delay will grab it note: delay is live and isn't the same you used in the command block
    		int x = nbt.getInteger("x");
    		int y = nbt.getInteger("y");
    		int z = nbt.getInteger("z");
    		nbt.removeTag("x");
			nbt.removeTag("y");
			nbt.removeTag("z");
    		//Supports custom pos spawners
    		if(SpawnerUtil.isCustomSpawnerPos(nbt,"Pos") && w.getGameRules().getBoolean("CustomPosSpawner"))
    			SpawnerUtil.setOffsets(nbt,x,y,z);
    		nbt.removeTag("id");
    		//if gamerule force multi index spawners to stack warning will loose initial index
    		if(SpawnerUtil.multiIndexSpawner(nbt) && !w.getGameRules().getBoolean("MultiSpawnerCurrentIndex"))
    		{
    			NBTTagCompound compound = nbt.getTagList("SpawnPotentials", 10).getCompoundTagAt(0).getCompoundTag("Entity");
    			nbt.setTag("SpawnData", compound);
    		}
    		NBTTagCompound data = nbt.getCompoundTag("SpawnData");
    		String name = data.getString("id");
    		NBTTagCompound display = new NBTTagCompound();
    		String entName = EntityUtil.getUnlocalizedName(data,w);
    		NBTTagCompound jockey = SpawnerUtil.getJockieNBT(data);
    		if(jockey != null)
    		{
    			entName = EntityUtil.getUnlocalizedName(jockey,w);
    			display.setBoolean("isJockey", true);
    		}
    		display.setString("EntName", entName);
    		nbt.setTag("display", display);
    		nbt.setString("silkTag", name);
    		stack.setTagCompound(nbt);

    		BlockUtil.DropBlock(w,p,stack);
    		if(!player.isCreative())
    		{
    			int chance = (int)(Math.random()*11);
    			if(chance >= 8 && s.getItem() == Items.GOLDEN_PICKAXE || s.getItem() != Items.GOLDEN_PICKAXE)
    				s.getItem().setDamage(s,s.getItemDamage()+1);
    		}
    		w.setBlockToAir(p);
    		e.setCanceled(true);
    	}
    }
	@SubscribeEvent
    public void read(ClientBlockPlaceEvent e)
    {
    	readSpawner(e.getState(),e.getWorld(),e.getPos(),e.getPlayer(),e.getHand());
    }
	@SubscribeEvent
    public void read(BlockEvent.PlaceEvent e)
    {
    	readSpawner(e.getState(),e.getWorld(),e.getPos(),e.getPlayer(),e.getHand() );
    }
	public void readSpawner(IBlockState state, World w,BlockPos p, EntityPlayer player,EnumHand hand) 
	{
	   if(state == null || player == null || p == null || hand == null || w == null)
		   return;
	   Block b = state.getBlock();
	   TileEntity tile = w.getTileEntity(p);
	   ItemStack s = player.getHeldItem(hand);
	   if(!(tile instanceof TileEntityMobSpawner) || s == null || s.getTagCompound() == null)
		   return;
	   NBTTagCompound nbt = s.getTagCompound();
	   nbt = nbt.copy();
	   nbt.removeTag("silkTag");
	   if(SpawnerUtil.isCustomSpawnerPos(nbt,"offsets"))
		   SpawnerUtil.reAlignSpawnerPos(nbt, p.getX(), p.getY(), p.getZ() );
	   nbt.setInteger("x", p.getX());
	   nbt.setInteger("y", p.getY());
	   nbt.setInteger("z", p.getZ());
	   tile.readFromNBT(nbt);
	   tile.markDirty();
	   w.notifyBlockUpdate(p, state, w.getBlockState(p), 3);//fixes issues
	}
}
