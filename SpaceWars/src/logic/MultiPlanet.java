package logic;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import clientServer.Client;

public class MultiPlanet extends Planet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Client secondPlanetOwner;
	private List<SpaceshipIf> secondOwnerOrbit;
	private int friendlyFighterInOrbit = 0;
	private int friendlyBattlestarsInOrbit = 0;

	/**
	 * @param planetName
	 * @param planetId
	 * @throws RemoteException
	 */
	public MultiPlanet(String planetName, int planetId) throws RemoteException {
		super(planetName, planetId);
		this.secondOwnerOrbit = new LinkedList<SpaceshipIf>();
		System.out.println("Multiplanet mit namen :"+this.getName()+" wurde erstellt");
	}

	/* (non-Javadoc)
	 * @see logic.Planet#isMultiPlanet()
	 */
	@Override
	public boolean isMultiPlanet() throws RemoteException {
		return true;
	}

	/* (non-Javadoc)
	 * @see logic.Planet#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	/* (non-Javadoc)
	 * @see logic.Planet#getShipsInOrbit()
	 */
	@Override
	public List<SpaceshipIf> getShipsInOrbit() throws RemoteException {
		// TODO Auto-generated method stub
		return super.getShipsInOrbit();
	}

	/* (non-Javadoc)
	 * @see logic.Planet#getShipsTryToOrbit()
	 */
	@Override
	public List<SpaceshipIf> getShipsTryToOrbit() throws RemoteException {
		// TODO Auto-generated method stub
		return super.getShipsTryToOrbit();
	}

	/* (non-Javadoc)
	 * @see logic.Planet#getSecondOwner()
	 */
	@Override
	public Client getSecondOwner() throws RemoteException {
		return this.secondPlanetOwner;
	}

	/**
	 * @param secondOwner
	 * @throws RemoteException
	 */
	private void setSecondPlanetOwner(Client secondOwner) throws RemoteException {
		this.secondPlanetOwner = secondOwner;
	}

	/* (non-Javadoc)
	 * @see logic.Planet#setPlanetOwner(clientServer.Client)
	 */
	@Override
	public void setPlanetOwner(Client planetOwner) throws RemoteException {
		System.out.println("unterklassen setPlanetOwner");
		if (this.getPlanetOwner() != null) {
			if (this.getPlanetOwner().isKI()) {
				super.setPlanetOwner(planetOwner);
				System.out.println("NewPlanet owner: " + planetOwner.getUsername());
			} else {
				this.setSecondPlanetOwner(planetOwner);
				System.out.println("New second owner: " + planetOwner.getUsername());
			}
		} else {
			super.setPlanetOwner(planetOwner);
			System.out.println("NewPlanet owner: " + planetOwner.getUsername());
		}
	}

	/* (non-Javadoc)
	 * @see logic.Planet#addShipToOrbit(logic.SpaceshipIf)
	 */
	@Override
	public void addShipToOrbit(SpaceshipIf newShip) throws RemoteException {
		System.out.println("unterklasse add ship to orbit");
		if (newShip != null) {
			System.out.println("newShip ownerId: " + newShip.getOwner().getOwnerId());
			if (this.getPlanetOwner() == null || this.getPlanetOwner().equals(newShip.getOwner())) {
				this.getShipsInOrbit().add(newShip);
				this.setPlanetOwner(newShip.getOwner());
				this.generatedCredits = this.generatedCreditsPerShip
						* (this.getShipsInOrbit().size() + this.secondOwnerOrbit.size());
				System.out.println("Added ship to orbit :" + newShip.shipInfo());
			} else if (!this.getPlanetOwner().isKI()) {
				this.secondOwnerOrbit.add(newShip);
				this.setPlanetOwner(newShip.getOwner());
				this.generatedCredits = this.generatedCreditsPerShip
						* (this.getShipsInOrbit().size() + this.secondOwnerOrbit.size());
				System.out.println("Added ship to secondorbit :" + newShip.shipInfo());
			} else {
				this.getShipsTryToOrbit().add(newShip);
				this.setFightAfterRoundEnded(true);
				System.out.println(newShip.shipInfo() + " zu tryTo Orbit hinzugefügt planet: " + this.getName());

			}
		}

	}

	/* (non-Javadoc)
	 * @see logic.Planet#removeShipFromOrbit(logic.SpaceshipIf)
	 */
	@Override
	public boolean removeShipFromOrbit(SpaceshipIf shipToRemove) throws RemoteException {
		// TODO Auto-generated method stub
		if (shipToRemove.getOwner().equals(getPlanetOwner())) {
			super.removeShipFromOrbit(shipToRemove);
			return true;
		} else if (shipToRemove.getOwner().equals(getSecondOwner())) {
			this.secondOwnerOrbit.remove(shipToRemove);
			return true;
		} else {
			return false;
		}

	}

	/* (non-Javadoc)
	 * @see logic.Planet#delShips()
	 */
	@Override
	public void delShips() throws RemoteException {
		// TODO Auto-generated method stub
		super.delShips();
		this.secondOwnerOrbit.clear();
	}

	/* (non-Javadoc)
	 * @see logic.Planet#roundEnd()
	 */
	@Override
	public void roundEnd() throws RemoteException {
		this.fighterInOrbit = 0;
		this.battlestarsInOrbit = 0;
		this.friendlyBattlestarsInOrbit = 0;
		this.friendlyFighterInOrbit = 0;
		System.out.println("[ round end kein kampf\n");
		for (SpaceshipIf s : this.getShipsInOrbit()) {
			if (s.isFighter()) {
				this.fighterInOrbit++;
				System.out.println("Fighter " + s.shipInfo());
			} else {
				this.battlestarsInOrbit++;
				System.out.println("Battlestar " + s.shipInfo());
			}
		}
		for (SpaceshipIf s : this.secondOwnerOrbit) {
			if (s.isFighter()) {
				this.friendlyFighterInOrbit++;
				System.out.println("Fighter " + s.shipInfo());
			} else {
				this.friendlyBattlestarsInOrbit++;
				System.out.println("Battlestar " + s.shipInfo());
			}
		}
		System.out.println("\t\t]");
		System.out.println("-----------------------------------ships in Orbit of planet: " + this.getName()
				+ "--------------------------------------");
		for (SpaceshipIf s : this.getShipsInOrbit()) {
			System.out.println(s.shipInfo());
		}
		for (SpaceshipIf s : this.secondOwnerOrbit) {
			System.out.println(s.shipInfo());
		}
		if(this.planetOwner!=null){
			this.planetOwner.setAmountOfPlanets(this.planetOwner.getAmountOfPlanets()+1);
		}
		System.out.println("-----------------------------------ende--------------------------------------");

	}

	/* (non-Javadoc)
	 * @see logic.Planet#fight()
	 */
	@Override
	public BattleReport fight() throws RemoteException {
		boolean isDoubleAttack = false;
		Client secondAttacker = null;
		Client shipOwner = this.getShipsTryToOrbit().get(0).getOwner();
		for (SpaceshipIf s : this.getShipsTryToOrbit()) {
			if (!s.getOwner().equals(shipOwner)) {
				isDoubleAttack = true;
				secondAttacker = s.getOwner();
			}
		}

		if (!secondOwnerOrbit.isEmpty() || isDoubleAttack) {
			Client attacker = this.getShipsTryToOrbit().get(0).getOwner();
			Client defender = this.getPlanetOwner();
			Client secondDefender = this.getSecondOwner();
			BattleReport report = new BattleReport(this);
			List<SpaceshipIf> shipsDefeated = new LinkedList<SpaceshipIf>();
			List<SpaceshipIf> combinedForce = new LinkedList<SpaceshipIf>();
			this.fighterInOrbit = 0;
			this.battlestarsInOrbit = 0;
			this.friendlyBattlestarsInOrbit = 0;
			this.friendlyFighterInOrbit = 0;
			System.out.println(this.getName() + " kampf startet");
			// combine defending forces
			for (SpaceshipIf s : this.getShipsInOrbit()) {
				if (!s.isFighter()) {
					combinedForce.add(s);
				}
			}
			if (isDoubleAttack) {
				for (SpaceshipIf s : this.secondOwnerOrbit) {
					if (!s.isFighter()) {
						combinedForce.add(s);
					}
				}
			}

			for (SpaceshipIf s : this.getShipsInOrbit()) {
				if (s.isFighter()) {
					combinedForce.add(s);
				}
			}
			if (isDoubleAttack) {
				for (SpaceshipIf s : this.secondOwnerOrbit) {
					if (s.isFighter()) {
						combinedForce.add(s);
					}
				}
			}

			// let the battle begin
			int attackingShipAttack = 0;
			int defendingShipAttack = 0;
			for (SpaceshipIf attackingShip : this.getShipsTryToOrbit()) {
				for (SpaceshipIf defendingShip : combinedForce) {
					if (!shipsDefeated.contains(attackingShip)) {
						if (!shipsDefeated.contains(defendingShip)) {
							System.out.println(attackingShip.shipInfo() + "ist angreifendes Schiff\n");
							System.out.println(defendingShip.shipInfo() + "ist verteidigendes Schiff\n");
							attackingShipAttack = attackingShip.attack();
							defendingShipAttack = defendingShip.attack();
							System.out.println("angreiferAttack: " + attackingShipAttack + " defendingShipAttack:"
									+ defendingShipAttack);
							if (attackingShipAttack <= defendingShipAttack) {
								System.out.println(
										defendingShip.getOwner().getUsername() + " hat gewonnen als defender mit: "
												+ defendingShipAttack + " zu : " + attackingShipAttack);
								shipsDefeated.add(attackingShip);
							} else {
								System.out.println(
										attackingShip.getOwner().getUsername() + "hat gewonnen als atacker mit: "
												+ attackingShipAttack + " zu : " + defendingShipAttack);
								shipsDefeated.add(defendingShip);
							}
						}
					}

				}
			}
			// battle is over
			this.getShipsInOrbit().removeAll(shipsDefeated);
			this.getShipsTryToOrbit().removeAll(shipsDefeated);
			this.secondOwnerOrbit.removeAll(shipsDefeated);
			combinedForce.removeAll(shipsDefeated);
			report.setDefeatedShips(shipsDefeated);
			if (this.getShipsTryToOrbit().isEmpty()) {
				if (!isDoubleAttack) {
					report.setWinnersUsername(defender.getUsername() + " und " + secondDefender.getUsername());
					report.setLoosersUsername(attacker.getUsername());
				} else {
					report.setWinnersUsername(defender.getUsername());
					report.setLoosersUsername(attacker.getUsername() + " und " + secondAttacker.getUsername());
				}
				for (SpaceshipIf s : combinedForce) {
					s.increaseRank();
				}
				if (this.getShipsInOrbit().isEmpty()) {
					this.getShipsInOrbit().addAll(secondOwnerOrbit);
					this.planetOwner = null;
					this.setPlanetOwner(secondDefender);
					this.secondPlanetOwner = null;
					this.secondOwnerOrbit.clear();
					defender.setAmountOfPlanets(defender.getAmountOfPlanets() - 1);
					secondDefender.setAmountOfPlanets(secondDefender.getAmountOfPlanets() + 1);
				}
				defender.setAmountOfPlanets(defender.getAmountOfPlanets() + 1);
				combinedForce.clear();
			} else {
				for (SpaceshipIf s : this.getShipsTryToOrbit()) {
					s.increaseRank();
				}
				if (!isDoubleAttack) {
					this.getShipsInOrbit().addAll(this.getShipsTryToOrbit());
					this.getShipsTryToOrbit().clear();
					this.setPlanetOwner(attacker);
					report.setWinnersUsername(attacker.getUsername());
					report.setLoosersUsername(defender.getUsername() + " und " + secondDefender.getUsername());
					defender.setAmountOfPlanets(defender.getAmountOfPlanets() - 1);
					attacker.setAmountOfPlanets(attacker.getAmountOfPlanets() + 1);
				} else {
					Client newOwner = this.getShipsTryToOrbit().get(0).getOwner();
					Client secondOwner = null;
					report.setLoosersUsername(defender.getUsername());
					report.setWinnersUsername(attacker.getUsername() + " und " + secondAttacker.getUsername());
					for (SpaceshipIf s : this.getShipsTryToOrbit()) {
						if (!s.getOwner().equals(newOwner)) {
							secondOwner = s.getOwner();
							this.getSecondOwnerOrbit().add(s);
						} else {
							this.getShipsInOrbit().add(s);
						}
					}
					this.setPlanetOwner(newOwner);
					this.setPlanetOwner(secondOwner);
					this.getShipsTryToOrbit().clear();
					defender.setAmountOfPlanets(defender.getAmountOfPlanets() - 1);
					newOwner.setAmountOfPlanets(newOwner.getAmountOfPlanets() + 1);

				}
			}
			this.setFightAfterRoundEnded(false);
			for (SpaceshipIf s : this.getShipsInOrbit()) {
				if (s.isFighter()) {
					report.addFighterAfterBattle();
					this.fighterInOrbit++;
					System.out.println(s.shipInfo());
				} else {
					report.addBattlestarsAfterBattle();
					this.battlestarsInOrbit++;
					System.out.println(s.shipInfo());

				}
			}
			for (SpaceshipIf s : this.secondOwnerOrbit) {
				if (s.isFighter()) {
					report.addFighterAfterBattle();
					this.friendlyFighterInOrbit++;
					System.out.println(s.shipInfo());
				} else {
					report.addBattlestarsAfterBattle();
					this.friendlyBattlestarsInOrbit++;
					System.out.println(s.shipInfo());

				}
			}
			System.out.println("-----------------------------------ships in Orbit of planet: " + this.getName()
					+ "--------------------------------------");
			for (SpaceshipIf s : this.getShipsInOrbit()) {
				System.out.println(s.shipInfo());
			}
			System.out.println("-----------------------------------ende--------------------------------------");

			return report;
		} else {
			return super.fight();
		}

	}
	/* (non-Javadoc)
	 * @see logic.Planet#getSecondOwnerOrbit()
	 */
	@Override
	public List<SpaceshipIf> getSecondOwnerOrbit() throws RemoteException {
		return secondOwnerOrbit;
	}

	/* (non-Javadoc)
	 * @see logic.Planet#getFighterInOrbit(clientServer.Client)
	 */
	@Override
	public int getFighterInOrbit(Client owner) throws RemoteException {
		// TODO Auto-generated method stub
		if (owner.equals(getPlanetOwner())) {
			return super.getFighterInOrbit();
		} else {
			return this.friendlyFighterInOrbit;
		}
	}

	/* (non-Javadoc)
	 * @see logic.Planet#getBattlestarsInOrbit(clientServer.Client)
	 */
	@Override
	public int getBattlestarsInOrbit(Client owner) throws RemoteException {
		// TODO Auto-generated method stub
		if (owner.equals(getPlanetOwner())) {
			return super.getBattlestarsInOrbit();
		} else {
			return this.friendlyBattlestarsInOrbit;
		}
	}

	/* (non-Javadoc)
	 * @see logic.Planet#payCash()
	 */
	@Override
	public void payCash() throws RemoteException {
		super.payCash();
		if(this.secondPlanetOwner!=null){
			this.secondPlanetOwner.addCash(this.getGeneratedCreditsPerShip());
		}
	}
	

}
