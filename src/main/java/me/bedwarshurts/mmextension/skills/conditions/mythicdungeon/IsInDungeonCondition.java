/* package me.bedwarshurts.mmextension.skills.conditions.mythicdungeon;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import net.playavalon.mythicdungeons.MythicDungeons;
import net.playavalon.mythicdungeons.api.parents.instances.InstancePlayable;
import net.playavalon.mythicdungeons.player.MythicPlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

@MythicCondition(author = "bedwarshurts", name = "isindungeon", aliases = {}, description = "Check if the entity is in a dungeon")
public class IsInDungeonCondition implements IEntityCondition {

    @Override
    public boolean check(AbstractEntity target) {
        World world = BukkitAdapter.adapt(target.getWorld());
        if (world.getPlayers().isEmpty()) return false;

        Player player = world.getPlayers().getFirst();
        MythicPlayer mythicPlayer = MythicDungeons.inst().getMythicPlayer(player);
        InstancePlayable inst = mythicPlayer.getInstance().asPlayInstance();

        return inst != null;
    }
}
*/
