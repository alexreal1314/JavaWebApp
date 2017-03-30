package Logic;

import Generated.GameDescriptor;
import javafx.beans.property.IntegerProperty;
import Generated.GameDescriptor.Board.Structure.Squares.Square;
import Generated.GameDescriptor.Board.Structure.Squares.Marker;

import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.math.BigInteger;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by alex on 19/11/2016.
 */
public class Board {
    public final int k_PlayerOne = 0;
    public final int k_PlayerTwo = 1;
    private int m_NumOfCols;
    private int m_NumOfRows;
    private int colX;
    private int rowX;
    private Cell[][] myBoard;
    private Game gameOfBoard;

    private Cell [][] boardCopy;
    private int colXCopy;
    private int rowXCopy;

    public void UpdateCell(int row, int col) {
        myBoard[row][col].SetNumInCell("X");
        myBoard[row][col].setCellStringColor(7);
    }

    public void eraseX() {

        myBoard[rowX - 1][colX - 1].SetNumInCell(" ");
    }

    public Cell[][] getCellBoard() {
        return myBoard;
    }

    public int NumOfRows() {
        return m_NumOfRows;
    }

    public int NumOfCols() {
        return m_NumOfCols;
    }

    public int getColX() {
        return colX;
    }

    public int getRowX() {
        return rowX;
    }

    public void setRowX(int newRowX) {
        rowX = newRowX;
    }

    public void setColX(int newColX) {
        colX = newColX;
    }

