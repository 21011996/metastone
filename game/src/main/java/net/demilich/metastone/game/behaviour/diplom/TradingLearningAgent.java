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

    public double eps = 1;
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
        List<GameAction> tradingActions = validActions.stream().filter(gameAction -> gameAction instanceof PhysicalAttackAction || gameAction instanceof EndTurnAction).collect(Collectors.toList());
        if (eps < random.nextDouble()) {
            GameAction action = diplomBehaviour.requestAction(context, player, tradingActions);
            ReplayBank.addTrainUnit(new TrainUnit(context, player, tradingActions, action));
            return action;
        } else {
            if (tradingActions.size() != 0) {
                int randomIndex = random.nextInt(tradingActions.size());
                GameAction randomAction = tradingActions.get(randomIndex);
                ReplayBank.addTrainUnit(new TrainUnit(context, player, tradingActions, randomAction));
                return randomAction;
            } else {
                ReplayBank.addTrainUnit(new TrainUnit(context, player, tradingActions, new EndTurnAction()));
                return new EndTurnAction();
            }
        }
    }
}
