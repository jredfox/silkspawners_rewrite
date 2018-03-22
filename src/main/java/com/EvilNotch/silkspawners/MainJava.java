package com.EvilNotch.silkspawners;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.math.BigDecimal;

import com.EvilNotch.lib.util.minecraft.BlockUtil;
import com.EvilNotch.lib.util.minecraft.EntityUtil;
import com.EvilNotch.lib.util.minecraft.MinecraftUtil;
import com.EvilNotch.lib.util.minecraft.SpawnerUtil;
import com.EvilNotch.silkspawners.client.proxy.ServerProxy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod(modid = MainJava.MODID,name = "silkspawners", version = MainJava.VERSION,acceptableRemoteVersions = "*", dependencies = "required-after:evilnotchlib")
public class MainJava
{
    public static final String MODID = "silkspawners";
    public static final String VERSION = "1.6.6";
	@SidedProxy(clientSide = "com.EvilNotch.silkspawners.client.proxy.ClientProxy", serverSide = "com.EvilNotch.silkspawners.client.proxy.ServerProxy")
	public static ServerProxy proxy;
	public static String[] versionType = {"Beta","Alpha","Release","Indev","WIPING"};
    
	 @EventHandler
	public void preinit(FMLPreInitializationEvent e)
	{
		 Config.loadConfig(e);
	}
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init();
    	MinecraftForge.EVENT_BUS.register(new MainJava());
    	ForgeRegistries.ITEMS.register(new ItemMobSpawner());
    }
    @EventHandler
    public void worldLoad(FMLServerStartingEvent e)
    {
    	World w = e.getServer().getEntityWorld();
    	GameRules g = w.getGameRules();
    	String pos = "CustomPosSpawner";
    	String index = "MultiSpawnerCurrentIndex";
    	String delay = "SpawnerSaveDelay";
    	ValueType type = GameRules.ValueType.BOOLEAN_VALUE;
    	MinecraftUtil.addGameRule(g,pos,true,type);
    	MinecraftUtil.addGameRule(g,index,false,type);//is false for stacking purposes true for data
    	MinecraftUtil.addGameRule(g,delay,false,type);//is false for stacking purposes true for data
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
    			||  toollvl < BlockUtil.getHarvestLevel(b) )//|| player.isCreative())
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
    		String white = ChatFormatting.WHITE;
    		String red = ChatFormatting.RED;
    		//if gamerule force multi index spawners to stack warning will loose initial index
    		if(SpawnerUtil.multiIndexSpawner(nbt) && !w.getGameRules().getBoolean("MultiSpawnerCurrentIndex"))
    		{
    			NBTTagCompound compound = nbt.getTagList("SpawnPotentials", 10).getCompoundTagAt(0).getCompoundTag("Entity");
    			nbt.setTag("SpawnData", compound);
    		}
    		NBTTagCompound data = nbt.getCompoundTag("SpawnData");
    		String name = data.getString("id");
    		NBTTagCompound display = new NBTTagCompound();
    		String entName = EntityUtil.TransLateEntity(data,w);
    		if(entName == null)
    			entName = "Blank";
    		NBTTagCompound jockey = SpawnerUtil.getJockieNBT(data);
    		if(jockey != null)
    			entName = EntityUtil.TransLateEntity(jockey,w) + " Jockey";
    		
    		String blockname = entName;
    		if( (entName + " " + b.getLocalizedName() ).length() <= Config.maxSpawnerName)
    			blockname += " " + b.getLocalizedName();
    		display.setString("Name", white + blockname );
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
	
    /**
     * This fixes the vanilla ItemMonsterSpawner Bugs
     */
    @SubscribeEvent
  	public void onVanillaEgg(PlayerInteractEvent.RightClickBlock e)
  	{
  		World w = e.getWorld();
  		EntityPlayer p = e.getEntityPlayer();
  	
  		if(p == null || w == null || w.isRemote || e.getHand() == null)
  			return;
  		ItemStack stack = p.getHeldItem(e.getHand());
  		BlockPos pos = e.getPos();
  		EnumFacing face = e.getFace();
  		if(pos == null || face == null)
  			return;
  		
  		TileEntity tile = w.getTileEntity(pos);
  		IBlockState state =  w.getBlockState(pos);
  		
  		if (stack == null || stack.getItem() != Items.SPAWN_EGG || tile == null || !(tile instanceof TileEntityMobSpawner) || state == null)
  			return;
  		
  		  if (tile instanceof TileEntityMobSpawner)
  		  {
  			  NBTTagCompound nbt = new NBTTagCompound();
  		   	  tile.writeToNBT(nbt);
  		   	  ResourceLocation stringId = ItemMonsterPlacer.getNamedIdFrom(stack);
  			  String name = stringId == null ? null : stringId.toString();
  			  if(name == null)
  				  return;
  			  NBTTagList spawnpot = new NBTTagList();
  			  NBTTagCompound entry = new NBTTagCompound();
  			  entry.setInteger("Weight", 1);
  			
  			  NBTTagCompound entity = new NBTTagCompound();
  			  entity.setString("id",name);
  			  
  			  entry.setTag("Entity", entity);
  			  spawnpot.appendTag(entry);
  			  
  			  nbt.setTag("SpawnPotentials", spawnpot);
  			  NBTTagCompound data = new NBTTagCompound();
  			  data.setString("id", name);
  			  nbt.setTag("SpawnData", data);
  			  if (!p.capabilities.isCreativeMode)
  			       stack.shrink(1);
  			  
  			  tile.readFromNBT(nbt);
  			  tile.markDirty();
  			  w.notifyBlockUpdate(pos, state, w.getBlockState(pos), 3);
  			  e.setCanceled(true);
  		  }
  	  }
}
