import java.util.*;
import tester.*; // The tester library
import javalib.worldimages.*; // images, like RectangleImage or OverlayImages
import javalib.funworld.*; // the abstract World class and the big-bang library
import javalib.colors.*; // Predefined colors (Red, Green, Yellow, Blue, Black, White)
import java.awt.Color; // general colors (as triples of red,green,blue values)

// Represents a single square of the game area
class Cell {
    // represents absolute height of this cell, in feet
    double height;
    // In logical coordinates, with the origin at the top-left corner of the
    // screen
    int x, y;
    // the four adjacent cells to this one
    Cell left, top, right, bottom;
    // reports whether this cell is flooded or not
    boolean isFlooded;
    // reports whether this cell is under the water height
    boolean isUnderwater;
    // reports whether this cell is occupied by a player or target
    boolean isOccupied;
    
    Cell(double height, int x, int y, Cell left, Cell top, Cell right,
            Cell bottom, boolean isFlooded, boolean isUnderwater,
            boolean isOccupied) {
        this.height = height;
        this.x = x;
        this.y = y;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.isFlooded = isFlooded;
        this.isUnderwater = isUnderwater;
        this.isOccupied = isOccupied;
    }
    Cell(double height, int x, int y) {
        this.height = height;
        this.x = x;
        this.y = y;
        this.left = this;
        this.top = this;
        this.right = this;
        this.bottom = this;
        this.isFlooded = false;
        this.isUnderwater = false;
        this.isOccupied = false;
    }
    Cell(double height, int x, int y, boolean isOccupied) {
        this.height = height;
        this.x = x;
        this.y = y;
        this.left = this;
        this.top = this;
        this.right = this;
        this.bottom = this;
        this.isFlooded = false;
        this.isUnderwater = false;
        this.isOccupied = isOccupied;
    }
    // is this Cell an OceanCell?   
    boolean isOceanCell() {
        return false;
    }
    // return this Cell's color
    Color getColor(double height, int waterHeight) {
        
        if (height > 32) {
            height = 32;
        }
        
        int depth = (int) (32 - height);
        
        if (!isFlooded && !isUnderwater) {
            return new Color(255 - (waterHeight * 6) - (depth * 6),
                    255 - (waterHeight * 3) - (depth * 2), 
                    255 - (waterHeight * 6) - (depth * 6));
        }
        else if (!isFlooded && isUnderwater) {
            return new Color(75 - (depth * 2) + (waterHeight * 5),
                    195 + (depth * 2) - (waterHeight * 5), 75);
        }
        else {
            return new Color(0, 21, 184 - (int)((waterHeight - height) * 3));
        }
    }
    
    //checks if this cell is the same as given cell
    boolean sameCell(Cell that) {
        return this.x == that.x &&
               this.y == that.y;
    }
    
    //draws the cell as a rectangle
    WorldImage renderCell(int waterHeight) {
        return new RectangleImage(new Posn((this.x * 10) - 5, (this.y * 10) - 5),
                10, 10, this.getColor(this.height, waterHeight));
    }
}

// represents a cell in the ocean
class OceanCell extends Cell {
    OceanCell(double height, int x, int y) {
        super(height, x, y);
        this.isFlooded = true;
        this.isUnderwater = true;
    }
    
    // is this Cell an OceanCell?
    boolean isOceanCell() {
        return true;
    }
    
    // return this Cell's color
    Color getColor(double height, int waterHeight) {
        return new Color(0, 21, 184);
    }
}

// represents the player of the forbidden island game
class Player {
    Cell location;
    int targetsFound;
    int steps;
    
    Player(Cell location, int targetsFound, int steps) {
        this.location = location;
        this.targetsFound = targetsFound;
        this.steps = steps;
    }
    
    Player(Cell location, int targetsFound) {
        this.location = location;
        this.targetsFound = targetsFound;
        this.steps = 0;
    }
    
