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

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class Mob {
  private LivingEntity ent;
  private MobType type=null;
  private int hps;
  Boolean notify = false;

    public Mob(MobType type, Location loc) {
    this.type = type;
    ent = type.spawn(loc);
    hps = type.getHps();
  }

  public LivingEntity getEnt() {
    return ent;
  }

  public MobType getType() {
    return type;
  }

  public Boolean getNotify() {
    return notify;
  }

  public void setNotify(Boolean notify) {
    this.notify = notify;
  }
  
  public void damage(EntityDamageEvent evt) {
    hps = ((Double)(hps - evt.getDamage())).intValue();
    evt.setDamage(1);
    if(hps<=0) {
      ent.setHealth(1);
    } else {
      ent.setHealth(ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    }
  }
  
  public Boolean isDead() {
    return hps<=0;
  }
  
  
}
