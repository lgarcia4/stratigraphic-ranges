package sranges;

import beast.core.BEASTObject;
import beast.core.Input;
import beast.evolution.alignment.Taxon;
import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;

import java.util.ArrayList;
import java.util.List;

/**
 *@author Alexandra Gavryushkina
 */
public class StratigraphicRange extends BEASTObject {

    public final Input<Taxon> taxonFirstOccurrenceInput = new Input<Taxon>("firstOccurrence", "A BEAST taxon object that corresponds to the first " +
            "occurrence of the taxon");
    public final Input<Taxon> taxonLastOccurrenceInput = new Input<Taxon>("lastOccurrence", "A BEAST taxon object that corresponds to the last " +
            "occurrence of the taxon");


    String firstOccurrenceID="";

    String lastOccurrenceID="";

    /**
     * The list of nodes that belong to the range.
     * The nodes in the list should go in the descending order with respect to the height.
     * The node at position 0 is always corresponds to the first occurrence of the fossil.
     * The last node in the list always corresponds to the last occurrence of the fossil.
     * The intermediate nodes (if any) are always branching nodes.
     * For a single fossil range the first and the last occurrences coincide and there is only a single.
     * node in the list.
     */
    private List<Node> nodes = new ArrayList<>();  //

    @Override
    public void initAndValidate() {
        if (taxonFirstOccurrenceInput.get()!= null) {
            firstOccurrenceID = taxonFirstOccurrenceInput.get().getID();
        }
        if (taxonLastOccurrenceInput.get() != null) {
            lastOccurrenceID = taxonLastOccurrenceInput.get().getID();
        }
    }

    public boolean containsNodeNr(int nodeNr) {
        for (Node node:nodes) {
            if (node.getNr() == nodeNr) {
                return true;
            }
        }
        return false;
    }

    public void addNodeAfter(Node node1, Node node) {
        int node1Nr = node1.getNr();
        for (Node candidate_node:nodes) {
            if (candidate_node.getNr() == node1Nr) {
                int i = nodes.indexOf(candidate_node)+1;
                nodes.add(i,node);
                return;
            }
        }
    }

    public void removeNode(Node node) {
        int nodeNr = node.getNr();
        for (Node candidate_node:nodes) {
            if (candidate_node.getNr() == nodeNr) {
                nodes.remove(candidate_node);
                return;
            }
        }
    }

    public void removeAllNodes() {
        nodes.clear();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * sets the node that corresponds to the the first occurrence of the range in the tree at position 0
     * the single fossil is treated as the first occurrence
     * @param node
     */
    public void setFirstOccurrenceNode(Node node) {
        if (nodes.isEmpty()) {
            nodes.add(node);
        } else {
            nodes.set(0,node);
        }
    }

    /**
     * adds the node that corresponds to the last occurrence of the range in the tree at the last position
     * in the array. A single fossil is treated as the first and the last occurrence.
     *
     * @param node
     */
    public void setLastOccurrenceNode(Node node) {
        if (isSingleFossilRange()) {
            if (nodes.isEmpty()) {
                nodes.add(node);
            } else {
                nodes.set(0,node);
            }
            return;
        } else {
            if (nodes.isEmpty()) {
                nodes.add(null);
            }
        }
        nodes.add(node);
    }

    public boolean isSingleFossilRange() {
        if (taxonFirstOccurrenceInput.get() != null && taxonLastOccurrenceInput.get() != null) {
            return taxonFirstOccurrenceInput.get().equals(taxonLastOccurrenceInput.get());
        } else {
            return nodes.size() == 1;
        }

    }

    public List<Integer> getInternalNodeNrs() {
        List<Integer> internalNodeNrs = new ArrayList<>();
        for (int i=1; i< nodes.size(); i++) {
            internalNodeNrs.add(nodes.get(i).getNr());
        }
        return internalNodeNrs;
    }

    public void setFirstOccurrenceID(String ID) {
        if (taxonFirstOccurrenceInput.get() != null && ! taxonFirstOccurrenceInput.get().getID().equals(ID)) {
            throw new RuntimeException(ID + " was attempted to be assigned as the name of the first occurrence taxon " +
                    "for a stratigraphic range for which the first occurrence taxon input is specified and has name " +
                    taxonFirstOccurrenceInput.get().getID());
        }
        firstOccurrenceID = ID;
    }

    public void setLastOccurrenceID(String ID) {
        if (taxonLastOccurrenceInput.get() != null && ! taxonLastOccurrenceInput.get().getID().equals(ID)) {
            throw new RuntimeException(ID + " was attempted to be assigned as the name of the last occurrence taxon " +
                    "for a stratigraphic range for which the last occurrence taxon input is specified and has name " +
                    taxonFirstOccurrenceInput.get().getID());
        }
        lastOccurrenceID = ID;
    }

    public String getFirstOccurrenceID() {
        if (firstOccurrenceID.isEmpty() && taxonFirstOccurrenceInput.get() != null) {
            firstOccurrenceID = taxonFirstOccurrenceInput.get().getID();
        }
        return firstOccurrenceID;
    }

    public String getLastOccurrenceID() {
        if (lastOccurrenceID.isEmpty() && taxonLastOccurrenceInput.get() != null) {
            lastOccurrenceID = taxonLastOccurrenceInput.get().getID();
        }
        return lastOccurrenceID;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }
}