    public void initBasicRandomBoard(int from, int to) {

        colX = 1 + (int) (Math.random() * (m_NumOfRows));
        rowX = 1 + (int) (Math.random() * (m_NumOfCols));

        myBoard[rowX - 1][colX - 1].SetNumInCell("X");
        myBoard[rowX -1][colX -1].SetColor(Color.ORANGE);
        myBoard[rowX -1][colX -1].setCellStringColor(7);

        int num = Math.abs(to - from);
        if (from < 0)
            num++;

        int start = from;
        int RandomRow, RandomCol;
        boolean badCell = true;
        int k = ((m_NumOfRows * m_NumOfCols) - 1) / num;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < num; j++) {
                while (badCell) {
                    RandomRow = 1 + (int) (Math.random() * (m_NumOfRows));
                    RandomCol = 1 + (int) (Math.random() * (m_NumOfCols));
                    if (myBoard[RandomRow - 1][RandomCol - 1].GetNumInCell().equals(" ")) {
                        myBoard[RandomRow - 1][RandomCol - 1].SetNumInCell(Integer.toString(start));
                        badCell = false;
                    }
                }
                badCell = true;
                start++;
            }
            start = from;
        }

        createCopyBoard();
    }

    public Board(BigInteger rows, BigInteger cols, Game game) {
        m_NumOfCols = cols.intValue();
        m_NumOfRows = rows.intValue();
        gameOfBoard = game;

        myBoard = new Cell[m_NumOfRows][m_NumOfCols];

        for (int i = 0; i < m_NumOfRows; i++) {
            for (int j = 0; j < m_NumOfCols; j++) {
                myBoard[i][j] = new Cell(i, j, 5);
            }
        }
    }

    public boolean AdvancedCheckColRow() {
        int counterCol = 0;
        int counterRow = 0;

        for (int i = 0; i < m_NumOfCols; i++) {
            if (myBoard[i][colX - 1].GetNumInCell() == " " || myBoard[i][colX - 1].GetNumInCell() == "X") {
                counterCol++;
            }
        }

        for (int i = 0; i < m_NumOfCols; i++) {
            if (myBoard[rowX - 1][i].GetNumInCell() == " " || myBoard[rowX - 1][i].GetNumInCell() == "X") {
                counterRow++;
            }
        }

        if (counterRow == m_NumOfRows && counterCol == m_NumOfCols ) {
            return true;
        }
        return false;
    }

    public boolean BasicCheckColRow(int playerIndex) {
        int counter = 0;

        if (playerIndex == 0) {

            for (int i = 0; i < m_NumOfCols; i++) {
                if (myBoard[rowX - 1][i].GetNumInCell() == " " || myBoard[rowX - 1][i].GetNumInCell() == "X") {
                    counter++;
                }
            }

            if (counter == m_NumOfRows) {
                return false;
            }
            return true;
        }
        else {
            counter = 0;

            for (int i = 0; i < m_NumOfCols; i++) {
                if (myBoard[i][colX - 1].GetNumInCell() == " " || myBoard[i][colX - 1].GetNumInCell() == "X") {
                    counter++;
                }
            }

            if (counter == m_NumOfCols) {
                return false;
            }

            return true;
        }
    }

    public int compMoveBasicGame(int currentPlayer) {
        boolean goodNum = false;
        int num = 0;
        while (!goodNum) {
            num = 1 + (int) (Math.random() * m_NumOfRows);
            if (currentPlayer == k_PlayerOne) {
                if (myBoard[rowX - 1][num - 1].GetNumInCell() != "X" && myBoard[rowX - 1][num - 1].GetNumInCell() != " ") {
                    goodNum = true;
                }
            } else {
                if (myBoard[num - 1][colX - 1].GetNumInCell() != "X" && myBoard[num - 1][colX - 1].GetNumInCell() != " ") {
                    goodNum = true;
                }
            }
        }
        return num;
    }

    public List<Integer> compMoveAdvancedGame(int currentPlayer) {
        List<ArrayList<Integer>> tempList = new ArrayList<ArrayList<Integer>>();

        List<Integer> rowCol = new ArrayList<Integer>(2);
        int j=0;
        int index=0;
        for (int i=0; i<m_NumOfRows;i++){
            if (myBoard[i][colX-1].GetNumInCell()!="X" && myBoard[i][colX-1].GetNumInCell()!=" " && myBoard[i][colX-1].getCellStringColor().equals(gameOfBoard.getPlayersList().get(currentPlayer).getColorName())){
                tempList.add(new ArrayList<Integer>(2));
                tempList.get(j).add(0,i);
                tempList.get(j).add(1,colX-1);

                j++;
            }
        }
        for(int i=0; i<m_NumOfCols;i++){
            if (myBoard[rowX-1][i].GetNumInCell()!="X" && myBoard[rowX-1][i].GetNumInCell()!=" " && myBoard[rowX-1][i].getCellStringColor().equals(gameOfBoard.getPlayersList().get(currentPlayer).getColorName())){
                tempList.add(new ArrayList<Integer>(2));
                tempList.get(j).add(0, rowX-1);
                tempList.get(j).add(1, i);
                j++;
            }
        }
        index = (int)Math.random() * (tempList.size()-1);
        rowCol.add(tempList.get(index).get(0));
        rowCol.add(tempList.get(index).get(1));
        return rowCol;
    }

    public boolean CanMakeMove(int currentPlayer){
        Player player = gameOfBoard.getPlayersList().get(currentPlayer);

        for (int i = 0; i < m_NumOfCols; i++) {
            if (myBoard[rowX - 1][i].GetNumInCell() != " " && myBoard[rowX - 1][i].GetNumInCell() != "X") {
                if(myBoard[rowX - 1][i].getCellStringColor().equals(player.getColorName())){
                    return true;
                }
            }
        }

        for (int i = 0; i < m_NumOfRows; i++) {
            if (myBoard[i][colX - 1].GetNumInCell() != " " && myBoard[i][colX - 1].GetNumInCell() != "X") {
                if (myBoard[i][colX - 1].getCellStringColor().equals(player.getColorName())){
                    return true;
                }
            }
        }

        return false;
    }

    public void initExplicitBoard(List<Square> ListSquare, Marker marker) {

        colX = marker.getColumn().intValue();
        rowX = marker.getRow().intValue();

        myBoard[rowX - 1][colX - 1].SetNumInCell("X");
        //myBoard[rowX -1][colX -1].SetColor(Color.ORANGE);
        myBoard[rowX -1][colX -1].setCellStringColor(7);
        for (Square sqr : ListSquare) {
            myBoard[sqr.getRow().intValue() - 1][sqr.getColumn().intValue() - 1].SetNumInCell(sqr.getValue().toString());
        }

        createCopyBoard();
    }

    public void initAdvancedRandomBoard(int from, int to) {

        colX = 1 + (int) (Math.random() * (m_NumOfRows));
        rowX = 1 + (int) (Math.random() * (m_NumOfCols));

        myBoard[rowX - 1][colX - 1].SetNumInCell("X");
        //myBoard[rowX -1][colX -1].SetColor(Color.ORANGE);
        myBoard[rowX -1][colX -1].setCellStringColor(7);

        int num = Math.abs(to - from);
        if (from < 0)
            num++;

        int start = from;
        int RandomRow, RandomCol;
        boolean badCell = true;
        int k = ((m_NumOfRows * m_NumOfCols) - 1) / num /gameOfBoard.getNumOfNecessaryPlayers();
        for (int i = 0; i < k; i++) {
            for (int p = 1; p <= gameOfBoard.getNumOfNecessaryPlayers(); p++) {
                for (int j = 0; j < num; j++) {
                    while (badCell) {
                        RandomRow = 1 + (int) (Math.random() * (m_NumOfRows));
                        RandomCol = 1 + (int) (Math.random() * (m_NumOfCols));
                        if (myBoard[RandomRow - 1][RandomCol - 1].GetNumInCell().equals(" ")) {

                            myBoard[RandomRow - 1][RandomCol - 1].SetNumInCell(Integer.toString(start));
                            //myBoard[RandomRow - 1][RandomCol - 1].SetColor(p);
                            myBoard[RandomRow - 1][RandomCol - 1].setCellStringColor(p);
                            badCell = false;
                        }
                    }
                    badCell = true;
                    start++;
                }
                start = from;
            }

        }

        createCopyBoard();
    }

    public void initAdvancedExplicitBoard(List<Square> ListSquare, Marker marker) {
        colX = marker.getColumn().intValue();
        rowX = marker.getRow().intValue();

        myBoard[rowX - 1][colX - 1].SetNumInCell("X");
        //myBoard[rowX -1][colX -1].SetColor(Color.ORANGE);
        myBoard[rowX -1][colX -1].setCellStringColor(7);
        for (Square sqr : ListSquare) {
            myBoard[sqr.getRow().intValue() - 1][sqr.getColumn().intValue() - 1].SetNumInCell(sqr.getValue().toString());
            //myBoard[sqr.getRow().intValue() - 1][sqr.getColumn().intValue() - 1].SetColor(sqr.getColor());
            myBoard[sqr.getRow().intValue() - 1][sqr.getColumn().intValue() - 1].setCellStringColor(sqr.getColor());
        }


        createCopyBoard();
    }

    private void createCopyBoard() {
        colXCopy = colX;
        rowXCopy = rowX;
        boardCopy = new Cell[m_NumOfRows][m_NumOfCols];

        for (int i = 0; i < m_NumOfRows; i++) {
            for (int j = 0; j < m_NumOfCols; j++) {
                try {
                    boardCopy[i][j] = myBoard[i][j].clone();

                }
                catch (Exception ex){

                }
            }
        }
    }

    public void resetBoard() {

        colX = colXCopy;
        rowX = rowXCopy;
        myBoard = new Cell[m_NumOfRows][m_NumOfCols];
        for (int i = 0; i < m_NumOfRows; i++) {
            for (int j = 0; j < m_NumOfCols; j++) {
                try {
                    myBoard[i][j] = boardCopy[i][j].clone();

                }
                catch (Exception ex){

                }
            }
        }
    }
}