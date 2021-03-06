/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.warp.research.providers;

import org.destinationsol.Const;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.planet.SolSystem;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.warp.research.ResearchAction;
import org.destinationsol.warp.research.ResearchProvider;
import org.destinationsol.warp.research.actions.SolarResearchAction;

import java.util.HashMap;
import java.util.Map;

public class SolarResearchProvider implements ResearchProvider {
    private Map<SolSystem, ResearchAction> solarResearchMap = new HashMap<SolSystem, ResearchAction>();

    /**
     * Returns true if the provider is currently capable of doing research
     *
     * @param game         the game to research in
     * @param researchShip the ship to research with
     * @return if the provided can currently do any research
     */
    @Override
    public boolean canProvideResearch(SolGame game, SolShip researchShip) {
        SolSystem nearestSystem = game.getPlanetManager().getNearestSystem(researchShip.getPosition());
        float sunDistance = nearestSystem.getPosition().dst(researchShip.getPosition());

        boolean isNearToSun = (sunDistance < Const.SUN_RADIUS);

        if (!isNearToSun) {
            return false;
        }

        if (solarResearchMap.containsKey(nearestSystem)) {
            return !solarResearchMap.get(nearestSystem).isResearchComplete();
        } else {
            return true;
        }
    }

    /**
     * Obtains the current research action
     *
     * @param game         the game to research in
     * @param researchShip the ship to research with
     * @return the current research action
     */
    @Override
    public ResearchAction getAction(SolGame game, SolShip researchShip) {
        if (!canProvideResearch(game, researchShip)) {
            return null;
        }

        SolSystem nearestSystem = game.getPlanetManager().getNearestSystem(researchShip.getPosition());

        ResearchAction researchAction;

        if (!solarResearchMap.containsKey(nearestSystem)) {
            researchAction = new SolarResearchAction(nearestSystem);
            solarResearchMap.put(nearestSystem, researchAction);
        } else {
            researchAction = solarResearchMap.get(nearestSystem);
        }

        if (researchAction.isResearchComplete()) {
            return null;
        } else {
            return researchAction;
        }
    }

    /**
     * Obtains the currently discovered research actions
     *
     * @return the currently discovered actions
     */
    @Override
    public ResearchAction[] getDiscoveredActions() {
        return solarResearchMap.values().toArray(new ResearchAction[0]);
    }
}
