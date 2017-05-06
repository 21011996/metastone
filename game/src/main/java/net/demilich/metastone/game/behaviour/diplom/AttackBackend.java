package net.demilich.metastone.game.behaviour.diplom;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.EndTurnAction;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.actions.PhysicalAttackAction;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.behaviour.GreedyOptimizeMove;
import net.demilich.metastone.game.behaviour.heuristic.WeightedHeuristic;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ilya2 on 03.05.2017.
 */
public class AttackBackend {
    private Behaviour behaviour = new GreedyOptimizeMove(new WeightedHeuristic());

    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions, int attackerId) {
        List<GameAction> pass1 = validActions.stream().filter(gameAction -> gameAction instanceof EndTurnAction || gameAction instanceof PhysicalAttackAction).collect(Collectors.toList());
        List<GameAction> pass = pass1.stream().filter(gameAction -> (gameAction instanceof EndTurnAction) || (((PhysicalAttackAction) gameAction).getAttackerReference().getId() == attackerId)).collect(Collectors.toList());
        return behaviour.requestAction(context, player, pass);
    }
}
