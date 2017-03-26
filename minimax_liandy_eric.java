import java.util.HashMap;
/**
* 5 | 35 36 37 38 39 40 41
* 4 | 28 29 30 31 32 33 34
* 3 | 21 22 23 24 25 26 27
* 2 | 14 15 16 17 18 19 20
* 1 |  7  8  9 10 11 12 13
* 0 |  0  1  2  3  4  5  6
*   -----------------------
*      0  1  2  3  4  5  6
* @author Liandy Hardikoesoemo, Eric Du
**/

public class minimax_liandy_eric extends AIModule
{


  private int myAI;
  private int optMove = -1;
  private static final int CUTOFF = 5;  // cutoff > 5 will exceed the allowed 500ms turn time
  private int[] branchOrdering = {3, 2, 4, 1, 5, 0, 6};
  private static final int POSSIBLE_WIN_CONDITIONS = 69;

  // The 69 winning combinations. ( ͡° ͜ʖ ͡°)
  private int[][] fourInARow = {
  // Horizontal win conditions
  { 0, 1, 2,3 },
  {1,2,3,4},
  {2,3,4,5},
  {3,4,5,6},

  {7,8,9,10},
  {8,9,10,11},
  {9,10,11,12},
  {10,11,12,13},

  {14,15,16,17},
  {15,16,17,18},
  {16,17,18,19},
  {17,18,19,20},

  {21,22,23,24},
  {22,23,24,25},
  {23,24,25,26},
  {24,25,26,27},

  {28,29,30,31},
  {29,30,31,32},
  {30,31,32,33},
  {31,32,33,34},

  {35,36,37,38},
  {36,37,38,39},
  {37,38,39,40},
  {38,39,40,41},

  // Vertical win conditions
  {0,7,14,21},
  {7,14,21,28},
  {14,21,28,35},

  {1,8,15,22},
  {8,15,22,29},
  {15,22,29,36},

  {2,9,16,23},
  {9,16,23,30},
  {16,23,30,37},

  {3,10,17,24},
  {10,17,24,31},
  {17,24,31,38},

  {4,11,18,25},
  {11,18,25,32},
  {18,25,32,39},

  {5,12,19,26},
  {12,19,26,33},
  {19,26,33,40},

  {6,13,20,27},
  {13,20,27,34},
  {20,27,34,41},

  // Right diagonals (/)
  {14,22,30,38},
  {7,15,23,31},
  {15,23,31,39},
  {0,8,16,24},
  {8,16,24,32},
  {16,24,32,40},
  {1,9,17,25},
  {9,17,25,33},
  {17,25,33,41},
  {2,10,18,26},
  {10,18,26,34},
  {3,11,19,27},

  // Left diagonals (\)
  {3,9,15,21},
  {4,10,16,22},
  {10,16,22,28},
  {5,11,17,23},
  {11,17,23,29},
  {17,23,29,35},
  {6,12,18,24},
  {12,18,24,30},
  {18,24,30,36},
  {13,19,25,31},
  {19,25,31,37},
  {20,26,32,38}
};
  private int[][] heuristicTable = {
    {     0,   -10,  -100, -1000, -10000 },
    {    10,     0,     0,     0 },
    {   100,     0,     0,     0 },
    {  1000,     0,     0,     0 },
    { 10000,     0,     0,     0 }
  };

  HashMap<Integer, Integer> rowLookUpTable;
  HashMap<Integer, Integer> colLookupTable;

  // Key = Cell number
  // Value = Row number
  private void buildRowLookupTable(){
		rowLookUpTable = new HashMap<Integer, Integer>() {{
    put(0,0);put(1,0);put(2,0);put(3,0);put(4,0);put(5,0);put(6,0);
		put(7,1);put(8,1);put(9,1);put(10,1);put(11,1);put(12,1);put(13,1);
		put(14,2);put(15,2);put(16,2);put(17,2);put(18,2);put(19,2);put(20,2);
		put(21,3);put(22,3);put(23,3);put(24,3);put(25,3);put(26,3);put(27,3);
		put(28,4);put(29,4);put(30,4);put(31,4);put(32,4);put(33,4);put(34,4);
		put(35,5);put(36,5);put(37,5);put(38,5);put(39,5);put(40,5);put(41,5);
		put(42,6);put(43,6);put(44,6);put(45,6);put(46,6);put(47,6);put(48,6);
		}};
	}

