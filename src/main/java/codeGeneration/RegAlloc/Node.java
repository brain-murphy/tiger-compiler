package codeGeneration.RegAlloc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fishlinghu on 2016/12/3.
 */
public class Node {
    public String originalName, regName;
    public int start, end; // live range
    public List<Node> neighbor = new ArrayList<Node>();

    public Node(String s, int n){
        originalName = s;
        start = n;
        end = n;
    }
}
