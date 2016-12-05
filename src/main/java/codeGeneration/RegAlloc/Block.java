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

    public int maxRegNum;// initialize it here

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
            callrFlag = false;
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
                        newNode = new Node(tempStr, i); // start time is initialized here
                    }
                    else{
                        // update the end time
                        nodeMap.get( tempStr ).accessPoint.add(i);
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
        int overlapStart, overlapEnd;
        for(Map.Entry<String, Node> entryA: nodeMap.entrySet()){
            for(Map.Entry<String, Node> entryB: nodeMap.entrySet()){
                if(Objects.equals(entryA.getKey(), entryB.getKey())){
                    // same node
                    continue;
                }
                if(!(entryA.getValue().end < entryB.getValue().start) && !(entryA.getValue().start > entryB.getValue().end)){
                    // find a neighbor
                    entryA.getValue().neighbor.add( entryB.getValue() );
                    // record the overlapped range between nodes
                    overlapStart = Math.max(entryA.getValue().start, entryB.getValue().start);
                    overlapEnd = Math.min(entryA.getValue().end, entryB.getValue().end);
                    entryA.getValue().overlap.add( overlapEnd-overlapStart );
                }
            }
        }
    }
    public void doColoring(){
        Comparator c = new Comparator() {
            @Override
            public int compare(Object a, Object b) {
                Node n1 = (Node)a;
                Node n2 = (Node)b;
                if(n1.neighbor.size() > n2.neighbor.size())
                    return 1;
                else
                    return -1;
            }
        };
        Map<String, Boolean> traversedMap = new HashMap<String, Boolean>();
        PriorityQueue<Node> untraversedQ = new PriorityQueue<Node>(nodeMap.size(), c);
        int numRegUsed = 0;
        // get the starting node to put in the queue
        for(Map.Entry<String, Node> entry: nodeMap.entrySet()){
            untraversedQ.offer( entry.getValue() );
        }

        int i;
        Node currentNode, node_for_expand;
        PriorityQueue<Node> processQ = new PriorityQueue<Node>(nodeMap.size(), c);
        while( (currentNode = untraversedQ.poll()) != null ){
            if(traversedMap.containsKey(currentNode.originalName))
                continue;
            processQ.offer( currentNode );
            traversedMap.put( currentNode.originalName, true );
            while( (node_for_expand = processQ.poll()) != null ){
                // expand from the front node, update the traversedMap, assign register
                // Check how many register we can use
                Map<String, Boolean> regMap = new HashMap<String, Boolean>();{
                    regMap.put("t0", true);
                    regMap.put("t1", true);
                    regMap.put("t2", true);
                    regMap.put("t3", true);
                    regMap.put("t4", true);
                    regMap.put("t5", true);
                    regMap.put("t6", true);
                    regMap.put("t7", true);
                    regMap.put("t8", true);
                    regMap.put("t9", true);
                }
                // check which registers are used, also put the untraversed register into the queue
                i = 0;
                while(i < node_for_expand.neighbor.size()){
                    if( regMap.containsKey( node_for_expand.neighbor.get(i).regName ) )
                        regMap.remove( node_for_expand.neighbor.get(i).regName );
                    if( !traversedMap.containsKey( node_for_expand.neighbor.get(i).originalName ) )
                        processQ.offer( node_for_expand.neighbor.get(i) );
                    i = i + 1;
                }
                // the rest are register we can used
                for(Map.Entry<String, Boolean> entry: regMap.entrySet()){
                    node_for_expand.assigned = true;
                    node_for_expand.regName = entry.getKey();
                    break;
                }
            }
        }
    }
}
