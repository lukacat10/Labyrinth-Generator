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

package net.steeleyes.data;

import net.steeleyes.catacombs.CatMat;
import net.steeleyes.data.IConfig;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

import java.util.List;

public interface ICatConfig extends IConfig {

    Boolean debugMode();

    Integer RadiusMax();

    Integer floorDepth();

    Integer roomDepth();

    Integer roofDepth();

    Integer extraDepth();

    String HutType();

    Boolean UnderFill();

    Boolean OverFill();

    Boolean ResetButton();

    Boolean emptyChest();

    Boolean ProtectSpawners();

    Boolean SecretDoorOnlyInDungeon();

    Boolean GoldOff();

    Boolean RespawnInHut();

    Boolean NoPvPInDungeon();

    Boolean NoArmourInDungeon();

    Boolean MobsSpawnOnlyUnderground();

    Boolean MobsSpawnOnlyInDungeons();

    Boolean AdvancedCombat();

    Integer GroupRadius();

    Integer GroupDepth();

    Double GroupHpFactor();

    Integer SpawnRadius();

    Integer SpawnDepth();

    Integer MonsterRadius();

    Integer MonsterMax();

    Integer DeathGearCost();

    Boolean DeathKeepGear();

    Double DeathExpKept();

    Boolean ClickIronDoor();

    // Boolean BossEnabled();

    String Economy();

    List<String> BannedCommands();

    EntityType SpawnerType();

    Double Gold();

    Material ShroomType();

    Material AirType();

    Material DoorMaterial();

    Boolean NoTeleportIn();

    Boolean NoTeleportOut();

    Boolean isNatural(Block b);

    Boolean isBreakable(Block b);

    Boolean isPlaceable(Block b);

    List<String> TrapList();

    List<String> LootSmallList();

    List<String> LootMediumList();

    List<String> LootBigList();

    Boolean SmallEquipChance();

    Boolean MobDropReductionChance();

    Boolean MedEquipChance();

    Boolean MedSmallChance();

    Boolean MedSweepOre();

    Boolean BigEquipChance();

    Boolean BigSmallChance();

    Boolean MinorChance();

    CatMat majorMat();

    CatMat minorMat();

    CatMat floorMat();

    CatMat roofMat();

    void setRadiusMax(int r);
}
