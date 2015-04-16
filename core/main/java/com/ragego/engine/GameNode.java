package com.ragego.engine;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * State of the game.
 * A node is a description of an action which can have a parent and children. The sum of parents and this node describe
 * a state for the game. Each node has a GameBoard attached and an array of integers representing the board on this node.
 * This representation makes easier to understand that Go Game is a sum of action which create a state.
 * This class has useful functions to manipulates the game flow. But for analysis, you should use {@link GameBoard}.
 *
 * @author Philippe Vienne
 * @since 1.0
 */
public class GameNode {

    /**
     * Board attached to this node.
     * Board where this action has been played.
     */
    private GameBoard board;

    /**
     * Raw data from board state on this node
     */
    private int[][] rawData;
    /**
     * Describe if this node is loked.
     * A locked node is unable to refresh the hash from GameBoard. It prevent manipulation of history.
     */
    private boolean locked = false;
    /**
     * Label for this node.
     */
    private String label;
    /**
     * The action on this node.
     */
    private Action action;
    /**
     * Intersection where the action is.
     * If the action is not on a specific intersection, this value is null.
     */
    private Intersection intersection;
    /**
     * Player acting for this node.
     */
    private Player player;
    /**
     * Represent the {@link GameBoard} as a MD5 hash.
     * We consider that MD5 algorithm is too complex to make (19*19)^3 solutions different.
     */
    private String boardHash;
    /**
     * The parent node.
     * This node contains the previous state. If there is not one (null value), consider we are at game start point.
     */
    private GameNode parent = null;
    /**
     * Children state.
     * From one state, you can make many actions, if we represent that, we have children. Normaly, a node has only
     * one child when you are playing.
     */
    private ArrayList<GameNode> children = new ArrayList<GameNode>();
    /**
     * Property for this node.
     * You can set properties to a Game node. This is usefull for the main game node to set information data. This
     * data use code from SGF format.
     */
    private HashMap<String, String> properties = new HashMap<String, String>();

    /**
     * Create a node from a given Game board.
     *
     * @param board        the board where we are playing
     * @param parent       The parent node for this node (could be null)
     * @param action       The action performed by this node (could not be null)
     * @param intersection Where the action is performed (could be null if action is not PUT_STONE)
     * @param player       The player acting on this action (could be null)
     */
    public GameNode(GameBoard board, GameNode parent, GameNode.Action action, Intersection intersection, Player player) {
        if (board == null)
            throw new IllegalArgumentException("Board can not be null");
        boardHash = board.getBoardHash();
        this.rawData = board.getRepresentation();
        this.action = action;
        this.intersection = intersection;
        this.player = player;
        this.board = board;
    }

    /**
     * Create a node from a given Game board.
     *
     * @param board  the board where we are playing
     * @param parent The parent node for this node (could be null)
     * @param action The action performed by this node (could not be null)
     * @param player The player acting on this action (could be null)
     */
    public GameNode(GameBoard board, GameNode parent, GameNode.Action action, Player player) {
        this(board, parent, action, null, player);
    }

    /**
     * Create a node from a given Game board.
     *
     * @param board  the board where we are playing
     * @param parent The parent node for this node (could be null)
     * @param action The action performed by this node (could not be null)
     */
    public GameNode(GameBoard board, GameNode parent, GameNode.Action action) {
        this(board, parent, action, null, null);
    }

    /**
     * Create a node from a given Game board.
     *
     * @param board  the board where we are playing
     * @param action The action performed by this node
     */
    public GameNode(GameBoard board, Action action) {
        this(board, null, action, null, null);
    }

    public String getLabel() {
        return label;
    }

    public void addLabel(String label) {
        this.label = label;
    }

    /**
     * Retrieve state of board on this node.
     *
     * @return Double coordinate array.
     */
    public int[][] getRawData() {
        return rawData;
    }

    public void addSetup(Player player, Intersection intersection) {

    }

    public Intersection getIntersection() {
        return intersection;
    }

    public void setIntersection(Intersection intersection) {
        this.intersection = intersection;
    }

    public GameBoard getBoard() {
        return board;
    }

    public void setBoard(GameBoard board) {
        this.board = board;
        this.rawData = board.getRepresentation();
    }

    /**
     * Ask to refresh the hash from the GameBoard.
     */
    public void recomputeHash() {
        if (!locked)
            boardHash = board.getBoardHash();
    }

