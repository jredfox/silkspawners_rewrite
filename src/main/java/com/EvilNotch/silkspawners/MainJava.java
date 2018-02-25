package com.EvilNotch.silkspawners;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.compress.compressors.pack200.Pack200Strategy;
import org.lwjgl.input.Keyboard;

import com.EvilNotch.silkspawners.client.ToolTipEvent;
import com.EvilNotch.silkspawners.client.proxy.ServerProxy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.GameData;

@Mod(modid = MainJava.MODID,name = "silkspawners", version = MainJava.VERSION,acceptableRemoteVersions = "*")
public class MainJava
{
    public static final String MODID = "silkspawners";
    public static final String VERSION = "1.2.5.2";
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
    	addGameRule(g,pos,true,type);
    	addGameRule(g,index,false,type);//is false for stacking purposes true for data
    	addGameRule(g,delay,false,type);//is false for stacking purposes true for data
    }
    public void addGameRule(GameRules g,String pos, boolean init, ValueType type) {
    	if(!g.hasRule(pos))
    		g.addGameRule(pos, "" + init, type);
    	else
    		g.addGameRule(pos, "" + g.getBoolean(pos), type);
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
    			||  toollvl < getHarvestLevel(b) )//|| player.isCreative())
    		return;
    	
    	TileEntity tile = w.getTileEntity(p);
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
    		if(isCustomSpawnerPos(nbt,"Pos") && w.getGameRules().getBoolean("CustomPosSpawner"))
    			setOffsets(nbt,x,y,z);
    		nbt.removeTag("id");
    		String white = ChatFormatting.WHITE;
    		String red = ChatFormatting.RED;
    		//if gamerule force multi index spawners to stack warning will loose initial index
    		if(multiIndexSpawner(nbt) && !w.getGameRules().getBoolean("MultiSpawnerCurrentIndex"))
    		{
    			NBTTagCompound compound = nbt.getTagList("SpawnPotentials", 10).getCompoundTagAt(0).getCompoundTag("Entity");
    			nbt.setTag("SpawnData", compound);
    		}
    		NBTTagCompound data = nbt.getCompoundTag("SpawnData");
    		String name = data.getString("id");
    		NBTTagCompound display = new NBTTagCompound();
    		String entName = TranslateEntity(name,w);
    		if(entName == null)
    			entName = "Blank";
    		String jockey = jockeyString(nbt);
    		if(jockey != null)
    			entName = MainJava.TranslateEntity(jockey, w) + " Jockey";
    		else
    			jockey = "";
    		String blockname = entName;
    		if( (entName + b.getLocalizedName() + jockey).length() < Config.maxSpawnerName)
    			blockname += " " + b.getLocalizedName();
    		display.setString("Name", white + blockname );
    		nbt.setTag("display", display);
    		nbt.setString("silkTag", name);
    		stack.setTagCompound(nbt);
    		DropBlock(w,p,stack);
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
    public static void setOffsets(NBTTagCompound nbt, int x, int y, int z)
    {
//    	System.out.println(nbt);
    	NBTTagCompound tag = nbt.getCompoundTag("SpawnData");
    	setOffset(tag,x,y,z);
//    	System.out.println(nbt);
    	NBTTagList list = nbt.getTagList("SpawnPotentials", 10);
    	for(int i=0;i<list.tagCount();i++)
    	{
    		NBTTagCompound compound = list.getCompoundTagAt(i).getCompoundTag("Entity");
    		setOffset(compound,x,y,z);
    	}
//    	System.out.println(nbt);
    }
    public static void setOffset(NBTTagCompound nbt,int x,int y,int z)
    {
    	if(!nbt.hasKey("Pos"))
    		return;
    	NBTTagList list = nbt.getTagList("Pos", 6);
    	NBTTagList offsets = new NBTTagList();
    	offsets.appendTag(new NBTTagDouble(getOffset(list.getDoubleAt(0),x,x)));
    	offsets.appendTag(new NBTTagDouble(getOffset(list.getDoubleAt(1),y,y)));
    	offsets.appendTag(new NBTTagDouble(getOffset(list.getDoubleAt(2),z,z)));
    	nbt.setTag("offsets", offsets);
    	nbt.removeTag("Pos");//makes spawners stack
    }
    public static void reAlignSpawnerPos(NBTTagCompound nbt,int x,int y,int z)
    {
    	NBTTagCompound tag = nbt.getCompoundTag("SpawnData");
    	alignPos(tag,x,y,z);
    	
    	//Does SpawnPotentials
    	NBTTagList list = nbt.getTagList("SpawnPotentials", 10);
    	for(int i=0;i<list.tagCount();i++)
    	{
    		NBTTagCompound compound = list.getCompoundTagAt(i);
    		alignPos(compound.getCompoundTag("Entity"),x,y,z);
    	}
    }
    public static double getOffset(double p, int ox, int nx)
	{
    	BigDecimal pos = new BigDecimal("" + p);
		BigDecimal oldx = new BigDecimal("" + ox);
		BigDecimal newx = new BigDecimal("" + nx);
		BigDecimal offset = oldx.subtract(pos).multiply(new BigDecimal("-1") );
		return offset.doubleValue();
	}
    /**
     * Does not require initial position since the offsets are pre-calculated now because of bigdeci
     */
    public static double recalDouble(int nx, double ofset)
	{
		BigDecimal newx = new BigDecimal("" + nx);
		BigDecimal offset = new BigDecimal("" + ofset);
		return newx.add(offset).doubleValue();
	}
    /**
     * Create pos tags from the offsets
     */
    public static void alignPos(NBTTagCompound tag, int x, int y, int z) 
    {
	    if (!tag.hasKey("offsets"))
	    	return;
	     NBTTagList list = tag.getTagList("offsets", 6);
	     NBTTagList pos = new NBTTagList();
	     int[] li = {x,y,z};
	     for (int i=0;i<3;i++)
	     {
	    	 double offset = list.getDoubleAt(i);
	    	 double new_pos = recalDouble(li[i], offset);
    		 pos.appendTag(new NBTTagDouble(new_pos));
	     }
	    tag.removeTag("offsets");//just in case tile entity has offsets array/tag doesn't effect stack since I modify nbt after I copy it
	    tag.setTag("Pos", pos);
	}
    
	public static boolean isCustomSpawnerPos(NBTTagCompound nbt,String pos)
	{
		if (nbt == null || !(nbt.hasKey("SpawnData") ) && !(nbt.hasKey("SpawnPotentials")) )
			return false;
		if (nbt.getTag("SpawnData") != null)
		{
			NBTTagCompound tag = (NBTTagCompound)nbt.getTag("SpawnData");
			if (tag.hasKey(pos))
				return true;
		}
		if (nbt.getTag("SpawnPotentials") != null)
		{
			NBTTagList list = nbt.getTagList("SpawnPotentials",10);
			if (list.tagCount() > 0)
			{
				for (int i=0;i<list.tagCount();i++)
				{
					NBTTagCompound tag = list.getCompoundTagAt(i);
					NBTTagCompound ent = tag.getCompoundTag("Entity");
					if (ent.hasKey(pos))
						return true;
				}
			}
		}
		return false;
	}
    public static int getHarvestLevel(Block b)
	{
		int lvl = -1;
		for(int i=0;i<16;i++)
		{
			int harvest = b.getHarvestLevel(b.getDefaultState());
			if(harvest > lvl)
				lvl = harvest;
		}
		return lvl;
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
	   if(isCustomSpawnerPos(nbt,"offsets"))
		   reAlignSpawnerPos(nbt, p.getX(), p.getY(), p.getZ() );
	   nbt.setInteger("x", p.getX());
	   nbt.setInteger("y", p.getY());
	   nbt.setInteger("z", p.getZ());
	   tile.readFromNBT(nbt);
	   tile.markDirty();
	   w.notifyBlockUpdate(p, state, w.getBlockState(p), 3);//fixes issues
	}
	public static void printChat(EntityPlayer player,String c_player, String c_msg, String messege)
	{
		player.sendMessage(new TextComponentString(c_player + player.getName() + " " + c_msg + messege));
	}
	public static String getStringId(Item item)
	{
		return ForgeRegistries.ITEMS.getKey(item).toString();
	}
	public void writeToClipboard(String s, ClipboardOwner owner) 
	{
		if(s == null)
			s = "null";
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    Transferable transferable = new StringSelection(s);
	    clipboard.setContents(transferable, owner);
	}

    public static void DropBlock(World world, BlockPos p, ItemStack stack)
    {
    	 if (!world.isRemote && world.getGameRules().hasRule("doTileDrops") && !world.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
         {
            float f = 0.7F;
            double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            int x = p.getX();
            int y = p.getY();
            int z = p.getZ();
            EntityItem entityitem = new EntityItem(world, (double)x + d0, (double)y + d1, (double)z + d2, stack);
            entityitem.setPickupDelay(10);
            world.spawnEntity(entityitem);
        }
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
    
    /**
	 * Translates non living and living entities along with a trying method to always get the proper translation...
	 * It returns null if it can't find a translation This method is crashproof and null proof 
	 */
	public static String TranslateEntity(String s,World world)
	{
	   if (s == null || s.equals("blank") || s.equals("") || s.equals("\"\"") || s.equals("minecraft:blank") || s.equals("null") || s.equals("minecraft:null"))
		   return null;
		
	   String EntityName = null;
	   try{
		   EntityName = I18n.translateToLocal("entity." + s + ".name");
	   if(EntityName == null)
		   EntityName = s;
		//Corrects if there is no local translation back to default namming...
	    if (EntityName.equals("entity." + s + ".name"))
	     	EntityName = s;
	    //1.12.2 update entity can be translated yet still freak out
	    if(EntityName.startsWith("entity.") && EntityName.endsWith(".name"))
	    	EntityName = EntityName.substring(7, EntityName.length()-5);
	   }catch(Throwable t){return null;}
	   
	  
	    //Experimental Code_______________________
	    if(s.equals(EntityName))
	    { 
	    	if (createEntityByNameQuietly(EntityName, world) != null)
	    	{
	    		 Entity entity = createEntityByNameQuietly(EntityName, world);
	    		 String commandsender = getcommandSenderName(entity);
	    		 //mc 1.12 can still translate yet freak out
	    		 if(commandsender.startsWith("entity.") && commandsender.endsWith(".name"))
	    			 commandsender = commandsender.substring(7, commandsender.length()-5);
	    		 
	    		 if(commandsender == null)
	    			 return EntityName;//If entity fails do this
	    		if(!commandsender.equals("generic"))
	    		{
	    			if(!commandsender.equals(EntityName) && !commandsender.equals("entity." + EntityName + ".name") && !commandsender.equals("entity." + s + ".name")  )
	    				EntityName = commandsender;
	    		}
	    	}
	    }
	    //End Experimental Code___________________
	    
	    return EntityName;
	}
	
	public static String getcommandSenderName(Entity entity) 
	{
		try{
			if(entity == null)
				return null;
			return entity.getName();
		}catch(Throwable t){
			t.printStackTrace();
		}
		return null;
	}
    @Nullable
    public static Entity createEntityByNameQuietly(String name, World worldIn)
    {
    	try{
    	ResourceLocation loc = new ResourceLocation(name);
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.getValue(loc);
        return entry == null ? null : entry.newInstance(worldIn);
    	}catch(Exception e){}
    	return null;
    }
    /**
     * Checks for a soft coded non laggy way of detecting if a dropped spawner has custom pos
     */
    public static boolean isStackCustomPos(NBTTagCompound nbt) {
		return nbt.getCompoundTag("SpawnData").hasKey("offsets");
	}

	public static String jockeyString(NBTTagCompound nbt) {
		nbt = nbt.copy();
		NBTTagCompound data = nbt.getCompoundTag("SpawnData");
		if(!data.hasKey("Passengers"))
			return null;
		NBTTagList list = data.getTagList("Passengers", 10);
		NBTTagCompound entity = list.getCompoundTagAt(list.tagCount()-1);
		return entity.getString("id");
	}

	public static boolean multiIndexSpawner(NBTTagCompound nbt) {
		nbt = nbt.copy();
		if(nbt == null || !nbt.hasKey("SpawnPotentials") || nbt.getTagList("SpawnPotentials", 10).tagCount() == 0)
			return false;
		if(nbt.getTagList("SpawnPotentials", 10).tagCount() == 1)
		{
			NBTTagCompound data = nbt.getCompoundTag("SpawnData");
			NBTTagCompound compare = nbt.getTagList("SpawnPotentials", 10).getCompoundTagAt(0);
			return !data.equals(compare.getCompoundTag("Entity"));
		}
		return true;
	}
}
