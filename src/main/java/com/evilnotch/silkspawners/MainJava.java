package com.evilnotch.silkspawners;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.minecraft.basicmc.auto.creativetab.ICreativeTabHook;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.client.creativetab.BasicCreativeTab;
import com.evilnotch.lib.minecraft.event.PickEvent;
import com.evilnotch.lib.minecraft.event.tileentity.BlockDataEvent;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.minecraft.util.BlockUtil;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.minecraft.util.EnumChatFormatting;
import com.evilnotch.silkspawners.client.proxy.ServerProxy;
import com.evilnotch.silkspawners.commands.CommandMTHand;
import com.evilnotch.silkspawners.packet.PacketAddPass;
import com.evilnotch.silkspawners.packet.handler.PacketAddPassHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = MainJava.MODID, name = "silkspawners", version = MainJava.VERSION,acceptableRemoteVersions = "*", dependencies = "required-after:evilnotchlib@[1.2.3,]")
public class MainJava
{
    public static final String MODID = "silkspawners";
    public static final String VERSION = "1.8.2";//build 15
	@SidedProxy(clientSide = "com.evilnotch.silkspawners.client.proxy.ClientProxy", serverSide = "com.evilnotch.silkspawners.client.proxy.ServerProxy")
	public static ServerProxy proxy;
	public static String[] versionType = {"Beta","Alpha","Release","Indev","WIPING"};
	public static boolean dungeontweaks = false;
	public static BasicCreativeTab tab_living;
	public static BasicCreativeTab tab_nonliving;
	public static BasicCreativeTab tab_custom;
	public static ICreativeTabHook spawnerHook;
	
	public static ResourceLocation dungeonTweaksLoc;
	public static boolean dungeonTweaksLegacy = false;
    
