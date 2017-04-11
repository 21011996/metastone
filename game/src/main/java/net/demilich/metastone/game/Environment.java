package net.demilich.metastone.game;

public enum Environment {
    SUMMON_REFERENCE_STACK,
    KILLED_MINION,
    TARGET_OVERRIDE,
    ATTACKER_REFERENCE,
    EVENT_TARGET_REFERENCE_STACK,
    TARGET,
    SPELL_TARGET,
    TRANSFORM_REFERENCE,
    DAMAGE_STACK,
    DEBUG,
    PENDING_CARD,
    EVENT_CARD,
    CHOOSE_ONE_CARD,
    LAST_MANA_COST,;

    public boolean customClone() {
        return (this == SUMMON_REFERENCE_STACK ||
                this == EVENT_TARGET_REFERENCE_STACK ||
                this == DAMAGE_STACK);
    }
}
