package net.demilich.metastone.game.decks;

import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeckFormat implements Serializable {

    private String name = "";
    private String filename;
    private List<CardSet> sets;

    public DeckFormat() {
        sets = new ArrayList<CardSet>();
    }

    public void addSet(CardSet cardSet) {
        sets.add(cardSet);
    }

    public boolean isInFormat(Card card) {
        return sets.contains(card.getCardSet());
    }

    public boolean isInFormat(CardSet set) {
        return sets.contains(set);
    }

    public boolean isInFormat(Deck deck) {
        for (Card card : deck.getCards()) {
            if (!isInFormat(card)) {
                return false;
            }
        }
        return true;
    }

    public List<CardSet> getCardSets() {
        return new ArrayList<CardSet>(sets);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
