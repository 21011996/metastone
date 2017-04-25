package net.demilich.metastone.game.behaviour;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.behaviour.diplom.LearningStation;
import net.demilich.metastone.game.cards.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayRandomBehaviour2 extends Behaviour implements Serializable {
    LearningStation learningStation;
    private int i = 0;
    private Random random = new Random();

    public PlayRandomBehaviour2() {
        learningStation = new LearningStation();
    }

    @Override
    public String getName() {
        return "Learn by Play Random";
    }

    @Override
    public List<Card> mulligan(GameContext context, Player player, List<Card> cards) {
        return new ArrayList<>();
    }

    @Override
    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions) {
        if (validActions.size() == 1) {
            return validActions.get(0);
        }

        int randomIndex = random.nextInt(validActions.size());
        GameAction randomAction = validActions.get(randomIndex);
        /*if (player.getMinions().size() != 0 && context.getOpponent(player).getMinions().size() != 0) {
            ObjectOutputStream oos = null;
            try {
                while (new File("dataset\\" + i + ".ser").isFile()) {
                    i++;
                }
                oos = new ObjectOutputStream(new FileOutputStream("dataset\\" + i + ".ser"));
                i++;
                oos.writeObject(context);
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        if (true) {
            int plmin = player.getMinions().size();
            int oppmin = context.getOpponent(player).getMinions().size();
            if ((plmin != 0 || oppmin != 0) && !(plmin > 0 && oppmin == 0)) {
                GameContext context1 = context.clone();
                learningStation.preRun();
                learningStation.runLearning(context1);
                while (!learningStation.finished2) {
                }
                context1.dispose();
            }
            //ReplayBank.printProfile();
        }
        return randomAction;
    }

}
