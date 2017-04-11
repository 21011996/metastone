package net.demilich.metastone.game.behaviour.human;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.GameAction;

import java.util.List;

public class HumanActionOptions {

    private final HumanBehaviour behaviour;
    private final GameContext context;
    private final Player player;
    private final List<GameAction> validActions;

    public HumanActionOptions(HumanBehaviour behaviour, GameContext context, Player player, List<GameAction> validActions) {
        this.behaviour = behaviour;
        this.context = context;
        this.player = player;
        this.validActions = validActions;
    }

    public HumanBehaviour getBehaviour() {
        return behaviour;
    }

    public GameContext getContext() {
        return context;
    }

    public Player getPlayer() {
        return player;
    }

    public List<GameAction> getValidActions() {
        return validActions;
    }

}
