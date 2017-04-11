package net.demilich.metastone.game.behaviour.diplom.utils;

import net.demilich.metastone.game.Attribute;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.minions.Minion;

/**
 * @author ilya2
 *         created on 09.04.2017
 */
public class FeautureExtractor {

    public static Feature getFeatures(GameContext gameContext, Player us) {
        // Features Hero: effective health = 1
        // Features minions: health, attack, can_attack, taunt, divine shield, is present = 5
        // Total 1 + 6*7 + 1 + 6*7 = 86
        double[] features = new double[86];

        int playerId = us.getId();
        Player player = gameContext.getPlayer(playerId);
        Player opponent = gameContext.getOpponent(player);
        int i = 0;
        getPlayerFeatures(player, features, 0);
        getPlayerFeatures(opponent, features, 43);
        return new Feature(features);
    }

    private static void getPlayerFeatures(Player player, double[] features, int offset) {
        int i = offset;
        features[i] = player.getHero().getHp() + player.getHero().getArmor() / 60.0;
        i++;
        for (Minion minion : player.getMinions()) {
            features[i] = minion.getHp() / 12.0;
            i++;
            features[i] = minion.getAttack() / 12.0;
            i++;
            features[i] = minion.canAttackThisTurn() ? 1.0 : 0.0;
            i++;
            features[i] = minion.hasAttribute(Attribute.TAUNT) ? 1.0 : 0.0;
            i++;
            features[i] = minion.hasAttribute(Attribute.DIVINE_SHIELD) ? 1.0 : 0.0;
            i++;
            features[i] = 1.0;
            i++;
        }
    }

    public static void printContext(GameContext gameContext, Player us) {
        Feature feature = getFeatures(gameContext, us);
        System.out.println(feature.get(0));
        for (int i = 0; i < 7; i++) {
            System.out.println(String.format("%f %f %f %f %f %f", feature.get(i * 6 + 1), feature.get(i * 6 + 2), feature.get(i * 6 + 3), feature.get(i * 6 + 4), feature.get(i * 6 + 5), feature.get(i * 6 + 6)));
        }
        System.out.println(feature.get(43));
        for (int i = 0; i < 7; i++) {
            System.out.println(String.format("%f %f %f %f %f %f", feature.get(i * 6 + 44), feature.get(i * 6 + 45), feature.get(i * 6 + 46), feature.get(i * 6 + 47), feature.get(i * 6 + 48), feature.get(i * 6 + 49)));
        }
    }


}
