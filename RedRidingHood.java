/**
 * Class implements Red Riding Hood agent
 */
public class RedRidingHood extends Agent {
    int berries;
    private Cell[][] knowledge; //knowledge about current environment
    boolean inBackTrack;
    GameProcess game; //to make connection with launcher
    Location bearLocation; //not null when RRH have seen bear
    Location wolfLocation; //not null when RRH have seen wolf
    Location woodCutterLocation; //not null when RRH knows where is woodCutter
    Location grannyHome;
    boolean woodCutterFound;
    Location woodCuttingPlace;
    Location woodCutterHome;
    boolean inRisk; //true when risk mode is on

    /**
     * Red Riding Hood needs to know where are woodCutterHome, woodCuttingPlace and grannyHome
     */
    RedRidingHood(Location woodCutterHome, Location woodCuttingPlace, Location grannyHome) {
        super();
        this.woodCutterLocation = new Location();
        this.grannyHome = grannyHome;
        this.woodCuttingPlace = woodCuttingPlace;
        this.woodCutterHome = woodCutterHome;
        inRisk = false;
        location.x = 0;
        location.x = 0;
        berries = 6;
        initKnowledge(woodCutterHome, woodCuttingPlace, grannyHome);
        inBackTrack = false;
    }

    /**
     * Method that controlls RRH moving. Firstly, she increases number of path length, then checks neighbourhood cells for wolf, bear and current cell to the woodcutter and marks current cell as been
     * Then she tries to move to apprpriate to grandma direction, then tries to move in other directions
     * If there is no way to go, she starts backtracking
     */
    public void move() {
        game.pathLength++;
        switch (game.checkWolfZone().size()) {
            case 0: break;
            case 1: knowledge[game.checkWolfZone().get(0).y][game.checkWolfZone().get(0).x].wolfZone = true;
                break;
            case 2: knowledge[game.checkWolfZone().get(0).y][game.checkWolfZone().get(0).x].wolfZone = true;
                knowledge[game.checkWolfZone().get(1).y][game.checkWolfZone().get(1).x].wolfZone = true;
                break;
            default: break;
        }
        if (game.checkBearZone() != null && !inRisk) knowledge[game.checkBearZone().y][game.checkBearZone().x].bearZone = true;
        if (game.woodCutterFound()) knowledge[location.y][location.x].woodCutter = true;
        knowledge[location.y][location.x].been = true;
        if (canMove()) {
            if (!moveInAppropriateDirection()) {
                if (knowledge[location.y][location.x].isPossibleMoveRight())
                    moveRight();
                else if (knowledge[location.y][location.x].isPossibleMoveDown())
                    moveDown();
                else if (knowledge[location.y][location.x].isPossibleMoveLeft())
                    moveLeft();
                else if (knowledge[location.y][location.x].isPossibleMoveUp())
                    moveUp();
            }
            if (!inRisk) game.rrhCheckBear();
            game.rrhCheckWolf();
        } else if (!inRisk) {
            backtrack();
        }
    }

    /**
     * Method that chooses and moves RRH to the appropriate direction
     * @return true if trying was successful
     */
    private boolean moveInAppropriateDirection() {
        int horizontalPrority = location.x - grannyHome.x;
        int verticalPriority = location.y - grannyHome.y;
        if (Math.abs(horizontalPrority) > Math.abs(verticalPriority)) {
            if (horizontalPrority > 0) {
                if (knowledge[location.y][location.x].isPossibleMoveLeft()) {
                    moveLeft();
                    return true;
                }
            } else if (knowledge[location.y][location.x].isPossibleMoveRight()) {
                moveRight();
                return true;
            }
        } else if (verticalPriority > 0) {
            if (knowledge[location.y][location.x].isPossibleMoveUp()) {
                moveUp();
                return true;
            }
        } else if (knowledge[location.y][location.x].isPossibleMoveDown()) {
            moveDown();
            return true;
        }
        return false;
    }

    /**
     * @return true if RRH can move from current cell to the safety and not been cell
     */
    private boolean canMove() {
        return knowledge[location.y][location.x].isPossibleMoveDown() || knowledge[location.y][location.x].isPossibleMoveUp() || knowledge[location.y][location.x].isPossibleMoveRight() || knowledge[location.y][location.x].isPossibleMoveLeft();
    }

    /**
     * Method backtracks RRH
     */
    private void backtrack() {
        int i = 0;
        while (!stopBackTrack() && !canMove() && i < 80) {
            location = knowledge[location.y][location.x].prev;
            i++;
        }
        if (stopBackTrack()) {
            inRisk = true;
        }
    }

    /**
     * @return true if algorithm can not solve this map and we need risk mode
     */
    private boolean stopBackTrack() {
        return location.x == 0 && location.y == 0 && knowledge[location.y][location.x].prev.y == 0 && knowledge[location.y][location.x].prev.x == 0;
    }


