/*  This file is part of Catacombs.

    Catacombs is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Catacombs is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Catacombs.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author John Keay  <>(@Steeleyes, @Blockhead2)
 * @copyright Copyright (C) 2011
 * @license GNU GPL <http://www.gnu.org/licenses/>
*/

package net.steeleyes.catacombs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CatLoot {
    public static void fillChest(Inventory inv, List<String> list) {
        for (ItemStack i : fillChest(new ArrayList<ItemStack>(), list)) inv.addItem(i);
    }

    public static List<ItemStack> fillChest(List<ItemStack> inv, List<String> list) {
        if (inv == null) inv = new ArrayList<>();

        for (String s : list) {
            ItemStack i = fromString(s);
            if (i != null) inv.add(i);
        }

        return inv;
    }

    public static ItemStack fromString(String l) {
        try {
            return fs(l);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Passes in a Catacombs config Item string (Currently formatted as itemname:dmgvalue:probability amount#-#[ enchant:lvl])
    //Originally formatted as itemname[/dmgvalue]:probability:amount#-#[:enchant/lvl]
    public static ItemStack fs(String l) {
        //Split into array of supposed to be 2 or more strings (itemname:dmgvalue:probability and amount#-# (name:&color_code_support lore:underscores_for_spaces enchantment:lvl )
        String[] sa = l.split(" ");

        //If old style config format (no spaces), upconvert
        if(sa.length==1){
            String[] oldItemLine = sa[0].split(":");
            StringBuilder newLine=new StringBuilder();
            //Parse old style damage value via "/"
            if(oldItemLine[0].contains("/")){
                String[] itemDmg = oldItemLine[0].split("/");
                if(itemDmg.length==2) {
                    newLine.append(itemDmg[0]+":"+itemDmg[1]);
                } else {
                    Catacombs.log("Failed to parse loot entry "+oldItemLine.toString());
                }
            } else {
                newLine.append(oldItemLine[0]+":0");
            }
            //Append probability
            newLine.append(":"+oldItemLine[1]);
            //Append space for new format
            newLine.append(" ");
            //Append amount after space
            newLine.append(oldItemLine[2]);
            //If old style enchants
            if(oldItemLine.length>3){
                //i = 3 is 4th item in array
                for(int i=3; i<oldItemLine.length; i++){
                    if(oldItemLine[i].contains("/")){
                        newLine.append(" "+oldItemLine[i].replace("/",":"));
                    } else {
                        newLine.append(" "+oldItemLine[i]);
                    }
                }
            }
            Catacombs.debug("Upconverting old style config entry: "+newLine.toString());
            //Presumably I have a properly updated formatted item string now and can resplit it
            sa=newLine.toString().split(" ");
        }
        //Load type itemname:dmgvalue:probability
        String t = sa[0];
        //Patch to make item damage value optional again
        if(t.split(":").length==2){
            String[] tShort = t.split(":");
            StringBuffer tDefault = new StringBuffer(tShort[0]).append(":0:").append(tShort[1]);
            t=tDefault.toString();
        }
        //Load amount
        int amount = 0;
        try {
            //Split amount bounds into array, should be length 2 or 1 for no bounds
            String[] amountArray = sa[1].replace(" ", "").split("-");
            //If no bounds parse int from single array entry
            if (amountArray.length == 1) amount = Integer.parseInt(amountArray[0]);
            else {
                int max = Integer.parseInt(amountArray[1]);
                int min = Integer.parseInt(amountArray[0]);
                //Different math in random calculator
                amount = new Random().nextInt(max - min) + min;
            }
        } catch (Exception ignored) {
        }
        if (amount < 1) amount = 1;

        //Get type
        Material m;
        Integer c;
        short dur = -1;
        //Split itemname:dmgvalue:probability
        String[] td = t.split(":");
        //if you leave off say for instance damage value :/
        if (td.length <= 2) {
            System.out.println("[Warning] Invalid item-length whilst loading lootList! (" + sa.length + "<3)");
            return null;
        }

        //Type
        try {
            //TODO: This will break in 1.13!
            //Attempt to process as an integer in the depreciated materialbyitemid method
            m = Material.getMaterial(Integer.parseInt(td[0]));
        } catch (Exception ignored) {
            //If fails process by string name
            m = Material.matchMaterial(td[0]);
        }

        //Durability which is required now..
        try {
            dur = Short.parseShort(td[1]);
        } catch (Exception ignored) {
        }
        if (dur < 0) dur = 0;

        //Probability of loot
        try {
            c = Integer.parseInt(td[2]);
        } catch (Exception ignored) {
            System.out.println("[Warning] Invalid chance whilst loading lootList! (" + td[2] + ")");
            return null;
        }

        if (m == null) {
            System.out.println("[Warning] Invalid material-type whilst loading lootList! (" + td[0] + ")");
            return null;
            //uh random chance of item not being placed in chest based on probability
        } else if (new Random().nextInt(100) >= c) return null;

        ItemStack i = new ItemStack(m, amount, dur);

        //Load and apply params
        //If ' ' split array has more than itemname:dmgvalue:probability and amount#-#
        if (sa.length > 2) {
            ItemMeta im = i.getItemMeta();
            //Start parsing tags after item amount
            for (int n = 2; n < sa.length; n++) {
                //Split tags by :
                String[] pa = sa[n].split(":");
                //If more than a tag and a value, skip it
                if (pa.length < 2) continue;
                //if tag is "name" set the display name of the item. Underscores are converted to spaces
                if (pa[0].equalsIgnoreCase("name")) {
                    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', pa[1].replaceAll("_", " ")));
                //if tag is "lore" set the display name of the item. Underscores are converted to spaces
                } else if (pa[0].equalsIgnoreCase("lore")) {
                    List<String> lore = im.getLore();
                    if (lore == null) lore = new ArrayList<>();
                    for (String s : pa[1].split("[|]"))
                        if (s != null) lore.add(ChatColor.translateAlternateColorCodes('&', s.replaceAll("_", " ")));
                    im.setLore(lore);
                } else {
                    Enchantment e;
                    try {
                        //TODO: This will break in 1.13!
                        e = Enchantment.getById(Integer.parseInt(pa[0]));
                    } catch (Exception ignored) {
                        e = Enchantment.getByName(pa[0]);
                    }
                    if (e != null) {
                        try {
                            Integer lvl = Integer.parseInt(pa[1]);
                            im.addEnchant(e, lvl, true);
                        } catch (NumberFormatException ignored) {
                        }
                    } else for (PotionType ptv : PotionType.values())
                        if (ptv.name().equalsIgnoreCase(pa[0])) try {
                            Integer lvl = Integer.parseInt(pa[1]);
                            if (lvl > 2) lvl = 2;
                            if (lvl < 1) lvl = 1;
                            new Potion(ptv, lvl).apply(i);
                            break;
                        } catch (Exception ignored) {
                        }
                }
            }
            i.setItemMeta(im);
        }
        return i;
    }

    public static void smallChest(CatConfig cnf, Inventory inv) {
        if (cnf.SmallEquipChance()) inv.addItem(leather_equipment(cnf));
        fillChest(inv, cnf.LootSmallList());
    }

    public static List<ItemStack> smallChest(CatConfig cnf, List<ItemStack> inv) {
        if (cnf.SmallEquipChance()) inv.add(leather_equipment(cnf));
        return fillChest(inv, cnf.LootBigList());
    }

    public static void midChest(CatConfig cnf, Inventory inv) {
        if (cnf.MedEquipChance()) if (cnf.Chance(90)) {
            inv.addItem(new ItemStack(Material.IRON_INGOT, cnf.nextInt(10) + 1));
            inv.addItem(iron_equipment(cnf));
        } else {
            inv.addItem(new ItemStack(Material.GOLD_INGOT, cnf.nextInt(10) + 1));
            inv.addItem(gold_equipment(cnf));
            inv.addItem(new ItemStack(Material.GOLDEN_APPLE, 1));
        }
        fillChest(inv, cnf.LootMediumList());
        if (cnf.MedEquipChance()) smallChest(cnf, inv);
    }

    public static List<ItemStack> midChest(CatConfig cnf, List<ItemStack> inv) {
        if (cnf.MedEquipChance()) if (cnf.Chance(90)) {
            inv.add(new ItemStack(Material.IRON_INGOT, cnf.nextInt(10) + 1));
            inv.add(iron_equipment(cnf));
        } else {
            inv.add(new ItemStack(Material.GOLD_INGOT, cnf.nextInt(10) + 1));
            inv.add(gold_equipment(cnf));
            inv.add(new ItemStack(Material.GOLDEN_APPLE, 1));
        }
        inv = fillChest(inv, cnf.LootMediumList());
        if (cnf.MedSmallChance()) inv = smallChest(cnf, inv);
        return inv;
    }

    public static void bigChest(CatConfig cnf, Inventory inv) {
        if (cnf.BigEquipChance()) inv.addItem(diamond_equipment(cnf));
        fillChest(inv, cnf.LootBigList());
        if (cnf.BigSmallChance()) smallChest(cnf, inv);
    }

    public static List<ItemStack> bigChest(CatConfig cnf, List<ItemStack> inv) {
        if (cnf.BigEquipChance()) inv.add(diamond_equipment(cnf));
        inv = fillChest(inv, cnf.LootBigList());
        if (cnf.BigSmallChance()) inv = smallChest(cnf, inv);
        return inv;
    }

    private static ItemStack leather_equipment(CatConfig cnf) {
        switch (cnf.nextInt(6) + 1) {
            case 1:
                return new ItemStack(Material.LEATHER_HELMET, 1);
            case 2:
                return new ItemStack(Material.LEATHER_CHESTPLATE, 1);
            case 3:
                return new ItemStack(Material.LEATHER_LEGGINGS, 1);
            case 4:
                return new ItemStack(Material.LEATHER_BOOTS, 1);
        }
        return new ItemStack(Material.AIR, 1);
    }

    private static ItemStack iron_equipment(CatConfig cnf) {
        switch (cnf.nextInt(12) + 1) {
            case 1:
                return new ItemStack(Material.IRON_HELMET, 1);
            case 2:
                return new ItemStack(Material.IRON_CHESTPLATE, 1);
            case 3:
                return new ItemStack(Material.IRON_LEGGINGS, 1);
            case 4:
                return new ItemStack(Material.IRON_BOOTS, 1);
            case 5:
                return new ItemStack(Material.IRON_PICKAXE, 1);
            case 6:
                return new ItemStack(Material.IRON_SPADE, 1);
            case 7:
                return new ItemStack(Material.IRON_AXE, 1);
            case 8:
                return new ItemStack(Material.IRON_SWORD, 1);
            case 9:
                return new ItemStack(Material.IRON_HOE, 1);
        }
        return new ItemStack(Material.AIR, 1);
    }

    private static ItemStack gold_equipment(CatConfig cnf) {
        switch (cnf.nextInt(12) + 1) {
            case 1:
                return new ItemStack(Material.GOLD_HELMET, 1);
            case 2:
                return new ItemStack(Material.GOLD_CHESTPLATE, 1);
            case 3:
                return new ItemStack(Material.GOLD_LEGGINGS, 1);
            case 4:
                return new ItemStack(Material.GOLD_BOOTS, 1);
            case 5:
                return new ItemStack(Material.GOLD_PICKAXE, 1);
            case 6:
                return new ItemStack(Material.GOLD_SPADE, 1);
            case 7:
                return new ItemStack(Material.GOLD_AXE, 1);
            case 8:
                return new ItemStack(Material.GOLD_SWORD, 1);
            case 9:
                return new ItemStack(Material.GOLD_HOE, 1);
        }
        return new ItemStack(Material.AIR, 1);
    }

    private static ItemStack diamond_equipment(CatConfig cnf) {
        switch (cnf.nextInt(12) + 1) {
            case 1:
                return new ItemStack(Material.DIAMOND_HELMET, 1);
            case 2:
                return new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
            case 3:
                return new ItemStack(Material.DIAMOND_LEGGINGS, 1);
            case 4:
                return new ItemStack(Material.DIAMOND_BOOTS, 1);
            case 5:
                return new ItemStack(Material.DIAMOND_PICKAXE, 1);
            case 6:
                return new ItemStack(Material.DIAMOND_SPADE, 1);
            case 7:
                return new ItemStack(Material.DIAMOND_AXE, 1);
            case 8:
                return new ItemStack(Material.DIAMOND_SWORD, 1);
            case 9:
                return new ItemStack(Material.DIAMOND_HOE, 1);
        }
        return new ItemStack(Material.AIR, 1);
    }
}