  // Key = Cell number
  // Value = Col number
  private void buildColLookupTable(){
		colLookupTable = new HashMap<Integer, Integer>() {{
    put(0,0);put(1,1);put(2,2);put(3,3);put(4,4);put(5,5);put(6,6);
		put(7,0);put(8,1);put(9,2);put(10,3);put(11,4);put(12,5);put(13,6);
		put(14,0);put(15,1);put(16,2);put(17,3);put(18,4);put(19,5);put(20,6);
		put(21,0);put(22,1);put(23,2);put(24,3);put(25,4);put(26,5);put(27,6);
		put(28,0);put(29,1);put(30,2);put(31,3);put(32,4);put(33,5);put(34,6);
		put(35,0);put(36,1);put(37,2);put(38,3);put(39,4);put(40,5);put(41,6);
		put(42,0);put(43,1);put(44,2);put(45,3);put(46,4);put(47,5);put(48,6);
		}};
	}

	public void getNextMove(final GameStateModule state)
	{
    buildRowLookupTable();
    buildColLookupTable();
    myAI = state.getActivePlayer();

		minimax(state, 0, true);
    chosenMove = optMove;
	}

  private int getPlayerAtCell(final GameStateModule state, int row, int col){
    return state.getAt(row,col);
  }

	private int minimax(final GameStateModule state, int depth, boolean playerIsMax)
	{
		if(terminate){
      System.out.println("Exceeds 500ms");
			return 0;
		}

		if(depth == CUTOFF){
			//System.out.println("Cutoff reached.");
			return evaluationFunction(state);
		}

		int val = 0;
		depth++;

		if(playerIsMax){	// pick max
			int max = Integer.MIN_VALUE;
			//  System.out.println("player is Max");
			// System.out.println("Expanding node: ");
			for(int i : branchOrdering) {
				if(state.canMakeMove(i)){
					// System.out.println(i + " is " + val);
					state.makeMove(i);
					val = minimax(state, depth, false);	// now recurse to the Min player.

					if(val > max){	// >= or > ?
						// System.out.println(val + " is greater than " + max);
						max = val;

						if(depth == 1){

							optMove = i;
              // System.out.println("optMove is now " + optMove);
						}
					}
					state.unMakeMove();
				}
			}
			return max;

		} else {	// pick min
			int min = Integer.MAX_VALUE;
			// System.out.println("player is Min");
			for(int i : branchOrdering){
				// System.out.println(i + " is " + val);
				if(state.canMakeMove(i)){
					state.makeMove(i);
					val = minimax(state, depth, true);
					// System.out.println(i + "is" + val);
					if(val < min){
						// System.out.println(val + " is smaller than " + min);

						min = val;
					}
					state.unMakeMove();
				}
			}
			return min;
		}
	}

	public int evaluationFunction(final GameStateModule state)
	{
    int value = 0;
    int oppAI = (myAI == 1) ? 2 : 1;

    for(int i = 0; i < POSSIBLE_WIN_CONDITIONS; i++) {
      int myAIPiece = 0;
      int oppAIPiece = 0;
      for(int j = 0; j <4; j++) {
        int currPiece = state.getAt(rowLookUpTable.get(fourInARow[i][j]), colLookupTable.get(fourInARow[i][j]));
        if(currPiece == myAI){
          myAIPiece++;
        } else if(currPiece == oppAI) {
          oppAIPiece++;
        }
      }
      value += heuristicTable[myAIPiece][oppAIPiece];
    }

    return value;
	}
}
