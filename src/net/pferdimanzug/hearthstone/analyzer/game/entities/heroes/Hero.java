package net.pferdimanzug.hearthstone.analyzer.game.entities.heroes;

import net.pferdimanzug.hearthstone.analyzer.game.GameTag;
import net.pferdimanzug.hearthstone.analyzer.game.entities.Entity;
import net.pferdimanzug.hearthstone.analyzer.game.entities.EntityType;
import net.pferdimanzug.hearthstone.analyzer.game.entities.weapons.Weapon;
import net.pferdimanzug.hearthstone.analyzer.game.heroes.powers.HeroPower;

public abstract class Hero extends Entity {

	private final HeroClass heroClass;
	private final HeroPower heroPower;
	private Weapon weapon;
	
	public Hero(String name, HeroClass heroClass, HeroPower heroPower) {
		super(null);
		setName(name);
		this.heroClass = heroClass;
		this.heroPower = heroPower;
	}

	public HeroClass getHeroClass() {
		return heroClass;
	}

	public HeroPower getHeroPower() {
		return heroPower;
	}
	
	@Override
	public EntityType getEntityType() {
		return EntityType.HERO;
	}
	
	@Override
	public int getAttack() {
		int attack = super.getAttack();
		if (getWeapon() != null) {
			attack += weapon.getAttack();
		}
		return attack;
	}
	
	public int getArmor() {
		return hasTag(GameTag.ARMOR) ? getTagValue(GameTag.ARMOR) : 0;
	}

	public void modifyArmor(int armor) {
		// armor cannot fall below zero
		int newArmor = Math.max(getArmor() + armor, 0);
		setTag(GameTag.ARMOR, newArmor);
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
		if (weapon != null) {
			weapon.setOwner(getOwner());
		}
	}
	
}