    // moves the player based on the given string key input
    public Player movePlayer(String ke) {
        if (ke.equals("right")) {
            if (!this.location.right.isOceanCell()) {
                if (!this.location.right.isFlooded) {
                    return new Player(this.location.right, this.targetsFound,
                            steps += 1);
                }
            }
        } 
        else if (ke.equals("left")) {
            if (!this.location.left.isOceanCell()) {
                if (!this.location.left.isFlooded) {
                    return new Player(this.location.left, this.targetsFound,
                            steps += 1);
                }
            }
        } 
        else if (ke.equals("up")) {
            if (!this.location.bottom.isOceanCell()) {
                if (!this.location.bottom.isFlooded) {
                    return new Player(this.location.top, this.targetsFound,
                            steps += 1);
                }
            }
        } 
        else if (ke.equals("down")) {
            if (!this.location.top.isOceanCell()) {
                if (!this.location.top.isFlooded) {
                    return new Player(this.location.bottom, this.targetsFound, 
                            steps += 1);
                }
            }
        }
        return this;
    }
    
    WorldImage playerImage() {
        return new DiskImage(new Posn((this.location.x * 10) - 5,
                (this.location.y * 10) - 5), 5, new Red());
    }
}

// represents various pieces in the game
class Target {
    Cell location;
    WorldImage picture;
    
    Target (Cell location) {
        this.location = location;
        this.picture = this.generatePicture();
    }
    
    Target (Cell location, WorldImage picture) {
        this.location = location;
        this.picture = picture;
    }
    
    
    WorldImage generatePicture() {
         ArrayList<WorldImage> pictures = new ArrayList<WorldImage>();
        pictures.add(new FromFileImage(new Posn((this.location.x * 10) - 5,
                (this.location.y * 10) - 5), "Helicopter_Piece_1.png"));
        pictures.add(new FromFileImage(new Posn((this.location.x * 10) - 5,
                (this.location.y * 10) - 5), "Helicopter_Piece_2.png"));
        pictures.add(new FromFileImage(new Posn((this.location.x * 10) - 5,
                (this.location.y * 10) - 5), "Helicopter_Piece_3.png"));
        pictures.add(new FromFileImage(new Posn((this.location.x * 10) - 5,
                (this.location.y * 10) - 5), "Helicopter_Piece_4.png"));
        pictures.add(new FromFileImage(new Posn((this.location.x * 10) - 5,
                (this.location.y * 10) - 5), "Helicopter_Piece_5.png"));
        return pictures.get((int)(Math.random() * 3));
    }
    
    // is this Target a HelicopterTarget?
    boolean isHeli() {
        return false;
    }
    
    
    
}

// represents the helicopter
class HelicopterTarget extends Target {
    HelicopterTarget (Cell location) {
        super(location);
    }
    
    // is this Target a HelicopterTarget?
    boolean isHeli() {
        return true;
    }
}

// represents the world of the forbidden island game
class ForbiddenIslandWorld extends World {
    // All the cells of the game, including the ocean
    IList<Cell> board;
    // the current height of the ocean
    int waterHeight;
    // this world's player
    Player p1;
    // this world's helicopter target
    HelicopterTarget ht;
    // current time expended in-game
    int ticksPassed;
    
    // Defines an int constant
    static final int ISLAND_SIZE = 64;
    static final double MAX_HEIGHT = 31;
    static final int NUMBER_OF_TARGETS = 2;
    // contains all the heights of the cells
    ArrayList<ArrayList<Double>> heights = new ArrayList<ArrayList<Double>>();
    // contains all of the board's cells
    ArrayList<ArrayList<Cell>> cells = new ArrayList<ArrayList<Cell>>();
    // contains all of the board's targets
    ArrayList<Target> targets = new ArrayList<Target>();
    
    ForbiddenIslandWorld() {
        super();
        this.board = makeRandom();
        this.p1 = new Player(this.randomCell(), 0);
        this.waterHeight = 0;
        this.ticksPassed = 0;
    }
    
    ForbiddenIslandWorld(String ke) {
        super();
        if (ke.equals("m")) {
            this.board = makeMountain();
        }
        else if (ke.equals("r")) {
            this.board = makeRandom();
        }
        else {
            this.board = makeTerrain();
        }
        this.p1 = new Player(this.randomCell(), 0);
        this.waterHeight = 0;
        this.ticksPassed = 0;
    }
    
    ForbiddenIslandWorld(IList<Cell> board, int waterHeight, Player p1,
            ArrayList<Target> targets, HelicopterTarget ht, int ticksPassed) {
        super();
        this.board = board;
        this.waterHeight = waterHeight;
        this.p1 = p1;
        this.targets = targets;
        this.ht = ht;
        this.ticksPassed = ticksPassed;
    }
    
