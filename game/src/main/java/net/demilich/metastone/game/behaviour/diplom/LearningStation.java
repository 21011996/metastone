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
    public GameContext context;
    public int i = 0;
    TradingLearningAgent tla;

    public LearningStation() {
        tla = new TradingLearningAgent();
    }

    public void preRun() {
        finished = false;
        finished2 = false;
    }

    public void runLearning(GameContext context) {
        context.getActivePlayer().setBehaviour(tla);
        context.getOpponent(context.getActivePlayer()).setBehaviour(new GreedyOptimizeMove(new MinionHeuristic()));
        context.resume(this);
        while (!finished) {

        }
        i++;
        /*if (i % 50 == 0) {
            tla.diplomBehaviour.forceSave();
            System.out.println(tla.diplomBehaviour.error * 1.0 / tla.diplomBehaviour.total);
        }*/
        finished2 = true;
    }
}
