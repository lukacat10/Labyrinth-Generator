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

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class BlockChangeHandler implements Runnable {
    private int changed = 0;

    private Catacombs plugin;

    private final List<BlockChange> delay = new ArrayList<>();
    private final List<BlockChange> high = new ArrayList<>();
    private final List<BlockChange> low = new ArrayList<>();
    private final List<Player> who = new ArrayList<>();

    public BlockChangeHandler(Catacombs plugin) {
        this.plugin = plugin;
    }

    private void setBlock(BlockChange x) {
        Block blk = x.getBlk();
        try {
            blk.setType(x.getMat());
            if (x.getCode() > 0) blk.setData(x.getCode(), true);
        } catch (Exception e) {
            System.err.println("[Catacombs] Problem setting block " + blk + " " + x.getMat() + " " + x.getCode());
        }

        if (x.getItems() != null && blk.getState() instanceof InventoryHolder) {
            InventoryHolder cont = (InventoryHolder) blk.getState();
            Inventory inv = cont.getInventory();
            for (ItemStack s : x.getItems()) inv.addItem(s);
            if (x.getMat() == Material.DISPENSER) delay.add(new BlockChange(blk, null, x.getCode()));
        }

        if (x.getSpawner() != null && blk.getState() instanceof CreatureSpawner) {
            CreatureSpawner spawner = (CreatureSpawner) blk.getState();
            spawner.setSpawnedType(x.getSpawner());
        }

        if (x.hasLines() && blk.getState() instanceof Sign) {
            Sign sign = (Sign) blk.getState();
            for (int i = 0; i < 4; i++) {
                String str = x.getLine(i);
                if (str != null) sign.setLine(i, str);
            }
            sign.update(true);
        }
    }

    @Override
    public void run() {
        int maxChanges = plugin.getCnf().getMaxChangesPerSecond();
        while (!delay.isEmpty()) {
            BlockChange x = delay.remove(0);
            x.getBlk().setData(x.getCode());
        }

        int cnt = 0;
        while (!high.isEmpty() && cnt < maxChanges) {
            BlockChange x = high.remove(0);
            setBlock(x);
            cnt++;
        }
        if (cnt == 0)  // Only do low priority on a new tick
            while (!low.isEmpty() && cnt < maxChanges) {
                BlockChange x = low.remove(0);
                setBlock(x);
                cnt++;
            }
        if (cnt > 0) changed += cnt;
        if (cnt == 0 && changed > 0) {
            System.out.println("[Catacombs] Block Handler #changes=" + changed);
            for (Player p : who) if (plugin != null) plugin.inform(p, "Catacomb changes complete");
            who.clear();
            changed = 0;
        }
    }


    // figure out a good way to avoid the long list of combinations here
    public void add(BlockChange b, Position pos) {
        if (pos == Position.LOW) low.add(b);
        else high.add(b);
    }

    public void add(Block blk, Material mat, Position pos) {
        add(new BlockChange(blk, mat), pos);
    }

    public void add(Block blk, Material mat, byte code, Position pos) {
        add(new BlockChange(blk, mat, code), pos);
    }


    public void add(Block blk, Material mat, List<ItemStack> items, Position pos) {
        add(new BlockChange(blk, mat, items), pos);
    }


    public void add(Block blk, Material mat, byte code, List<ItemStack> items, Position pos) {
        BlockChange ch = new BlockChange(blk, mat, code);
        ch.setItems(items);
        add(ch, pos);
    }


    public void add(World world, int x, int y, int z, Material mat, Position pos) {
        add(new BlockChange(world.getBlockAt(x, y, z), mat), pos);
    }

    public void add(World world, int x, int y, int z, Material mat, byte code, Position pos) {
        add(new BlockChange(world.getBlockAt(x, y, z), mat, code), pos);
    }

    public void add(World world, int x, int y, int z, Material mat, List<ItemStack> items, Position pos) {
        add(new BlockChange(world.getBlockAt(x, y, z), mat, items), pos);
    }


    public void add(World world, int x, int y, int z, Material mat, byte code, List<ItemStack> items,Position pos) {
        BlockChange ch = new BlockChange(world.getBlockAt(x, y, z), mat, code);
        ch.setItems(items);
        add(ch, pos);
    }

    public void add(Player player) {
        who.add(player);
    }
}