    // generates a random, unoccupied cell
    Cell randomCell() {
        boolean noCell = true;
        Cell result = new Cell(0, 0, 0);
        while (noCell) {
            int randomX = (int) (Math.random() * (ISLAND_SIZE + 1));
            int randomY = (int) (Math.random() * (ISLAND_SIZE + 1));
            Cell temp = cells.get(randomX).get(randomY);
            if (!temp.isOccupied && !temp.isOceanCell()) {
                result = temp;
                noCell = false;
            }
        }
        return result;
    }
    
    // prints the world
    public WorldImage makeImage() {
        return this.boardImage().overlayImages(this.p1.playerImage().
                overlayImages(this.targetsImage().overlayImages(
                 this.statsImage())));
    }
    
    // prints the targets
    WorldImage targetsImage() {
        int length = ISLAND_SIZE * 10;
        WorldImage result = new FrameImage(new Posn((length * 10) / 2,
                (length * 10) / 2), length * 11, length * 11, new Color(0, 21, 184));
        for (Target t : targets) {
            if (!t.isHeli()) {
                result = result.overlayImages(t.picture);
            }
            else {
                if (this.p1.targetsFound != NUMBER_OF_TARGETS) {
                    result = result.overlayImages(new FromFileImage(
                            new Posn((this.ht.location.x * 10) - 5,
                                     (this.ht.location.y * 10) - 5),
                            "Helicopter_Pad.png"));
                }
                else {
                    result = result.overlayImages(new FromFileImage(
                            new Posn((this.ht.location.x * 10) - 5,
                                    (this.ht.location.y * 10) - 5),
                           "Helicopter.png"));
                }
            }
        }
        return result;
    }
    
    // prints the board
    WorldImage boardImage() {
        int length = ISLAND_SIZE * 10;
        WorldImage result = new FrameImage(new Posn((length * 10) / 2,
                (length * 10) / 2), length * 10, length * 10, new Color(0, 21, 184));
        for (Cell c : this.board) {
            result = new OverlayImages(result, c.renderCell(this.waterHeight));
        }
        return result;  
    }
    
    // prints the stats
    WorldImage statsImage() {
        WorldImage time = new TextImage(new Posn(56, 20), "Time Passed: " +
                           Integer.toString(this.ticksPassed), new White());
        WorldImage steps = new TextImage(new Posn(56, 40), "Steps Taken: " +
                            Integer.toString(this.p1.steps), new White());
        
        return new OverlayImages(time, steps);
        
        
    }
    
    // updates the world on a tick
    public World onTick() {
        int temp = ticksPassed += 1;
        if (temp % 10 == 0) {
            return new ForbiddenIslandWorld(this.boardUpdate(),
                    this.waterHeight + 1, this.p1, this.targets, this.ht,
                    temp);
        }
        else {
            return new ForbiddenIslandWorld(this.boardUpdate(),
                    this.waterHeight, this.p1, this.targets, this.ht,
                    temp);
        }
        
    }
    
    // updates the board on tick
    public IList<Cell> boardUpdate() {
        IList<Cell> temp = this.board;
            for (Cell c : temp) {
                if (c.height < waterHeight && 
                            (c.right.isFlooded || c.left.isFlooded ||
                                    c.top.isFlooded || c.bottom.isFlooded)) {
                    c.isFlooded = true;
                    c.isUnderwater = true;
                }
                else if (c.height < waterHeight &&
                     !(c.right.isFlooded || c.left.isFlooded ||
                       c.top.isFlooded || c.bottom.isFlooded)) {
                    c.isUnderwater = true;
            }
        }
        
        if (!(this.foundTarget() == -1)) {
            if (!targets.get(this.foundTarget()).isHeli()) {
                targets.remove(this.foundTarget());
                this.p1 = new Player(this.p1.location,
                        this.p1.targetsFound + 1, this.p1.steps);
            }
        }
        
        
        return temp;
    }
    
    // finds the index of the target player found, if any
    // returns -1 if none is being currently found
    int foundTarget() {
        int result = -1;
        if (!(targets.size() == 0)) {
            for (int i = 0; i < targets.size(); i += 1) {
                if (this.p1.location.sameCell(this.targets.get(i).location)) {
                    result = i;
                }
            }
        }
        return result;
    }
    
