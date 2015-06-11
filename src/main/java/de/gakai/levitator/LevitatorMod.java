package de.gakai.levitator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import de.gakai.levitator.gui.GuiHandler;

@Mod(modid = LevitatorMod.MODID, version = LevitatorMod.VERSION)
public class LevitatorMod
{

    // FIXME: Shift-click on a feather while a redstone feather is in the slot, increases the redstone feather stack
    // XXX: Shift-right clicking with a feather adds it into the container, if it fits

    public static final String MODID = "GakaisLevitator";
    public static final String VERSION = "0.1";
    public static final String ASSETS = "levitator";

    private static final String CONF_CAT = "Levitator";
    private static final String SHAPES_HELP = "Available shapes: " + StringUtils.join(Shape.values(), ", ");

    @Instance(MODID)
    public static LevitatorMod instance;

    public static final Block levitator = new BlockLevitator();

    public static final Item redstoneFeather = new Item() //
            .setFull3D() //
            .setUnlocalizedName("redstoneFeather") //
            .setCreativeTab(CreativeTabs.tabTransport) //
            .setTextureName(ASSETS + ":redstoneFeather");

    public static final Item creativeFeather = new Item() {
        @Override
        public boolean hasEffect(ItemStack par1ItemStack, int pass)
        {
            return true;
        };
    }.setFull3D() //
            .setUnlocalizedName("creativeFeather") //
            .setCreativeTab(CreativeTabs.tabTransport) //
            .setTextureName(ASSETS + ":creativeFeather") //
            .setMaxStackSize(1);

    private static final Map<Item, Integer> fuels = new HashMap<Item, Integer>();

    private static final Item upgradeItem = Item.getItemFromBlock(Blocks.glowstone);

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        FMLCommonHandler.instance().bus().register(this);

        Configuration config = new Configuration(new File("config/Levitator.cfg"), true);
        TileEntityLevitator.MAX_POWER = config.get(CONF_CAT, "MaxPower", TileEntityLevitator.MAX_POWER).getInt();
        TileEntityLevitator.POWER_PER_PLAYER = config.get(CONF_CAT, "PowerPerPlayer", TileEntityLevitator.POWER_PER_PLAYER).getInt();
        TileEntityLevitator.POWER_PER_TICK = config.get(CONF_CAT, "PowerPerTick", TileEntityLevitator.POWER_PER_TICK).getInt();
        TileEntityLevitator.POWER_PER_UPGRADE = config.get(CONF_CAT, "PowerPerUpgrade", TileEntityLevitator.POWER_PER_UPGRADE).getDouble();
        TileEntityLevitator.RANGE_BASE = config.get(CONF_CAT, "BaseRange", TileEntityLevitator.RANGE_BASE).getInt();
        TileEntityLevitator.RANGE_PER_UPGRADE = config.get(CONF_CAT, "RangePerUpgrade", TileEntityLevitator.RANGE_PER_UPGRADE).getDouble();
        TileEntityLevitator.shape = Shape.valueOf(config.get(CONF_CAT, "shape", TileEntityLevitator.shape.toString(), SHAPES_HELP).getString().toUpperCase());
        config.save();

        GameRegistry.registerItem(redstoneFeather, "redstoneFeather");
        GameRegistry.registerItem(creativeFeather, "creativeFeather");
        GameRegistry.registerBlock(levitator, "levitator");
        GameRegistry.registerTileEntity(TileEntityLevitator.class, LevitatorMod.MODID);

        GameRegistry.addShapedRecipe(new ItemStack(levitator), //
                "dod", //
                "ogo", //
                "dod", //
                'd', Items.diamond, //
                'o', Blocks.obsidian, //
                'g', Blocks.redstone_lamp);

        GameRegistry.addShapedRecipe(new ItemStack(redstoneFeather), //
                " r ", //
                "rfr", //
                " r ", //
                'f', Items.feather, //
                'r', Items.redstone);

        fuels.put(Items.feather, 12000);
        fuels.put(redstoneFeather, 12000 * 4);
        fuels.put(creativeFeather, 1200);
    }

    public static boolean isItemFuel(ItemStack item)
    {
        return LevitatorMod.fuels.containsKey(item.getItem());
    }

    public static Integer getFuelValue(ItemStack fuelStack)
    {
        return fuelStack == null ? null : fuels.get(fuelStack.getItem());
    }

    public static boolean isItemUpgrade(ItemStack item)
    {
        return item.getItem() == LevitatorMod.upgradeItem;
    }

}