    /**
     * Methods describe RRH moves in different directions
     */
    private void moveRight() {
        knowledge[location.y][location.x+1].prev.x = location.x;
        knowledge[location.y][location.x+1].prev.y = location.y;
        location.x += 1;
    }
    private void moveLeft () {
        knowledge[location.y][location.x-1].prev.x = location.x;
        knowledge[location.y][location.x-1].prev.y = location.y;
        location.x -= 1;
    }
    private void moveUp() {
        knowledge[location.y-1][location.x].prev.x = location.x;
        knowledge[location.y-1][location.x].prev.y = location.y;
        location.y -= 1;
    }
    private void moveDown() {
        knowledge[location.y+1][location.x].prev.x = location.x;
        knowledge[location.y+1][location.x].prev.y = location.y;
        location.y += 1;
    }

    /**
     * Method initializes RRH knowledge map
     */
    public void initKnowledge(Location woodCutterHome, Location woodCuttingPlace, Location granny) {
        knowledge = new Cell[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                knowledge[i][j] = new Cell();
            }
        }
        knowledge[0][0].been = true;
        knowledge[woodCutterHome.y][woodCutterHome.x].woodCutterHome = true;
        knowledge[woodCuttingPlace.y][woodCuttingPlace.x].woodCuttingPlace = true;
        knowledge[granny.y][granny.x].granny = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                knowledge[i][j].possibleMoveDown = true;
                knowledge[i][j].possibleMoveUp = true;
                knowledge[i][j].possibleMoveLeft = true;
                knowledge[i][j].possibleMoveRight = true;
            }
        }
        for (int i = 0; i < 9; i++) {
            knowledge[0][i].possibleMoveUp = false;
        }
        for (int i = 0; i < 9; i++) {
            knowledge[i][0].possibleMoveLeft = false;
        }
        for (int i = 0; i < 9; i++) {
            knowledge[8][i].possibleMoveDown = false;
        }
        for (int i = 0; i < 9; i++) {
            knowledge[i][8].possibleMoveRight = false;
        }
    }

    /**
     * Method that uses only in risk mode, when RRH lost some berries. If RRH already was in woodCuttingPlace or in woodCutterHome, she will try to check other place if there is WoodCutter and than teleport to Granny
     */
    public void tryToFillBasket() {
        if (knowledge[woodCuttingPlace.y][woodCuttingPlace.x].been && !knowledge[woodCutterHome.y][woodCutterHome.x].been) {
            location.y = woodCutterHome.y;
            location.x = woodCutterHome.x;
            game.woodCutterFound();
            location.y = grannyHome.y;
            location.x = grannyHome.x;
        }
        if (!knowledge[woodCuttingPlace.y][woodCuttingPlace.x].been && knowledge[woodCutterHome.y][woodCutterHome.x].been) {
            location.y = woodCuttingPlace.y;
            location.x = woodCuttingPlace.x;
            game.woodCutterFound();
            location.y = grannyHome.y;
            location.x = grannyHome.x;
        }
        if (!knowledge[woodCuttingPlace.y][woodCuttingPlace.x].been  && !knowledge[woodCutterHome.y][woodCutterHome.x].been) {
            location.y = woodCutterHome.y;
            location.x = woodCutterHome.x;
            game.woodCutterFound();
            location.y = grannyHome.y;
            location.x = grannyHome.x;
            game.woodCutterFound();
            location.y = grannyHome.y;
            location.x = grannyHome.x;
        }
    }

    /**
     * Class implements one cell from RRH knowledge
     */
    public class Cell {
        Cell() {
            prev = new Location();
        }
        Location prev;
        boolean been;
        boolean possibleMoveRight;
        boolean possibleMoveLeft;
        boolean possibleMoveDown;
        boolean possibleMoveUp;
        boolean woodCutter;
        boolean bearZone;
        boolean wolfZone;
        boolean woodCutterHome;
        boolean woodCuttingPlace;
        boolean granny;

        /**
         * Following methods checks directions whether we can move or not
         * @return true if we can move in this direction
         */
        public boolean isPossibleMoveRight() {
            if (possibleMoveRight && !knowledge[location.y][location.x +1].wolfZone && !knowledge[location.y][location.x +1].bearZone && !knowledge[location.y][location.x +1].been) {
                return true;
            } else return false;
        }
        public boolean isPossibleMoveLeft() {
            if (possibleMoveLeft && !knowledge[location.y][location.x-1].wolfZone && !knowledge[location.y][location.x -1].bearZone && !knowledge[location.y][location.x -1].been) {
                return true;
            } else return false;
        }
        public boolean isPossibleMoveUp() {
            if (possibleMoveUp && !knowledge[location.y-1][location.x].wolfZone && !knowledge[location.y -1][location.x].bearZone && !knowledge[location.y -1][location.x].been) {
                return true;
            } else return false;
        }
        public boolean isPossibleMoveDown() {
            if (possibleMoveDown && !knowledge[location.y+1][location.x].wolfZone && !knowledge[location.y+1][location.x].bearZone && !knowledge[location.y+1][location.x].been) {
                return true;
            } else return false;
        }
    }
}
