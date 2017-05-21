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
    private Random random = new Random();

    public PlayRandomBehaviour() {
    }

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
        return randomAction;
    }

    @Override
    public void printGame() {

    }
}
