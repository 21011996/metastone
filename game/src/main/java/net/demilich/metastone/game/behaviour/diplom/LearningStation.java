package net.demilich.metastone.game.behaviour.diplom;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.behaviour.GreedyOptimizeMove;
import net.demilich.metastone.game.behaviour.diplom.qutils.MinionHeuristic;

/**
 * @author ilya2
 *         created on 11.04.2017
 */
public class LearningStation {
    public boolean finished2 = false;
    public boolean finished = false;

    public LearningStation(GameContext context) {
        TradingLearningAgent tla = new TradingLearningAgent();
        context.getActivePlayer().setBehaviour(tla);
        context.getOpponent(context.getActivePlayer()).setBehaviour(new GreedyOptimizeMove(new MinionHeuristic()));
        context.resume(this);
        while (!finished) {

        }
        tla.diplomBehaviour.learn();
        tla.diplomBehaviour.saveNet();
        finished2 = true;
    }
}
