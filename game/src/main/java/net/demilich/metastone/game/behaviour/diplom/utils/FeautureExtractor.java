package net.demilich.metastone.game.behaviour.diplom.utils;

import net.demilich.metastone.game.Attribute;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.minions.Minion;

import java.util.Random;

/**
 * @author ilya2
 *         created on 09.04.2017
 */
public class FeautureExtractor {
    private static Random random = new Random();

    public static Pair<Feature, int[]> getFeatures(GameContext gameContext, Player us) {
        // Features Hero: effective health = 1
        // Features minions: health, attack, can_attack, taunt, divine shield, is present = 5
        // Total 1 + 6*7 + 1 + 6*7 = 86
        double[] features = new double[86];
        int[] offset = new int[2 * 7];

        int playerId = us.getId();
        Player player = gameContext.getPlayer(playerId);
        Player opponent = gameContext.getOpponent(player);
        for (int i = 0; i < 7; i++) {
            features[i * 6 + 6] = 1.0;
        }
        for (int i = 0; i < 7; i++) {
            features[i * 6 + 49] = 1.0;
        }
        getPlayerFeatures(player, features, 0, offset);
        getPlayerFeatures(opponent, features, 43, offset);
        return new Pair<>(new Feature(features), offset);
    }

    public static Feature getFeatures3(GameContext gameContext, Player us) {
        // Features Hero: effective health = 1
        // Features minions: health, attack, can_attack, taunt, divine shield, is present = 5
        // Total 1 + 6*7 + 1 + 6*7 = 86
        double[] features = new double[86];
        int[] offset = new int[2 * 7];

        int playerId = us.getId();
        Player player = gameContext.getPlayer(playerId);
        Player opponent = gameContext.getOpponent(player);
        for (int i = 0; i < 7; i++) {
            features[i * 6 + 6] = 1.0;
        }
        for (int i = 0; i < 7; i++) {
            features[i * 6 + 49] = 1.0;
        }
        getPlayerFeatures3(player, features, 0);
        getPlayerFeatures3(opponent, features, 43);
        return new Feature(features);
    }

    public static Feature getFeatures(GameContext gameContext, Player us, int[] offset) {
        // Features Hero: effective health = 1
        // Features minions: health, attack, can_attack, taunt, divine shield, is present = 5
        // Total 1 + 6*7 + 1 + 6*7 = 86
        double[] features = new double[86];

        int playerId = us.getId();
        Player player = gameContext.getPlayer(playerId);
        Player opponent = gameContext.getOpponent(player);
        for (int i = 0; i < 7; i++) {
            features[i * 6 + 6] = 1.0;
        }
        for (int i = 0; i < 7; i++) {
            features[i * 6 + 49] = 1.0;
        }
        getPlayerFeatures2(player, features, 0, offset);
        getPlayerFeatures2(opponent, features, 43, offset);
        return new Feature(features);
    }

    private static void getPlayerFeatures(Player player, double[] features, int offset, int[] newOffset) {
        int i = offset;
        features[i] = player.getHero().getEffectiveHp() / 60.0;
        i++;
        int emptyPool = 7 - player.getMinions().size();

        int j = 0;
        if (offset > 0) {
            j = 7;
        }

        for (Minion minion : player.getMinions()) {
            if (emptyPool > 0) {
                int skipN = random.nextInt(emptyPool + 1);
                i += skipN * 6;
                emptyPool -= skipN;
                newOffset[j] = 7 - player.getMinions().size() - emptyPool;
            }
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
            features[i] = 0.0;
            i++;
            j++;
        }
    }

    private static void getPlayerFeatures3(Player player, double[] features, int offset) {
        int i = offset;
        features[i] = player.getHero().getEffectiveHp() / 60.0;
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
            features[i] = 0.0;
            i++;
        }
    }

    private static void getPlayerFeatures2(Player player, double[] features, int offset, int[] newOffset) {
        int i = offset;
        features[i] = player.getHero().getEffectiveHp() / 60.0;
        i++;
        int j = 0;
        if (offset > 0) {
            j = 7;
        }
        int skipped = 0;
        for (Minion minion : player.getMinions()) {
            i += (newOffset[j] - skipped) * 6;
            skipped = newOffset[j];
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
            features[i] = 0.0;
            i++;
            j++;
        }
    }

    public static void printContext(GameContext gameContext, Player us) {
        Feature feature = getFeatures(gameContext, us).left;
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
