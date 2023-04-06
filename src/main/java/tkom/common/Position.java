package tkom.common;

public class Position {
    public int rowNo;
    public int colNo;

    public Position(int row, int col){
        rowNo = row;
        colNo = col;
    }

    @Override
    public boolean equals(Object object) {
        Position comparePos = (Position) object;
        if ((this.rowNo != comparePos.rowNo) || (this.colNo != comparePos.colNo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "row no: " + rowNo + ", column no: " + colNo;
    }

}