    /**
     * Lock the hash of the node.
     */
    public void lock() {
        locked = true;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getHashString() {
        return boardHash;
    }

    public Stone getStone() {
        if (intersection == null) return null;
        return new Stone(intersection, player);
    }

    /**
     * Remove a child from this node
     *
     * @param node The node to remove
     */
    public void removeChild(GameNode node) {
        children.remove(node);
    }

    /**
     * Retrieve the parent node.
     * If there is not one (null value), consider we are at game start point.
     *
     * @return The parent node.
     */
    public GameNode getParent() {
        return parent;
    }

    /**
     * Set a new parent to this node.
     * Before setting gameNode as parent, check if this instance is a child of it. If not, add this instance as child.
     * Normally you should call this function only one time.
     *
     * @param gameNode The GameNode to set as parent (should not be null)
     */
    public void setParent(GameNode gameNode) {
        if (gameNode == null) throw new IllegalArgumentException("GameNode is null, stopping before we fall all");
        if (!gameNode.hasChild(this)) {
            gameNode.addChild(this);
        }
        parent = gameNode;
    }

    /**
     * Add a new child to this node.
     * When you have an action to add to this state, simply add a child.
     * After registering gameNode as child, check if this instance is his parent.
     *
     * @param gameNode Node to add to this one.
     */
    public void addChild(GameNode gameNode) {
        if (gameNode == null) throw new IllegalArgumentException("GameNode is null, stopping before we fall all");
        //if (gameNode.hasParent() && gameNode.getParent() != this)
        //    throw new IllegalStateException("This node is already a child of another. Are you making a rape?");
        children.add(gameNode);
        if (!gameNode.isParent(this))
            gameNode.setParent(this);
    }

    /**
     * Check if a node is parent of this instance.
     *
     * @param gameNode The supposed parent
     * @return true if it's the parent
     */
    public boolean isParent(GameNode gameNode) {
        return parent == gameNode;
    }

    /**
     * Check if this Node has a child
     *
     * @param gameNode The supposed child
     * @return true if it's a child of this instance.
     */
    public boolean hasChild(GameNode gameNode) {
        return children.contains(gameNode);
    }

    /**
     * Retrieve all children.
     *
     * @return An array of children of this node.
     */
    public GameNode[] getChildren() {
        return children.toArray(new GameNode[children.size()]);
    }

    /**
     * Check if we are not violating the KO rule.
     * Note if the current action is Pass, it execute this on the next parent who has not passed.
     *
     * @return true if we are violating KO rule.
     */
    public boolean isMakingKO() {
        if (!hasParent()) return false;
        GameNode parent = getParent();
        if (action != Action.PUT_STONE) {
            while (parent.hasParent() && parent.action != Action.PUT_STONE) {
                parent = parent.getParent();
            }
        }
        while (parent != null) {
            if (!parent.locked) {
                if (!parent.hasParent() && parent.action != Action.PUT_STONE) // If it's end on PASS node, it's OK
                    return false;
                if (boardHash.equals(parent.boardHash) && parent.action == Action.PUT_STONE)
                    return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    /**
     * Get value of a property of this node.
     *
     * @return The value or null if there is not one.
     */
    public String getProperty(String key) {
        return properties.get(key);
    }

    /**
     * Set value as property of this node.
     *
     * @param key   The key used for this property.
     * @param value The stored value.
     * @return Previous value if there is one.
     */
    public String setProperty(String key, String value) {
        return properties.put(key, value);
    }

    /**
     * Check if this instance has parent.
     *
     * @return true if it's an orphan.
     */
    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Compare nodes to chech if they are the same.
     * Two nodes are identical if they has the same hash code.
     *
     * @param obj The object to compare.
     * @return True on identical GameNode.
     */
    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof GameNode) && (boardHash != null) && (((GameNode) obj).boardHash != null) && boardHash.equals(((GameNode) obj).boardHash));
    }

    public String toString() {
        return "[GameNode Action=" + action + ", Intersection=" + intersection + ", Player=" + player + "]";
    }

    public GameNode copy(GameBoard board) {
        return new GameNode(board, null, action, intersection == null ? null : Intersection.get(intersection.getColumn(), intersection.getLine(), board), player);
    }

    public boolean isLocked() {
        return locked;
    }

    /**
     * Describe actions possible in the game between two nodes.
     * Currently, the actions are :
     * <ul>
     * <li><b>START_GAME</b>: First node is a START_GAME, it defines a start for the node's tree.</li>
     * <li><b>PASS</b>: The player decide to not play his turn. Intersection data is null. KO rule not checkable.</li>
     * <li><b>PUT_STONE</b>: A player put a stone on the board. The data is after compute dead stones</li>
     * <li><b>IA_SPECIAL_ACTION</b>: (Not SGF standard) something strange happened and board is not the same</li>
     * </ul>
     */
    public enum Action {
        START_GAME,
        PASS,
        PUT_STONE,
        NOTHING, IA_SPECIAL_ACTION
    }
}
