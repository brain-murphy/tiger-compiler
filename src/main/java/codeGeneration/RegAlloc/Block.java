package codeGeneration.RegAlloc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fishlinghu on 2016/12/3.
 */
public class Block {
    public Map<String, Node> nodeMap = new HashMap<String, Node>(); // key is the original name of a node
    public Block nextBlock;
    public List<String> oldIR = new ArrayList<String>();
    public List<String> newIR = new ArrayList<String>();
}