    // delegates the changes to happen on certain key inputs
    public World onKeyEvent(String ke) {
        if (ke.equals("m") || ke.equals("r") || ke.equals("t")) {
            return new ForbiddenIslandWorld(ke);
        }
        else {
            return new ForbiddenIslandWorld(this.board, this.waterHeight,
                this.p1.movePlayer(ke), this.targets, this.ht,
                this.ticksPassed);
        }
    }
    
    // determines when the world ends
    public WorldEnd worldEnds() {
        int length = (ISLAND_SIZE * 10);
        int center = length / 2;
        if (this.p1.location.isFlooded) {
            return new WorldEnd(true, new RectangleImage(new Posn(center, center), length, length, new White()).
                    overlayImages(new TextImage(new Posn(center, center), "You drowned.", new Red())));
        }
        else if (this.p1.targetsFound == NUMBER_OF_TARGETS) {
                if (this.p1.location.sameCell(this.ht.location)) {
                        return new WorldEnd(true, new RectangleImage(new Posn(center, center), length, length, new White()).
                                overlayImages(new TextImage(new Posn(center, center), "You win!", new Red())));
                }
                else {
                    return new WorldEnd(false, this.makeImage()); 
                }
        }
        else {
            return new WorldEnd(false, this.makeImage());
        }
    }
    
    // initialize the targets of this game
    ArrayList<ArrayList<Cell>> setPieces(ArrayList<ArrayList<Cell>> cells) {
        ArrayList<ArrayList<Cell>> result = cells;
        
        // adds the non-helicopter targets
        for (int i = 0; i < NUMBER_OF_TARGETS; i += 1) {
            Cell temp = this.randomCell();
            targets.add(new Target(temp));
            result.get(temp.x).set(temp.y, 
                    new Cell(temp.height, temp.x, temp.y, true));
        }
        
        // adds the helicopter target
        Cell tempHeli = this.randomCell();
        targets.add(new HelicopterTarget(tempHeli));
        result.get(tempHeli.x).set(tempHeli.y,
                new Cell(tempHeli.height, tempHeli.x, tempHeli.y, true));
        this.ht = new HelicopterTarget(tempHeli);
        
        return result;
    }
    
    // creates a non-random mountain board
    IList<Cell> makeMountain() {
        
        int center = (ISLAND_SIZE / 2) + 1;
        for (int x = 0; x < ISLAND_SIZE + 2; x++) {
            heights.add(new ArrayList<Double>());
            for (int y = 0; y < ISLAND_SIZE + 2; y++) {
                heights.get(x).add(
                        y,
                        (MAX_HEIGHT - Math.abs(center - x) - Math.abs(center
                                - y) + 1));
            }
        }
        
        for (int x = 0; x < ISLAND_SIZE + 2; x++) {
            cells.add(new ArrayList<Cell>());
            for (int y = 0; y < ISLAND_SIZE + 2; y++) {
                if (heights.get(x).get(y) <= 0) {
                    cells.get(x).add(new OceanCell(0, x, y));
                }
                else {
                    cells.get(x).add(new Cell(heights.get(x).get(y), x, y));
                }
            }
        }
        
        cells = this.setPieces(cells);
        
        for (int x = 0; x < ISLAND_SIZE + 2; x++) {
            cells.get(x).get(0).top = cells.get(x).get(0);
            cells.get(x).get(ISLAND_SIZE + 1).bottom = cells.get(x)
                    .get(ISLAND_SIZE + 1);
        }
        for (int y = 0; y < ISLAND_SIZE + 2; y++) {
            cells.get(0).get(y).left = cells.get(0).get(y);
            cells.get(ISLAND_SIZE + 1).get(y).right = cells.get(ISLAND_SIZE + 1).get(y);
        }
        
        cells.get(0).get(0).left = cells.get(0).get(0);
        cells.get(0).get(0).top = cells.get(0).get(0);
        cells.get(0).get(ISLAND_SIZE + 1).left = cells.get(0).get(ISLAND_SIZE + 1);
        cells.get(0).get(ISLAND_SIZE + 1).bottom = cells.get(0).get(ISLAND_SIZE + 1);
        cells.get(ISLAND_SIZE + 1).get(0).right = cells.get(ISLAND_SIZE + 1).get(0);
        cells.get(ISLAND_SIZE + 1).get(0).top = cells.get(ISLAND_SIZE + 1).get(0);
        cells.get(ISLAND_SIZE + 1).get(ISLAND_SIZE + 1).bottom = cells.get(ISLAND_SIZE + 1)
                .get(ISLAND_SIZE + 1);
        cells.get(ISLAND_SIZE + 1).get(ISLAND_SIZE + 1).right = cells.get(ISLAND_SIZE + 1)
                .get(ISLAND_SIZE + 1);
        for (int x = 1; x < ISLAND_SIZE + 1; x++) {
            for (int y = 1; y < ISLAND_SIZE + 1; y++) {
                cells.get(x).get(y).left = cells.get(x - 1).get(y);
                cells.get(x).get(y).top = cells.get(x).get(y - 1);
                cells.get(x).get(y).right = cells.get(x + 1).get(y);
                cells.get(x).get(y).bottom = cells.get(x).get(y + 1);
            }
        }
        
        IList<Cell> result = new Mt<Cell>();
        for (int x = 0; x < ISLAND_SIZE + 2; x++) {
            for (int y = 0; y < ISLAND_SIZE + 2; y++) {
                result = new Cons<Cell>(cells.get(x).get(y), result);
            }
        }
        return result;
    }
    
