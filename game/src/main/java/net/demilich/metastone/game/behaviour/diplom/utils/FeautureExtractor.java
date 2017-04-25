package net.demilich.metastone.game.behaviour.diplom.utils;

import net.demilich.metastone.game.Attribute;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.minions.Minion;

import java.util.HashMap;
import java.util.Random;

import static net.demilich.metastone.game.behaviour.diplom.Consts.FEATURE_SIZE;

/**
 * @author ilya2
 *         created on 09.04.2017
 */
public class FeautureExtractor {
    private static Random random = new Random();

    public static Pair<Feature, HashMap<Integer, Integer>> getFeatures(GameContext gameContext, Player us) {
        // Features Hero: effective health = 1
        // Features minions: health, attack, can_attack, taunt, divine shield, is present = 5
        // Total 1 + 6*7 + 1 + 6*7 = FEATURE_SIZE
        double[] features = new double[FEATURE_SIZE];
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
        HashMap<Integer, Integer> position = new HashMap<>();
        getPlayerFeatures(player, features, 0, position);
        getPlayerFeatures(opponent, features, 43, position);

        features[86] = ((double) gameContext.getTotalDamageCount(player)) / 12.0 * 7;
        features[87] = ((double) gameContext.getTotalDamageCount(gameContext.getOpponent(player))) / 12.0 * 7;
        features[88] = ((double) gameContext.getTurn()) / 101.0;
        features[89] = ((double) gameContext.getMinionCount(player)) / 7.0;
        features[90] = ((double) gameContext.getMinionCount(gameContext.getOpponent(player))) / 7.0;

        return new Pair<>(new Feature(features), position);
    }

    public static Feature getFeatures3(GameContext gameContext, Player us) {
        // Features Hero: effective health = 1
        // Features minions: health, attack, can_attack, taunt, divine shield, is present = 5
        // Total 1 + 6*7 + 1 + 6*7 = FEATURE_SIZE
        double[] features = new double[FEATURE_SIZE];
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

        features[86] = ((double) gameContext.getTotalDamageCount(player)) / 12.0 * 7;
        features[87] = ((double) gameContext.getTotalDamageCount(gameContext.getOpponent(player))) / 12.0 * 7;
        features[88] = ((double) gameContext.getTurn()) / 101.0;
        features[89] = ((double) gameContext.getMinionCount(player)) / 7.0;
        features[90] = ((double) gameContext.getMinionCount(gameContext.getOpponent(player))) / 7.0;

        return new Feature(features);
    }

    public static Feature getFeatures(GameContext gameContext, Player us, HashMap<Integer, Integer> position) {
        // Features Hero: effective health = 1
        // Features minions: health, attack, can_attack, taunt, divine shield, is present = 5
        // Total 1 + 6*7 + 1 + 6*7 = 86
        double[] features = new double[FEATURE_SIZE];

        int playerId = us.getId();
        Player player = gameContext.getPlayer(playerId);
        Player opponent = gameContext.getOpponent(player);
        for (int i = 0; i < 7; i++) {
            features[i * 6 + 6] = 1.0;
        }
        for (int i = 0; i < 7; i++) {
            features[i * 6 + 49] = 1.0;
        }
        getPlayerFeatures2(player, features, 0, position);
        getPlayerFeatures2(opponent, features, 43, position);

        features[86] = ((double) gameContext.getTotalDamageCount(player)) / 12.0 * 7;
        features[87] = ((double) gameContext.getTotalDamageCount(gameContext.getOpponent(player))) / 12.0 * 7;
        features[88] = ((double) gameContext.getTurn()) / 101.0;
        features[89] = ((double) gameContext.getMinionCount(player)) / 7.0;
        features[90] = ((double) gameContext.getMinionCount(gameContext.getOpponent(player))) / 7.0;

        return new Feature(features);
    }

    private static void getPlayerFeatures(Player player, double[] features, int offset, HashMap<Integer, Integer> position) {
        int i = offset;
        features[i] = player.getHero().getEffectiveHp() / 60.0;
        i++;
        int emptyPool = 7 - player.getMinions().size();

        for (Minion minion : player.getMinions()) {
            //if (emptyPool > 0) {
                int skipN = random.nextInt(emptyPool + 1);
                i += skipN * 6;
                emptyPool -= skipN;
            position.put(minion.getId(), i);
            //}
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

    private static void getPlayerFeatures2(Player player, double[] features, int offset, HashMap<Integer, Integer> position) {
        int i = offset;
        features[i] = player.getHero().getEffectiveHp() / 60.0;
        i++;
        for (Minion minion : player.getMinions()) {
            if (position.containsKey(minion.getId())) {
                i = position.get(minion.getId());
            } else {
                //TODO fix it later when deathrattle will come
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
        }
    }
}
