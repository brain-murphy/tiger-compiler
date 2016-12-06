package codeGeneration.RegAlloc;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;

/**
 * Created by fishlinghu on 2016/12/3.
 */
public class Node {
    public String originalName;
    public String regName;
    public boolean assigned;
    public int start, end; // live range
    public List<Node> neighbor = new ArrayList<Node>();
    public List<Integer> overlap = new ArrayList<Integer>();
    public List<Integer> accessPoint = new ArrayList<Integer>();

    public Node(String s, int n){
        originalName = s;
        start = n;
        end = n;
        assigned = false;
        regName = "";
    }
}