    // creates an island with random heights
    IList<Cell> makeRandom() {
        for (int x = 0; x < ISLAND_SIZE + 2; x++) {
            heights.add(new ArrayList<Double>());
            for (int y = 0; y < ISLAND_SIZE + 2; y++) {
                heights.get(x).add(0.00);
            }
        }
        int center = (ISLAND_SIZE / 2) + 1;
        Random rand = new Random();
        for (int x = 0; x < ISLAND_SIZE + 2; x++) {
            for (int y = 0; y < ISLAND_SIZE + 2; y++) {
                if ((MAX_HEIGHT - Math.abs(center - x) - Math.abs(center - y)) >= 0) {
                    heights.get(x).add(y,
                            (rand.nextDouble() * (MAX_HEIGHT - 1)) + 1);
                }
            }
        }
        for (int x = 0; x < ISLAND_SIZE + 2; x++) {
            cells.add(new ArrayList<Cell>());
            for (int y = 0; y < ISLAND_SIZE + 2; y++) {
                if (heights.get(x).get(y) <= 0) {
                    cells.get(x).add(new OceanCell(0, x, y));
                }
                else {
                    cells.get(x).add(new Cell(heights.get(x).get(y), x, y));
                }
            }
        }
        
        cells = this.setPieces(cells);
        
        for (int x = 0; x < ISLAND_SIZE + 2; x++) {
            cells.get(x).get(0).top = cells.get(x).get(0);
            cells.get(x).get(ISLAND_SIZE + 1).bottom = cells.get(x)
                    .get(ISLAND_SIZE + 1);
        }
        for (int y = 0; y < ISLAND_SIZE + 2; y++) {
            cells.get(0).get(y).left = cells.get(0).get(y);
            cells.get(ISLAND_SIZE + 1).get(y).right = cells.get(ISLAND_SIZE + 1).get(y);
        }
        cells.get(0).get(0).left = cells.get(0).get(0);
        cells.get(0).get(0).top = cells.get(0).get(0);
        cells.get(0).get(ISLAND_SIZE + 1).left = cells.get(0).get(ISLAND_SIZE + 1);
        cells.get(0).get(ISLAND_SIZE + 1).bottom = cells.get(0).get(ISLAND_SIZE + 1);
        cells.get(ISLAND_SIZE + 1).get(0).right = cells.get(ISLAND_SIZE + 1).get(0);
        cells.get(ISLAND_SIZE + 1).get(0).top = cells.get(ISLAND_SIZE + 1).get(0);
        cells.get(ISLAND_SIZE + 1).get(ISLAND_SIZE + 1).bottom = cells.get(ISLAND_SIZE + 1)
                .get(ISLAND_SIZE + 1);
        cells.get(ISLAND_SIZE + 1).get(ISLAND_SIZE + 1).right = cells.get(ISLAND_SIZE + 1)
                .get(ISLAND_SIZE + 1);
        for (int x = 1; x < ISLAND_SIZE + 1; x++) {
            for (int y = 1; y < ISLAND_SIZE + 1; y++) {
                cells.get(x).get(y).left = cells.get(x - 1).get(y);
                cells.get(x).get(y).top = cells.get(x).get(y - 1);
                cells.get(x).get(y).right = cells.get(x + 1).get(y);
                cells.get(x).get(y).bottom = cells.get(x).get(y + 1);
            }
        }
        IList<Cell> result = new Mt<Cell>();
        for (int x = 0; x < ISLAND_SIZE + 2; x++) {
            for (int y = 0; y < ISLAND_SIZE + 2; y++) {
                result = new Cons<Cell>(cells.get(x).get(y), result);
            }
        }
        return result;
    }
    
