package com.EvilNotch.silkspawners;

import com.EvilNotch.lib.minecraft.BlockUtil;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.EnumChatFormatting;
import com.EvilNotch.lib.minecraft.MinecraftUtil;
import com.EvilNotch.lib.minecraft.content.LangEntry;
import com.EvilNotch.lib.minecraft.content.client.creativetab.BasicCreativeTab;
import com.EvilNotch.lib.minecraft.events.ClientBlockPlaceEvent;
import com.EvilNotch.lib.minecraft.events.TileStackSyncEvent;
import com.EvilNotch.lib.minecraft.network.NetWorkHandler;
import com.EvilNotch.lib.minecraft.registry.GeneralRegistry;
import com.EvilNotch.silkspawners.client.proxy.ServerProxy;
import com.EvilNotch.silkspawners.client.render.item.NEISpawnerRender;
import com.EvilNotch.silkspawners.commands.CommandMTHand;
import com.EvilNotch.silkspawners.commands.CommandSpawner;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = MainJava.MODID,name = "silkspawners", version = MainJava.VERSION,acceptableRemoteVersions = "*", dependencies = "required-after:evilnotchlib@[1.2.3]")
public class MainJava
{
    public static final String MODID = "silkspawners";
    public static final String VERSION = "1.8";
	@SidedProxy(clientSide = "com.EvilNotch.silkspawners.client.proxy.ClientProxy", serverSide = "com.EvilNotch.silkspawners.client.proxy.ServerProxy")
	public static ServerProxy proxy;
	public static String[] versionType = {"Beta","Alpha","Release","Indev","WIPING"};
	public static boolean dungeontweaks = false;
	public static BasicCreativeTab tab_living;
	public static BasicCreativeTab tab_nonliving;
	public static BasicCreativeTab tab_custom;
	public static ItemSpawner mob_spawner;
    
