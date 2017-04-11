package net.demilich.metastone.game.behaviour;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.cards.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayRandomBehaviour extends Behaviour implements Serializable {
    private int i = 0;

    private Random random = new Random();

    @Override
    public String getName() {
        return "Play Random";
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
        /*if (player.getMinions().size() != 0 && context.getOpponent(player).getMinions().size() != 0) {
            GameContext context1 = context.clone();
            DiplomBehaviour diplomBehaviour = new DiplomBehaviour(context1);
            while (!diplomBehaviour.finished){
            }
        }*/
        return randomAction;
    }

}
