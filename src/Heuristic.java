
public class Heuristic {

    //The following coefficients needs to be found by experimenting
    private static double stonesInStoreCoefficient = 2;
    private static double stonesInHolesCoefficient = 0.5;
    private static double freeTurnMovesCoefficient = 0.5;
    private static double maxStealSeedMoveCoefficient = 0.5;

    // Every function ending with "Diff" are used to make heuristics. Actual heuristic in the bottom
    private static int stonesInStoreDiff(Board board, Side side){
        int stonesInOwnStore = board.getSeedsInStore(side);
        int stonesInOppStore = board.getSeedsInStore(side.opposite());

        return (stonesInOwnStore - stonesInOppStore);
    }

    private static int stonesInHolesDiff(Board board, Side side) {
        int stonesInOwnHoles = numberOfStonesInHoles(board, side);
        int stonesInOppHoles = numberOfStonesInHoles(board, side.opposite());
        return (stonesInOwnHoles - stonesInOppHoles);
    }

    private static int numberOfStonesInHoles(Board board, Side side){
        int stones = 0;
        for(int hole = 1; hole <= board.getNoOfHoles(); hole++){
            stones += board.getSeeds(side, hole);
        }
        return stones;
    }

    private static int freeTurnMovesDiff(Board board, Side side){
        int ownFreeTurnMoves = countFreeMoves(board, side);
        int oppFreeTurnMoves = countFreeMoves(board, side.opposite());
        return ownFreeTurnMoves - oppFreeTurnMoves;
    }

    private static int countFreeMoves(Board board, Side side){
        int count = 0;
        for(int hole = 1; hole <= board.getNoOfHoles(); hole++){
            if(MoveClassifier.isFreeTurnMove(board, side, hole)){
                count++;
            }
        }
        return count;
    }

    private static int maxStealMoveDiff(Board board, Side side){
        int ownMaxStealMove = maxStealMove(board, side);
        int oppMaxStealMove = maxStealMove(board, side.opposite());
        return ownMaxStealMove - oppMaxStealMove;
    }

    private static int maxStealMove(Board board, Side side){
        int maxSteal = 0;
        for(int hole = 1; hole <= board.getNoOfHoles(); hole++){
            if(MoveClassifier.isStealSeedsMove(board, side, hole)){
                int endHole = MoveClassifier.findEndHole(board, side, hole);
                int stealValue = board.getSeeds(side.opposite(), endHole);
                if (stealValue > maxSteal){
                    maxSteal = stealValue;
                }
            }
        }
        return maxSteal;
    }

    //TODO implement function that return coefficient based on how far we have reached in the game.
    private static double getStonesInStoreCoefficient(){
        return stonesInStoreCoefficient;
    }

    public static double simpleHeuristic(Board board, Side side){
        return stonesInStoreCoefficient * stonesInStoreDiff(board, side)
                + stonesInHolesCoefficient * stonesInHolesDiff(board, side);
    }

    //TODO confirm that Double.POSITIVE_INFINITY works with double as return variable
    public static double advancedHeuristic(Board board, Side side){
        if (Kalah.gameOver(board)){
            if(board.getSeedsInStore(side) > board.getSeedsInStore(side.opposite())){
                return Double.POSITIVE_INFINITY;
            }
            return Double.NEGATIVE_INFINITY;
        }
        return stonesInStoreCoefficient * stonesInStoreDiff(board, side)
                + stonesInHolesCoefficient * stonesInHolesDiff(board, side)
                + freeTurnMovesCoefficient * freeTurnMovesDiff(board, side)
                + maxStealSeedMoveCoefficient * maxStealMoveDiff(board, side);
    }
}