    // creates a random terrain
    IList<Cell> makeTerrain() {
        for (int x = 0; x < ISLAND_SIZE + 2; x++) {
            heights.add(new ArrayList<Double>());
            for (int y = 0; y < ISLAND_SIZE + 2; y++) {
                heights.get(x).add(0.00);
            }
        }
        
        
        
        int center = ((ISLAND_SIZE / 2) + 1);
        heights.get(center).set(center, MAX_HEIGHT);
        heights.get(0).set(center, 1.0);
        heights.get(center).set(0, 1.0);
        heights.get(ISLAND_SIZE + 1).set(center, 1.0);
        heights.get(center).set(ISLAND_SIZE + 1, 1.0);
        
        this.terrainHelper(heights, 0, 0, center, center);
        this.terrainHelper(heights, center, 0, ISLAND_SIZE + 1, center);
        this.terrainHelper(heights, 0, center, center, ISLAND_SIZE + 1);
        this.terrainHelper(heights, center, center, ISLAND_SIZE + 1, ISLAND_SIZE + 1);
        
        for (int x = 0; x < ISLAND_SIZE + 2; x++) {
            cells.add(new ArrayList<Cell>());
            for (int y = 0; y < ISLAND_SIZE + 2; y++) {
                
                if (heights.get(x).get(y) <= 0) {
                    cells.get(x).add(new OceanCell(0, x, y));
                }
                else {
                    cells.get(x).add(new Cell(heights.get(x).get(y), x, y));
                }
            }
        }
        
        cells = this.setPieces(cells);
        
        for (int x = 1; x < ISLAND_SIZE; x++) {
            cells.get(x).get(1).top = cells.get(x).get(1);
            cells.get(x).get(ISLAND_SIZE + 1).bottom = cells.get(x)
                    .get(ISLAND_SIZE + 1);
            cells.get(x).get(1).bottom = cells.get(x).get(2);
            cells.get(x).get(1).left = cells.get(x - 1).get(1);
            cells.get(x).get(1).right = cells.get(x + 1).get(1);
            cells.get(x).get(ISLAND_SIZE + 1).top = (cells.get(x).get(ISLAND_SIZE));
            cells.get(x).get(ISLAND_SIZE + 1).left = (cells.get(x - 1).get(ISLAND_SIZE + 1));
            cells.get(x).get(ISLAND_SIZE + 1).right = (cells.get(x + 1).get(ISLAND_SIZE + 1));
        }
        for (int y = 1; y < ISLAND_SIZE; y++) {
            cells.get(1).get(y).left = cells.get(1).get(y);
            cells.get(ISLAND_SIZE + 1).get(y).right = cells.get(ISLAND_SIZE + 1).get(y);
            //cells.get(ISLAND_SIZE + 1).get(index)
        }
        cells.get(0).get(0).left = cells.get(0).get(0);
        cells.get(0).get(0).top = cells.get(0).get(0);
        cells.get(0).get(ISLAND_SIZE + 1).left = cells.get(0).get(ISLAND_SIZE + 1);
        cells.get(0).get(ISLAND_SIZE + 1).bottom = cells.get(0).get(ISLAND_SIZE + 1);
        cells.get(ISLAND_SIZE + 1).get(0).right = cells.get(ISLAND_SIZE + 1).get(0);
        cells.get(ISLAND_SIZE + 1).get(0).top = cells.get(ISLAND_SIZE + 1).get(0);
        cells.get(ISLAND_SIZE + 1).get(ISLAND_SIZE + 1).bottom = cells.get(ISLAND_SIZE + 1)
                .get(ISLAND_SIZE + 1);
        cells.get(ISLAND_SIZE + 1).get(ISLAND_SIZE + 1).right = cells.get(ISLAND_SIZE + 1)
                .get(ISLAND_SIZE + 1);
        for (int x = 2; x < ISLAND_SIZE + 1; x++) {
            for (int y = 2; y < ISLAND_SIZE + 1; y++) {
                cells.get(x).get(y).left = cells.get(x - 1).get(y);
                cells.get(x).get(y).top = cells.get(x).get(y - 1);
                cells.get(x).get(y).right = cells.get(x + 1).get(y);
                cells.get(x).get(y).bottom = cells.get(x).get(y + 1);
            }
        }
        IList<Cell> result = new Mt<Cell>();
        for (int x = 0; x < ISLAND_SIZE + 2; x++) {
            for (int y = 0; y < ISLAND_SIZE + 2; y++) {
                result = new Cons<Cell>(cells.get(x).get(y), result);
            }
        }
        return result;
    }
    
