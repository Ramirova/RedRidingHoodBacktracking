import java.util.LinkedList;
/**
 * Game Launcher
 */
public class GameProcess {
    boolean rrhEaten;
    int pathLength;
    boolean badMap; //indicates whether map is solvable or not
    boolean winningMap;
    boolean gameOver; //is game correctly finished or not
    String generatedMap; //string representation of generated environment
    private Forest forest; //environment

    GameProcess() {
        badMap = false;
        forest = new Forest();
        generatedMap = forest.forestToString();
        forest.redRidingHood.game = this;
        forest.bear.game = this;
        forest.wolf.game = this;
    }

    /**
     * Game starts there and finishes if risk is not needed
     */
    public void startGame() {
        while (!gameOver() && !forest.redRidingHood.inRisk) {
            forest.newMove();
            forest.bear.bearAction();
            rrhEaten = forest.wolf.wolfAction();
        }
        if (!gameOver() && forest.redRidingHood.inRisk) riskMode();
        if (!badMap && rrgAchievedGranny() && forest.redRidingHood.berries == 6) winningMap = true;
    }

    /**
     * If Red Riding Hood have to go to bear zone to win, this method works
     */
    private void riskMode() {
        if (forest.redRidingHood.woodCutterFound) {
            badMap = true;
        } else {
            forest.redRidingHood.initKnowledge(forest.redRidingHood.woodCutterHome, forest.redRidingHood.woodCuttingPlace, forest.redRidingHood.grannyHome);
            while (!gameOver() && forest.redRidingHood.berries <= 0) {
                forest.newMove();
                forest.bear.bearAction();
                rrhEaten = forest.wolf.wolfAction();
            }
        }
        if (!forest.redRidingHood.woodCutterFound) {
            forest.redRidingHood.tryToFillBasket();
        }
    }

    /**
     * Checks all possible cases when game is over
     * @return true when game is over
     */
    private boolean gameOver() {
        return rrhEaten || (rrgAchievedGranny() && forest.redRidingHood.berries == 6) || badMap;
    }

    /**
     * @return true if RRH achieved granny or not
     */
    private boolean rrgAchievedGranny() {
        if (forest.redRidingHood.location.x == forest.granny.location.x && forest.redRidingHood.location.y == forest.granny.location.y) {
            return true;
        } else return false;
    }

    /**
     * Allows RRH know where precisely is bear if he is in her detection range
     */
    public void rrhCheckBear() {
        boolean detected = inNeumanNeighbourhood(forest.redRidingHood, forest.bear);
        if (detected) forest.redRidingHood.bearLocation = forest.bear.location;
    }

    /**
     * Allows bear know where is bear if he is in her detection range
     * @return true if RRH in bear's detection range
     */
    public boolean bearDetectedRRH() {
        boolean detected = false;
        Location bearLocation = forest.bear.location;
        Location rrhLocation = forest.redRidingHood.location;
        if (bearLocation.x + 1 <= 8 && bearLocation.x + 1 == rrhLocation.x && bearLocation.y == rrhLocation.y)
            detected = true;
        if (bearLocation.x - 1 >= 0 && bearLocation.x - 1 == rrhLocation.x && bearLocation.y == rrhLocation.y)
            detected = true;
        if (bearLocation.y + 1 <= 8 && bearLocation.x == rrhLocation.x && bearLocation.y + 1 == rrhLocation.y)
            detected = true;
        if (bearLocation.y - 1 >= 0 && bearLocation.x == rrhLocation.x && bearLocation.y - 1 == rrhLocation.y)
            detected = true;
        if (bearLocation.y - 1 >= 0 && bearLocation.x + 1 <= 8 && bearLocation.x + 1 == rrhLocation.x && bearLocation.y - 1 == rrhLocation.y)
            detected = true;
        if (bearLocation.y - 1 >= 0 && bearLocation.x - 1 <= 8 && bearLocation.x - 1 == rrhLocation.x && bearLocation.y - 1 == rrhLocation.y)
            detected = true;
        if (bearLocation.y + 1 >= 0 && bearLocation.x + 1 <= 8 && bearLocation.x + 1 == rrhLocation.x && bearLocation.y + 1 == rrhLocation.y)
            detected = true;
        if (bearLocation.y + 1 >= 0 && bearLocation.x - 1 <= 8 && bearLocation.x - 1 == rrhLocation.x && bearLocation.y + 1 == rrhLocation.y)
            detected = true;
        return detected;
    }

    public void bearEatBerries() {
        forest.redRidingHood.berries -= 2;
    }

    /**
     * Allows wolf know where is bear if he is in her detection range
     * @return true if RRH in wolf's detection range
     */
    public boolean wolfDetectedRRH() {
        return inNeumanNeighbourhood(forest.wolf, forest.redRidingHood);
    }

    /**
     * checks whether passive agent in Neuman neighbourhood of active or not
     * @return true if passive agent in Neuman neighbourhood of active
     */
    private boolean inNeumanNeighbourhood(Agent active, Agent passive) {
        if ((active.location.y + 1 == passive.location.y) && (active.location.x == passive.location.x)) return true;
        if ((active.location.y - 1 == passive.location.y) && (active.location.x == passive.location.x)) return true;
        if ((active.location.y == passive.location.y) && (active.location.x + 1 == passive.location.x)) return true;
        if ((active.location.y == passive.location.y) && (active.location.x + 1 == passive.location.x)) return true;
        return false;
    }

