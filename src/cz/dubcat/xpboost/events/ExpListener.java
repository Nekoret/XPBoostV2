package cz.dubcat.xpboost.events;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import cz.dubcat.xpboost.XPBoostMain;
import cz.dubcat.xpboost.api.MainAPI;
import cz.dubcat.xpboost.api.MainAPI.Condition;
import cz.dubcat.xpboost.api.XPBoostAPI;
import cz.dubcat.xpboost.constructors.Debug;
import cz.dubcat.xpboost.constructors.GlobalBoost;
import cz.dubcat.xpboost.constructors.XPBoost;

public class ExpListener implements Listener {

    private static GlobalBoost gl = XPBoostMain.GLOBAL_BOOST;
    private static final Condition CONDITION_NAME = Condition.VANILLA;

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {

        if (XPBoostMain.getPlugin().getConfig().getBoolean("settings.disablevanillaxp")) {
            return;
        }

        Player player = event.getPlayer();

        int exp = event.getAmount();
        UUID id = player.getUniqueId();
        int expnew = 0;

        if (XPBoostAPI.hasBoost(id)) {
            XPBoost xpb = XPBoostAPI.getBoost(id);
            if (xpb.hasCondition(CONDITION_NAME))
                expnew = (int) Math.round(exp * xpb.getBoost());
            else
                return;
        }

        if (gl.isEnabled()) {
            expnew += (int) Math.round(exp * gl.getGlobalBoost());
        }

        if (expnew > 0) {
            event.setAmount(expnew);

            MainAPI.debug("Player " + player.getName() + " got " + expnew + " XP instead of " + exp + " XP ("
                    + CONDITION_NAME + ")", Debug.NORMAL);

            if (XPBoostMain.getPlugin().getConfig().getBoolean("settings.doublexpmsg")) {
                String message = XPBoostMain.getLang().getString("lang.doublexpnot").replaceAll("%newexp%", expnew + "")
                        .replaceAll("%oldexp%", exp + "");

                MainAPI.sendMessage(message, player);
            }
        }
    }
}
