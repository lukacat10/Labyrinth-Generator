// Derived from GarbageMule's MobAreana MACreature class

package net.steeleyes.catacombs;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;

public enum MobShape {
    // Standard creatures
    ZOMBIE(EntityType.ZOMBIE),
    HUSK(EntityType.HUSK),
    SKELETON(EntityType.SKELETON),
    STRAY(EntityType.STRAY),
    CREEPER(EntityType.CREEPER),
    PIG_ZOMBIE(EntityType.PIG_ZOMBIE),
    SPIDER(EntityType.SPIDER),
    CAVE_SPIDER(EntityType.CAVE_SPIDER),
    BLAZE(EntityType.BLAZE),
    WOLF(EntityType.WOLF),
    SILVERFISH(EntityType.SILVERFISH),
    ENDERMAN(EntityType.ENDERMAN),
    GHAST(EntityType.GHAST),
    GIANT(EntityType.GIANT),
    SLIME1(EntityType.SLIME),
    SLIME2(EntityType.SLIME),
    SLIME3(EntityType.SLIME),
    SLIME4(EntityType.SLIME),
    CHICKEN(EntityType.CHICKEN),
    COW(EntityType.COW),
    SQUID(EntityType.SQUID),
    SHEEP(EntityType.SHEEP),
    PIG(EntityType.PIG),

    // Special creatures
    POWEREDCREEPER(EntityType.CREEPER);

    private EntityType type;

    MobShape(EntityType type) {
        this.type = type;
    }

    public EntityType getType() {
        return type;
    }


    public LivingEntity spawn(World world, Location loc) {
        //Removed in 1.9
        //LivingEntity e = world.spawnCreature(loc, type);
        LivingEntity e = (LivingEntity)world.spawnEntity(loc, type);
        return spawn(e);
    }

    public LivingEntity spawn(LivingEntity e) {
        switch (this) {
            case WOLF:
                ((Wolf) e).setAngry(true);
                break;
            case PIG_ZOMBIE:
                ((PigZombie) e).setAngry(true);
                break;
            case POWEREDCREEPER:
                ((Creeper) e).setPowered(true);
                break;
            case SLIME1:
                ((Slime) e).setSize(1);
                break;
            case SLIME2:
                ((Slime) e).setSize(2);
                break;
            case SLIME3:
                ((Slime) e).setSize(3);
                break;
            case SLIME4:
                ((Slime) e).setSize(4);
                break;
            default:
                break;
        }
        return e;
    }

    public static MobShape getType(EntityType c) {
        return getType(c.toString());
    }

    public static MobShape getType(String string) {
        return CatUtils.getEnumFromString(MobShape.class, string.replaceAll("[-\\.]", ""));
    }


}