	@EventHandler
	public void preinit(FMLPreInitializationEvent e)
	{
		Config.loadConfig(e);
	    GeneralRegistry.registerGameRule("CustomPosSpawner", true);
	    GeneralRegistry.registerGameRule("MultiSpawnerCurrentIndex", false);
	    GeneralRegistry.registerGameRule("SpawnerSaveDelay", false);
	    GeneralRegistry.registerCommand(new CommandMTHand());
	    GeneralRegistry.registerCommand(new CommandSpawner());
	    mob_spawner = new ItemSpawner();
	 	ForgeRegistries.ITEMS.register(mob_spawner);
	 	dungeontweaks = Loader.isModLoaded("dungeontweaks");
	 	 
	    tab_living = new BasicCreativeTab(new ResourceLocation("silkspawners:living"), new ItemStack(Blocks.MOB_SPAWNER),new LangEntry("Living Mob Spawners","en_us"));
	    tab_nonliving = new BasicCreativeTab(new ResourceLocation("silkspawners:nonliving"), new ItemStack(Blocks.MOB_SPAWNER),new LangEntry("NonLiving Mob Spawners","en_us"));
	    tab_custom = new BasicCreativeTab(new ResourceLocation("silkspawners:custom"), new ItemStack(Blocks.MOB_SPAWNER),new LangEntry("Jockeys & Custom Entries","en_us"));
	}
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init();
    	MinecraftForge.EVENT_BUS.register(new MainJava());
    }
    @EventHandler
    public void postinit(FMLPostInitializationEvent event)
    {
    	proxy.postinit();
    }
    @EventHandler
    public void postinit(FMLLoadCompleteEvent event)
    {
    	proxy.onLoadComplete();
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
    		nbt.setString("silkTag", name);//have the current index of the spawners resource location become the silktag not the jockeyname
    		
    		NBTTagCompound display = new NBTTagCompound();
    		
    		NBTTagCompound jockey = getJockieNBT(data);
    		if(jockey != null)
    		{
    			name = jockey.getString("id");
    			ResourceLocation loc = new ResourceLocation(name);
    			String entName = getUnlocalizedName(loc,jockey);
    			display.setBoolean("isJockey", true);//used for dynamic translation append jockey to the end of the entity name
    			display.setString("EntName", entName);
    			display.setString("EntColor", getColor(loc,jockey));
    		}
    		else
    		{
    			ResourceLocation loc = new ResourceLocation(name);
    			String entName = getUnlocalizedName(loc,data);
    			String color = getColor(loc,data);
    			if(entName == null)
    			{
    				entName = "silkspawners.blankspawner.name";
    				nbt.setBoolean("isBlank", true);
    			}
        		display.setString("EntName", entName);
        		display.setString("EntColor", color);
    		}
    		
    		nbt.setTag("display", display);
    		stack.setTagCompound(nbt);
    		
        	if(dungeontweaks)
        	{
        		NBTTagCompound caps = nbt.getCompoundTag("ForgeCaps");
        		if(caps == null)
        		{
        			caps = new NBTTagCompound();
        			nbt.setTag("ForgeCaps", caps);
        		}
        		caps.setInteger("dungeontweaks:hasscanned", 1);
        	}

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
	/**
	 * supports chicken jockeys as their names are the one below it
	 * @param data
	 * @return
	 */
	public NBTTagCompound getJockieNBT(NBTTagCompound data) 
	{
		ResourceLocation loc = new ResourceLocation(data.getString("id"));
		if(loc.toString().equals("minecraft:chicken"))
		{
			NBTTagList list = data.getTagList("Passengers",10);
			if(!list.hasNoTags())
			{
				for(int i=0;i<list.tagCount();i++)
				{
					if(list.getCompoundTagAt(i).hasKey("Passengers"))
					{
						return SpawnerUtil.getJockieNBT(data);
					}
				}
				return data;
			}
		}
		return SpawnerUtil.getJockieNBT(data);
	}
	public static String getColor(ResourceLocation loc, NBTTagCompound data) 
	{
		String str = loc.toString();
		if(str.equals("minecraft:skeleton_horse") && data.getBoolean("SkeletonTrap"))
		{
			return Config.colorToConfig.get(EnumChatFormatting.DARK_RED);
		}
		else if(str.equals("minecraft:rabbit") && data.getInteger("RabbitType") == 99)
		{
			return Config.colorToConfig.get(EnumChatFormatting.RED);
		}
		else if(str.equals("minecraft:sheep") && data.getString("CustomName").equals("jeb_"))
		{
			return "rainbow";
		}
		else if(str.equals("minecraft:chicken"))
		{
			//get the color from above it
			if(data.hasKey("Passengers"))
			{
				NBTTagCompound nbt = data.getTagList("Passengers", 10).getCompoundTagAt(0);
				return Config.colorToConfig.get(getCachedInfo(new ResourceLocation(nbt.getString("id")))[2]);
			}
		}
		String[] parts = getCachedInfo(loc);
		if(parts == null)
			return Config.text_default;
		return Config.colorToConfig.get(parts[2]);
	}
	public static String getUnlocalizedName(ResourceLocation loc,NBTTagCompound data) 
	{
		String str = loc.toString();
		if(str.equals("minecraft:skeleton_horse") && data.getBoolean("SkeletonTrap"))
		{
			return "silkspawners.skeletontrap.name";
		}
		else if(str.equals("minecraft:rabbit") && data.getInteger("RabbitType") == 99)
		{
			return "silkspawners.killerrabbit.name";
		}
		else if(str.equals("minecraft:creeper") && data.getInteger("powered") == 1)
		{
			return "silkspawners.poweredcreeper.name";
		}
		String[] parts = getCachedInfo(loc);
		if(parts == null)
			return null;
		return parts[0];
	}
	/**
	 * null means the entity is either blacklisted or doesn't exists
	 */
	public static String[] getCachedInfo(ResourceLocation loc) {
		String[] parts = EntityUtil.living.containsKey(loc) ? EntityUtil.living.get(loc) : (EntityUtil.livingbase.containsKey(loc) ? EntityUtil.livingbase.get(loc) : EntityUtil.nonliving.get(loc) );
		return parts;
	}
	public static ResourceLocation getResourceLocation(NBTTagCompound tag, String key) {
		if(tag == null)
			return null;
		return new ResourceLocation(tag.getString(key));
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
	public void readSpawner(IBlockState state, World w,BlockPos pos, EntityPlayer player,EnumHand hand) 
	{
	   if(state == null || player == null || pos == null || hand == null || w == null)
		   return;
	   Block b = state.getBlock();
	   ItemStack s = player.getHeldItem(hand);
	   TileEntity tile = w.getTileEntity(pos);
	   if(s == null || s.getTagCompound() == null || !(tile instanceof TileEntityMobSpawner))
		   return;
	   NBTTagCompound nbt = s.getTagCompound();
	   if(nbt.hasKey("BlockEntityTag"))
		   return;
	   ItemBlock.setTileNBT(w, player, pos, s, nbt, false);//new format fires EvilNotchLib TileStackSync Events for compatibility and overrides
	   w.notifyBlockUpdate(pos, state, w.getBlockState(pos), 3);//fixes issues
	}
	@SubscribeEvent(priority=EventPriority.HIGH)
    public void syncOffsets(TileStackSyncEvent.Pre e)
    {
		if(e.isBlockData || !(e.tile instanceof TileEntityMobSpawner))
			return;
		if(SpawnerUtil.isCustomSpawnerPos(e.nbt,"offsets"))
		   SpawnerUtil.reAlignSpawnerPos(e.nbt, e.pos.getX(), e.pos.getY(), e.pos.getZ() );
    }
	/**
	 * allow regular players permission to place a spawner
	 */
	@SubscribeEvent(priority=EventPriority.HIGH)
    public void syncDefault(TileStackSyncEvent.Permissions e)
    {
    	if(!e.isBlockData && e.tile instanceof TileEntityMobSpawner)
    	{
    		e.opsOnly = false;
    	}
    }
}
