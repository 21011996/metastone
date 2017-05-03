package net.demilich.metastone.game.behaviour.diplom;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.EndTurnAction;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.actions.PhysicalAttackAction;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.behaviour.diplom.utils.ReplayBank;
import net.demilich.metastone.game.behaviour.diplom.utils.TrainUnit;
import net.demilich.metastone.game.cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author ilya2
 *         created on 11.04.2017
 */
public class TradingLearningAgent extends Behaviour {

    public DiplomBehaviour diplomBehaviour = new DiplomBehaviour();
    private Random random = new Random();

    @Override
    public String getName() {
        return "Trading Learning Agent";
    }

    @Override
    public List<Card> mulligan(GameContext context, Player player, List<Card> cards) {
        return new ArrayList<>();
    }

    @Override
    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions) {
        List<GameAction> validActions2 = validActions.stream().filter(gameAction -> !(gameAction instanceof PhysicalAttackAction) && !(gameAction instanceof EndTurnAction)).collect(Collectors.toList());
        if (validActions2.size() > 0) {
            return validActions2.get(random.nextInt(validActions2.size()));
        }
        List<GameAction> tradingActions = validActions.stream().filter(gameAction -> gameAction instanceof PhysicalAttackAction || gameAction instanceof EndTurnAction).collect(Collectors.toList());
        if (tradingActions.size() == 0) {
            return new EndTurnAction();
        }
        GameAction action = diplomBehaviour.requestAction(context, player, tradingActions);
        if (ReplayBank.addTrainUnit(new TrainUnit(context, player, tradingActions, action))) {
            diplomBehaviour.forceSave();
        }
        diplomBehaviour.learn();
        return action;
    }
}
