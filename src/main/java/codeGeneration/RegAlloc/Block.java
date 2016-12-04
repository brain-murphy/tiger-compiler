package codeGeneration.RegAlloc;

import java.util.*;

/**
 * Created by fishlinghu on 2016/12/3.
 */
public class Block {
    public Map<String, Node> nodeMap = new HashMap<String, Node>(); // key is the original name of a node
    public Block nextBlock;
    public List<List<String>> oldIR = new ArrayList<List<String>>();
    public List<String> newIR = new ArrayList<String>();

    public boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public void generateNode(){
        // generate nodes using oldIR
        Node newNode;
        int i = 0, j;
        String opStr, tempStr;
        Boolean callFlag, callrFlag, branchFlag;
        while(i < oldIR.size()){
            //
            opStr = oldIR.get(i).get(0);
            j = 1;
            if(Objects.equals(opStr, "GOTO")) {
                // skip the while loop, no variable here
                i = i + 1;
                continue;
            }
            else if(Objects.equals(opStr, "CALL")){
                callFlag = true;
                j = 2; // skip the first 2 strings
            }
            else if(Objects.equals(opStr, "CALLR")){
                // ignore the third string
                callrFlag = true;
            }
            else if(opStr.charAt(0) == 'B'){
                // branch instruction, ignore the last one
                branchFlag = true;
                j = 2;
            }
            while(j < oldIR.get(i).size()){
                if(callrFlag && j == 2)
                    j = j + 1;
                tempStr = oldIR.get(i).get(j);
                if(!isNumeric(tempStr)){
                    if(!nodeMap.containsKey(tempStr)){
                        // a new node
                        newNode = new Node(tempStr, i);
                    }
                    else{
                        // update the end time
                        nodeMap.get( tempStr ).accessCount += 1;
                        nodeMap.get( tempStr ).end = i;
                    }
                }
                j = j + 1;
            }
            i = i + 1;
        }
    }

    public void buildCFG(){
        // build CFG of nodes within this block
        for(Map.Entry<String, Node> entryA: nodeMap.entrySet()){
            for(Map.Entry<String, Node> entryB: nodeMap.entrySet()){
                if(Objects.equals(entryA.getKey(), entryB.getKey())){
                    // same node
                    continue;
                }
                if(!(entryA.getValue().end < entryB.getValue().start) && !(entryA.getValue().start > entryB.getValue().end)){
                    // find a neighbor
                    entryA.getValue().neighbor.add( entryB.getValue() );
                }
            }
        }
    }
}