    void terrainHelper(ArrayList<ArrayList<Double>> heights, int x1, int y1, int x2, int y2) {
        if (x2 - x1 >= 2 || y2 - y1 >= 2) {
            
            double tl = heights.get(x1).get(y1);
            double tr = heights.get(x2).get(y1);
            double bl = heights.get(x1).get(y2);
            double br = heights.get(x2).get(y2);
            double key = ((x2 - x1) * (y2 - y1) * 0.03);
            
            double t = (Math.random() - 0.5) * key + ((tl + tr) / 2);
            heights.get((x1 + x2) / 2).set(y1, t);
            double b = (Math.random() - 0.5) * key + ((bl + br) / 2);
            heights.get((x1 + x2) / 2).set(y2, b);
            double l = (Math.random() - 0.5) * key + ((tl + bl) / 2);
            heights.get(x1).set((y1 + y2) / 2, l);
            double r = (Math.random() - 0.5) * key + ((tr + br) / 2);
            heights.get(x2).set((y1 + y2) / 2, r);
            double m = (Math.random() - 0.5) * key + ((tl + tr + bl + br) / 4);
            heights.get((x1 + x2) / 2).set((y1 + y2) / 2, m);
            
            this.terrainHelper(heights, x1, y1, (x1 + x2) / 2, (y1 + y2) / 2);
            this.terrainHelper(heights, (x1 + x2) / 2, y1, x2, (y1 + y2) / 2);
            this.terrainHelper(heights, x1, (y1 + y2) / 2, (x1 + x2) / 2, y2);
            this.terrainHelper(heights, (x1 + x2) / 2, (y1 + y2) / 2, x2, y2);

            }
        }
}

//represents an abstract list
interface IList<T> extends Iterable<T>{
    //adds an item T to the front of this list
    IList<T> add(T t);
    //gets the first item in the list
    T getStart();
    //allows an iterator to operate on this list
    Iterator<T> iterator();
    //determines if this list is a Cons
    boolean isCons();
    //returns this list as a Cons
    Cons<T> asCons();
}

//represents a Cons class
class Cons<T> implements IList<T> {
    T first;
    IList<T> rest;
    
    Cons(T first, IList<T> rest) {
        this.first = first;
        this.rest = rest;
    }
    

    //determines if this list is a Cons
    public boolean isCons() {
        return true;
    }
    
    //returns this list as a Cons
    public Cons<T> asCons() {
        return this;
    }
    
    //adds an item T to the front of this list
    public IList<T> add(T t) {
        return new Cons<T>(t, new Cons<T>(first, rest));
    }
    
    //gets the first item in the list that is not an ocean cell
    public T getStart() {
        if (!(this.first instanceof OceanCell)) {
            return this.first;
        }
        return this.rest.getStart();
    }
    
    //makes this list iterable
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }
}

//represents an empty list
class Mt<T> implements IList<T> {
    
    //determines if the list is a Cons
    public boolean isCons() {
        return false;
    }
    //returns null, empty list cannot be returned as Cons
    public Cons<T> asCons() {
        return null;
    }
    
