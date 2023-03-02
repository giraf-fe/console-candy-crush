package CandyCrush;

public class Solve {
    enum Axis {
        Row, Col;
    }

    public Axis axis;
    public int axisIdx;
    public int beginIdx, endIdx;

    public Solve() {
    }

    // copy constructor
    public Solve(Solve ref) {
        this.axis = ref.axis;
        this.axisIdx = ref.axisIdx;
        this.beginIdx = ref.beginIdx;
        this.endIdx = ref.endIdx;
    }

    public String toString() {
        return "Solve: axis = " + axis.name() + ", axisidx = " + axisIdx + ", idx(beg, end) = " + beginIdx + ", "
                + endIdx;
    }
}
