package net.demilich.metastone.game.decks;

import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.game.entities.heroes.HeroClass;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class DeckFactory implements Serializable {

    public static Deck getDeckConsistingof(int count, Card... cards) {
        CardCollection cardCollection = new CardCollection();
        for (int i = 0; i < count; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(cards.length);
            cardCollection.add(cards[randomIndex].clone());
        }
        Deck deck = new Deck(HeroClass.ANY);
        deck.setName("[Debug deck]");
        deck.getCards().addAll(cardCollection);
        return deck;
    }

    public static Deck getRandomDeck(HeroClass heroClass, DeckFormat deckFormat) {
        return new RandomDeck(heroClass, deckFormat);
    }

}