    /**
     *  Allows RRH to know all wolf zone cells in her detection range
     * @return all cells of wolf zone that in detection range of RRH
     */
    public LinkedList<Location> checkWolfZone() {
        Location location = forest.redRidingHood.location;
        LinkedList<Location> smellLocation = new LinkedList<>();
        if ((location.x + 1 < 9) && ((forest.forest[location.y][location.x + 1].wolfSmell) || forest.forest[location.y][location.x + 1].agent == forest.wolf))
            smellLocation.add(new Location(location.x + 1, location.y));
        if ((location.y - 1 >= 0) && ((forest.forest[location.y - 1][location.x].wolfSmell) || forest.forest[location.y - 1][location.x].agent == forest.wolf))
            smellLocation.add(new Location(location.x, location.y - 1));
        if ((location.x - 1 >= 0) && ((forest.forest[location.y][location.x - 1].wolfSmell) || forest.forest[location.y][location.x - 1].agent == forest.wolf))
            smellLocation.add(new Location(location.x - 1, location.y));
        if ((location.y + 1 < 9) && ((forest.forest[location.y + 1][location.x].wolfSmell) || forest.forest[location.y + 1][location.x].agent == forest.wolf))
            smellLocation.add(new Location(location.x, location.y + 1));
        if ((location.y + 2 < 9) && ((forest.forest[location.y + 2][location.x].wolfSmell) || forest.forest[location.y + 2][location.x].agent == forest.wolf))
            smellLocation.add(new Location(location.x, location.y + 2));
        if ((location.y - 2 >= 0) && ((forest.forest[location.y - 2][location.x].wolfSmell) || forest.forest[location.y - 2][location.x].agent == forest.wolf))
            smellLocation.add(new Location(location.x, location.y - 2));
        if ((location.x + 2 < 9) && ((forest.forest[location.y][location.x + 2].wolfSmell) || forest.forest[location.y][location.x + 2].agent == forest.wolf))
            smellLocation.add(new Location(location.x + 2, location.y));
        if ((location.x - 2 >= 0) && ((forest.forest[location.y][location.x - 2].wolfSmell) || forest.forest[location.y][location.x - 2].agent == forest.wolf))
            smellLocation.add(new Location(location.x - 2, location.y));
        return smellLocation;
    }

    /**
     * Allows RRH know where precisely is wolf if he is in her detection range
     */
    public void rrhCheckWolf() {
        Location location = forest.redRidingHood.location;
        boolean detected = false;
        if ((location.x + 1 < 9) && (forest.forest[location.y][location.x + 1].agent == forest.wolf)) detected = true;
        if ((location.y - 1 >= 0) && (forest.forest[location.y - 1][location.x].agent == forest.wolf)) detected = true;
        if ((location.x - 1 >= 0) && (forest.forest[location.y][location.x - 1].agent == forest.wolf)) detected = true;
        if ((location.y + 1 < 9) && (forest.forest[location.y + 1][location.x].agent == forest.wolf)) detected = true;
        if ((location.y + 2 < 9) && (forest.forest[location.y + 2][location.x].agent == forest.wolf)) detected = true;
        if ((location.y - 2 >= 0) && (forest.forest[location.y - 2][location.x].agent == forest.wolf)) detected = true;
        if ((location.x + 2 < 9) && (forest.forest[location.y][location.x + 2].agent == forest.wolf)) detected = true;
        if ((location.x - 2 >= 0) && (forest.forest[location.y][location.x - 2].agent == forest.wolf)) detected = true;
        if (detected) forest.redRidingHood.wolfLocation = forest.wolf.location;
    }

    /**
     * Allows RRH to know that she found RRH and take berries
     * @return true if wood cutter found by RRH
     */
    public Location checkBearZone() {
        Location location = forest.redRidingHood.location;
        Location smellLocation = null;
        if ((location.y + 1 < 9) && ((forest.forest[location.y + 1][location.x].bearSmell) || forest.forest[location.y + 1][location.x].agent == forest.bear))
            smellLocation = new Location(location.x, location.y + 1);
        if ((location.y - 1 >= 0) && ((forest.forest[location.y - 1][location.x].bearSmell) || forest.forest[location.y - 1][location.x].agent == forest.bear))
            smellLocation = new Location(location.x, location.y - 1);
        if ((location.x + 1 < 9) && ((forest.forest[location.y][location.x + 1].bearSmell) || forest.forest[location.y][location.x + 1].agent == forest.bear))
            smellLocation = new Location(location.x + 1, location.y);
        if ((location.x - 1 >= 0) && ((forest.forest[location.y][location.x - 1].bearSmell) || forest.forest[location.y][location.x - 1].agent == forest.bear))
            smellLocation = new Location(location.x - 1, location.y);
        return smellLocation;
    }

    /**
     * Allows RRH to know that she found RRH and take berries
     * @return true if wood cutter found by RRH
     */
    public boolean woodCutterFound() {
        if ((forest.redRidingHood.location.x == forest.woodCutter.location.x) && (forest.redRidingHood.location.y == forest.woodCutter.location.y)) {
            forest.redRidingHood.berries = 6;
            forest.redRidingHood.woodCutterFound = true;
            return true;
        } else return false;
    }
}