	@EventHandler
	public void preinit(FMLPreInitializationEvent e)
	{
		Config.loadConfig(e);
		proxy.preinit();
	    GeneralRegistry.registerGameRule("CustomPosSpawner", Config.grPosSpawner);
	    GeneralRegistry.registerGameRule("MultiSpawnerCurrentIndex", Config.grCurrentIndex);
	    GeneralRegistry.registerGameRule("SpawnerSaveDelay", Config.grDelay);
	    GeneralRegistry.registerCommand(new CommandMTHand());
	    spawnerHook = new ItemSpawner();
	 	
	 	dungeontweaks = Loader.isModLoaded("dungeontweaks");
	 	if(dungeontweaks)
	 	{
	 		dungeonTweaksLoc = new ResourceLocation("dungeontweaks" + ":" + "hasScanned");
	 		dungeonTweaksLegacy = ReflectionUtil.classForName("com.EvilNotch.dungeontweeks.main.Events.EventDungeon$Post") != null;
	 	}
	 	 
	    tab_living = new BasicCreativeTab(new ResourceLocation("silkspawners:living"), new ItemStack(Blocks.MOB_SPAWNER),new LangEntry("en_us","Living Mob Spawners"));
	    tab_nonliving = new BasicCreativeTab(new ResourceLocation("silkspawners:nonliving"), new ItemStack(Blocks.MOB_SPAWNER),new LangEntry("en_us","NonLiving Mob Spawners"));
	    tab_custom = new BasicCreativeTab(new ResourceLocation("silkspawners:custom"), new ItemStack(Blocks.MOB_SPAWNER),new LangEntry("en_us","Jockeys & Custom Entries"));
	}
	
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init();
    	MinecraftForge.EVENT_BUS.register(new MainJava());
	    NetWorkHandler.registerMessage(PacketAddPassHandler.class, PacketAddPass.class, Side.CLIENT);
    }
    
    @EventHandler
    public void postinit(FMLPostInitializationEvent event)
    {
    	EntityUtil.cacheEnts();
    	proxy.postinit();
    }
    
    @EventHandler
    public void postinit(FMLLoadCompleteEvent event)
    {
    	proxy.onLoadComplete();
    }
    
    @SubscribeEvent
    public void playLogin(PlayerLoggedInEvent event)
    {
    	NetWorkHandler.INSTANCE.sendTo(new PacketAddPass(Config.addPass), (EntityPlayerMP) event.player);//sync server side config for client rendering
    }
   
	@SubscribeEvent
    public void drop(BlockEvent.BreakEvent e)
    {
    	Block b = e.getState().getBlock();
    	World w = e.getWorld();
    	BlockPos p = e.getPos();
    	EntityPlayer player = e.getPlayer();
    	if(b == null || w == null || p == null || w.isRemote || player == null || !w.getGameRules().getBoolean("doTileDrops"))
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
    		writeSpawnerToStack(stack,w,tile,nbt);
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
	 * custom tile entity mob spawner support for vanilla so spawners stack and special features function
	 */
	public void writeSpawnerToStack(ItemStack stack,World w,TileEntity tile, NBTTagCompound nbt) 
	{
		tile.writeToNBT(nbt);
		int delay = nbt.getInteger("Delay");
		int max = nbt.getInteger("MaxSpawnDelay");
		if(delay <= max && !w.getGameRules().getBoolean("SpawnerSaveDelay"))
			nbt.setInteger("Delay", Config.default_Delay);//makes spanwers stack but, if custom delay will grab it note: delay is live and isn't the same you used in the command block
		int x = nbt.getInteger("x");
		int y = nbt.getInteger("y");
		int z = nbt.getInteger("z");
		
		//vanilla tag removal but, do this after getting the pos
		nbt.removeTag("x");
		nbt.removeTag("y");
		nbt.removeTag("z");
		nbt.removeTag("id");
		
		//mod tag removal
		removeDungeonTweaksTag(nbt);
		
		//Supports custom pos spawners
		if(SpawnerUtil.isCustomSpawnerPos(nbt, "Pos") && w.getGameRules().getBoolean("CustomPosSpawner"))
			SpawnerUtil.setOffsets(nbt, x, y, z);
		
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
	}
	@SubscribeEvent
    public void pick(PickEvent.Block e)
    {
		if(!(e.tile instanceof TileEntityMobSpawner) || e.copyTE)
			return;
		if(e.current.isEmpty())
			e.current = new ItemStack(e.state.getBlock(),1,e.state.getBlock().getMetaFromState(e.state));
		writeSpawnerToStack(e.current, e.world, e.tile, new NBTTagCompound());
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
	
	/**
	 * you could technically redirect the nbt to a custom sub compound tag here and set canFire to true but, silkspawners wants the root tag for easier /give command
	 */
	@SubscribeEvent
    public void read(BlockDataEvent.HasTileData e)
    {
		if(e.tile instanceof TileEntityMobSpawner && !e.nbt.hasNoTags())
			e.canFire = true;
    }
	
	@SubscribeEvent(priority=EventPriority.HIGH)
    public void syncOffsets(BlockDataEvent.Merge e)
    {
		if(e.isVanilla || !(e.tile instanceof TileEntityMobSpawner))
			return;
		
		if(SpawnerUtil.isCustomSpawnerPos(e.nbt, "offsets"))
		   SpawnerUtil.reAlignSpawnerPos(e.nbt, e.pos.getX(), e.pos.getY(), e.pos.getZ() );
    }
	
	/**
	 * allow regular players permission to place a spawner
	 */
	@SubscribeEvent(priority=EventPriority.HIGH)
    public void syncDefault(BlockDataEvent.Permissions e)
    {
    	if(!e.isVanilla && e.tile instanceof TileEntityMobSpawner)
    	{
    		e.opsOnly = false;
    	}
    }
	
	public static void removeDungeonTweaksTag(NBTTagCompound nbt) 
	{
		if(!MainJava.dungeontweaks)
			return;
		if(MainJava.dungeonTweaksLegacy)
		{
			nbt.getCompoundTag("ForgeCaps").removeTag(MainJava.dungeonTweaksLoc.toString());
		}
		else
		{
			nbt.removeTag(MainJava.dungeonTweaksLoc.toString());
		}
	}
	
}