    //adds an item T to the front of this list
    public IList<T> add(T t) {
        return new Cons<T>(t, new Mt<T>());
    }
    
    //makes this list iterable
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }
    
    //returns null, there is no first item in an empty list
    public T getStart() {
        return null;
    }

}

//represents an IListIterator
class IListIterator<T> implements Iterator<T> {
    IList<T> list;
    
    IListIterator(IList<T> list) {
        this.list = list;
    }
    
    //determines whether an IList has a next item
    public boolean hasNext() {
        return this.list.isCons();
    }
    //returns the next item in the list
    public T next() {
        Cons<T> listAsCons = this.list.asCons();
        T result = listAsCons.first;
        this.list = listAsCons.rest;
        return result;
    }
    
}

//Examples class
class ForbiddenIslandExamples {
    static final int ISLAND_SIZE = 64;
    static final double MAX_HEIGHT = 31;
    static final int NUMBER_OF_TARGETS = 3;
    Cell celltop = new Cell(10, 10, 10);
    Cell cellbottom = new Cell(9, 9, 9);
    Cell cellright = new Cell(8, 8, 8);
    Cell cellleft = new Cell(7, 7, 7);
    Cell cell = new Cell(10, 9, 8, cellleft, celltop, cellright, cellbottom, false, false, false);
    Cell cell1 = new Cell(9, 9, 9, cellleft, celltop, cellright, cellbottom, false, true, false);
    Cell cell2 = new Cell(9, 8, 8, celltop, cellleft, cellright, cellbottom, true, true, false);
    Target target1 = new Target(cell1);
    HelicopterTarget heli = new HelicopterTarget(cell2);
    
    IList<Cell> mt = new Mt<Cell>();
    IList<Cell> cells = new Cons<Cell>(cell1, new Cons<Cell>(cell2, mt));
    Player player1 = new Player(cell, 10);
    
    //tests the method MovePlayer
    boolean testMovePlayer(Tester t) {
        return t.checkExpect(player1.movePlayer("right"), new Player(cellright, 10, 1)) &&
                t.checkExpect(player1.movePlayer("left"), new Player(cellleft, 10, 2)) &&
                t.checkExpect(player1.movePlayer("up"), new Player(celltop, 10, 3)) &&
                t.checkExpect(player1.movePlayer("down"), new Player(cellbottom, 10, 4));
    }
    
    //tests the method GetColor
    boolean testGetColor(Tester t) {
        return t.checkExpect(cell.getColor(10, 10), new Color(63, 181, 63)) &&
                t.checkExpect(cell1.getColor(10, 10), new Color(81, 189, 75)) &&
                t.checkExpect(cell2.getColor(10, 10), new Color(0, 21, 184));
    }
    
    //tests the method SameCell
    boolean testSameCell(Tester t) {
        return t.checkExpect(cell1.sameCell(cell2), false) &&
                t.checkExpect(cell2.sameCell(cellright), true);
    }
    
    //tests the method IsHeli
    boolean testIsHeli(Tester t) {
        return t.checkExpect(target1.isHeli(), false) &&
                t.checkExpect(heli.isHeli(), true);
    }
    
    //tests the method IsCons
    boolean testIsCons(Tester t) {
        return t.checkExpect(cells.isCons(), true) &&
                t.checkExpect(mt.isCons(), false);
    }
    
    //tests the method Add
    boolean testAdd(Tester t) {
        return t.checkExpect(cells.add(cell), new Cons<Cell>(cell, cells)) &&
                t.checkExpect(mt.add(cell), new Cons<Cell>(cell, mt));
    }
    
    boolean testAsCons(Tester t) {
        return t.checkExpect(cells.asCons(), cells) &&
                t.checkExpect(mt.asCons(), null);
    }
    
    boolean testPlayerImage(Tester t) {
        return t.checkExpect(player1.playerImage(), new DiskImage(new Posn(85,
                75), 5, new Red()));
    }
    /*
    void testRun(Tester t) {
        ForbiddenIslandWorld fiw1 = new ForbiddenIslandWorld();
        fiw1.bigBang((ISLAND_SIZE * 10) + 10, ((ISLAND_SIZE * 10) + 10), 0.3);
    }

    */
    
    
}