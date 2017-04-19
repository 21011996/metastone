package net.demilich.metastone.game.behaviour.diplom;

/**
 * @author ilya2
 * created on 11.04.2017
 */

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.EndTurnAction;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.actions.PhysicalAttackAction;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.behaviour.GreedyOptimizeMove;
import net.demilich.metastone.game.behaviour.diplom.qutils.MinionHeuristic;
import net.demilich.metastone.game.cards.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SubRandomBehaviour2 extends Behaviour implements Serializable {

    private Random random = new Random();
    private Behaviour behaviour = new GreedyOptimizeMove(new MinionHeuristic());

    public SubRandomBehaviour2() {
    }

    @Override
    public String getName() {
        return "Sub Random Hu";
    }

    @Override
    public List<Card> mulligan(GameContext context, Player player, List<Card> cards) {
        return new ArrayList<>();
    }

    @Override
    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions) {
        //TODO dont use this!!!
        if (validActions.size() == 1) {
            return validActions.get(0);
        }

        List<GameAction> validActions2 = validActions.stream().filter(gameAction -> !(gameAction instanceof PhysicalAttackAction) && !(gameAction instanceof EndTurnAction)).collect(Collectors.toList());
        if (validActions2.size() != 0) {
            int randomIndex = random.nextInt(validActions.size());
            GameAction randomAction = validActions.get(randomIndex);
            if (randomAction instanceof PhysicalAttackAction) {
                return behaviour.requestAction(context, player, validActions);
            }
            return randomAction;
        } else {
            return behaviour.requestAction(context, player, validActions);
        }

    }

}
