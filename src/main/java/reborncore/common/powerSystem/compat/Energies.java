package reborncore.common.powerSystem.compat;

import io.github.alexiyorlov.energy.handlers.EnergyHandler;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;
import team.reborn.energy.EnergyTier;

public class Energies {

	public static void setup(){

		Energy.registerHolder(object -> {
			if(object instanceof EnergyHandler){
				return ((EnergyHandler) object).getMaximalOutput() > 0;
			}
			return false;
		}, object -> {
			final EnergyHandler handler = (EnergyHandler) object;

			final EnergyTier energyTier = getTeir(handler.getMaximalOutput() * 2.5);

			return new EnergyStorage() {
				@Override
				public double getStored(EnergySide face) {
					return handler.getEnergy() * 2.5;
				}

				@Override
				public void setStored(double amount) {
					handler.setEnergy((int) (amount / 2.5));
				}

				@Override
				public double getMaxStoredPower() {
					return handler.getMaximalEnergyAmount() * 2.5;
				}

				@Override
				public EnergyTier getTier() {
					return energyTier;
				}

				@Override
				public double getMaxInput(EnergySide side) {
					return 0; //Only allows extracting as the genertors mod seems to allow power input and output for all machines, making the cables go crazy
					//return handler.getMaximalInput() * 2.5;
				}

				@Override
				public double getMaxOutput(EnergySide side) {
					return handler.getMaximalOutput() * 2.5;
				}
			};
		});

	}

	private static EnergyTier getTeir(double val) {
		for (EnergyTier tier : EnergyTier.values()) {
			if (tier.getMaxOutput() >= val) {
				return tier;
			}
		}
		return EnergyTier.INFINITE;
	}

}
