package parser.semantic.ir;

public class Label implements IrCode {

    private static int nextLabelId = 0;
    private static String makeLabelId() {
        return "L" + nextLabelId++;
    }


    private String labelName;

    public Label() {
        labelName = makeLabelId();
    }

    public String getLabelName() {
        return labelName;
    }
}
