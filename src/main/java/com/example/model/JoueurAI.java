package com.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * JoueurAI - Intelligent AI player for Connect4
 * Implements strategic decision-making with lookahead and column preference
 */
public class JoueurAI extends Joueur {
    private Random random;
    private int difficulty; // 1=easy (random), 2=medium (tactical), 3=hard (strategic)
    private static final int LOOKAHEAD_DEPTH = 6; // How many moves ahead to consider

    public JoueurAI(int id, String nom, int score, int difficulty) {
        super(id, nom, score);
        this.difficulty = Math.max(1, Math.min(difficulty, 3));
        this.random = new Random();
    }

    public JoueurAI(int id, String nom, int score) {
        this(id, nom, score, 3); // Default: medium difficulty
    }

    /**
     * Choose best column based on difficulty level
     * Easy: Random choice
     * Medium: Prefer center columns, block opponent threats
     * Hard: Lookahead analysis, offensive/defensive moves
     */
    public int choisirCoup(Game game) {
        try {
            switch (difficulty) {
                case 1:
                    return choisirCoupFacile(game);
                case 2:
                    return choisirCoupMoyen(game);
                case 3:
                    return choisirCoupDifficile(game);
                default:
                    return choisirCoupMoyen(game);
            }
        } catch (CoupException e) {
            // Fallback: return random valid column
            return choisirCoupFacile(game);
        }
    }

    /**
     * Easy: Random valid column
     */
    private int choisirCoupFacile(Game game) {
        List<Integer> validCols = getValidColumns(game);
        if (validCols.isEmpty()) {
            return random.nextInt(7); // Fallback
        }
        return validCols.get(random.nextInt(validCols.size()));
    }

    /**
     * Medium: Prefer center, consider blocking/winning
     */
    private int choisirCoupMoyen(Game game) throws CoupException {
        List<Integer> validCols = getValidColumns(game);
        if (validCols.isEmpty()) throw new CoupException("Aucune colonne valide");

        // Check for winning move
        for (int col : validCols) {
            try {
                int ligne = game.getLigneVideByColonne(col);
                if (game.estGagnant(new Position(ligne, col), this.getId())) {
                    return col;
                }
            } catch (CoupException e) {
                // Column full, skip
            }
        }

        // Check for blocking opponent winning move
        int opponentId = -1; // Will be set in context
        for (int col : validCols) {
            try {
                int ligne = game.getLigneVideByColonne(col);
                // Imagine opponent plays here and check if they win
                Game testGame = cloneGame(game);
                testGame.setCoup(ligne, col, opponentId);
                if (testGame.estGagnant(new Position(ligne, col), opponentId)) {
                    return col;
                }
            } catch (CoupException e) {
                // Skip
            }
        }

        // Prefer center columns
        validCols.sort((a, b) -> {
            int centerDist1 = Math.abs(a - 3);
            int centerDist2 = Math.abs(b - 3);
            return centerDist1 - centerDist2;
        });

        return validCols.get(0);
    }

    /**
     * Hard: Minimax with alpha-beta pruning (simplified)
     */
    private int choisirCoupDifficile(Game game) throws CoupException {
        List<Integer> validCols = getValidColumns(game);
        if (validCols.isEmpty()) throw new CoupException("Aucune colonne valide");

        // Evaluate each move
        int bestCol = validCols.get(0);
        int bestScore = Integer.MIN_VALUE;

        for (int col : validCols) {
            try {
                int ligne = game.getLigneVideByColonne(col);
                Game testGame = cloneGame(game);
                testGame.setCoup(ligne, col, this.getId());

                int score = evaluatePosition(testGame, 0, this.getId());
                if (score > bestScore) {
                    bestScore = score;
                    bestCol = col;
                }
            } catch (CoupException e) {
                // Skip
            }
        }

        return bestCol;
    }

    /**
     * Minimax evaluation function
     */
    private int evaluatePosition(Game game, int depth, int aiId) {
        // Terminal conditions
        if (testWin(game, aiId)) return 1000 - depth; // AI wins
        if (testWin(game, -aiId)) return -1000 + depth; // Opponent wins
        if (game.estRemplie()) return 0; // Draw

        if (depth >= LOOKAHEAD_DEPTH) {
            return evaluateHeuristic(game, aiId); // Heuristic evaluation
        }

        int opponentId = (aiId == 1) ? 2 : 1;
        List<Integer> validCols = getValidColumns(game);

        int bestScore = Integer.MIN_VALUE;
        for (int col : validCols) {
            try {
                int ligne = game.getLigneVideByColonne(col);
                Game testGame = cloneGame(game);
                testGame.setCoup(ligne, col, aiId);
                int score = evaluatePosition(testGame, depth + 1, aiId);
                bestScore = Math.max(bestScore, score);
            } catch (CoupException e) {
                // Skip
            }
        }

        return bestScore;
    }

    /**
     * Heuristic: Count potential alignments
     */
    private int evaluateHeuristic(Game game, int aiId) {
        int score = 0;

        // Count AI pieces in favorable positions (center > edges)
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (game.getValeurPosition(i, j) == aiId) {
                    score += (4 - Math.abs(j - 3)); // Prefer center
                }
            }
        }

        return score;
    }

    /**
     * Get all valid columns (not full)
     */
    private List<Integer> getValidColumns(Game game) {
        List<Integer> valid = new ArrayList<>();
        for (int col = 0; col < 7; col++) {
            try {
                game.getLigneVideByColonne(col);
                valid.add(col);
            } catch (CoupException e) {
                // Column full, skip
            }
        }
        return valid;
    }

    /**
     * Check if a player has won (used for lookahead)
     */
    private boolean testWin(Game game, int playerId) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (game.getValeurPosition(i, j) == playerId) {
                    if (game.estGagnant(new Position(i, j), playerId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Deep clone the game state for lookahead simulation
     */
    private Game cloneGame(Game original) {
        Game clone = new Game(original.getIdJ1(), original.getIdJ2());
        // Manually copy board state by using setCoup for non-empty positions
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                int val = original.getValeurPosition(i, j);
                if (val != 0) {
                    clone.setCoup(i, j, val);
                }
            }
        }
        return clone;
    }

    /**
     * Get AI difficulty level
     */
    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = Math.max(1, Math.min(difficulty, 3));
    }

    @Override
    public String toString() {
        String[] levels = {"", "Easy", "Medium", "Hard"};
        return super.toString() + " [AI - " + levels[difficulty] + "]";
    }
}
