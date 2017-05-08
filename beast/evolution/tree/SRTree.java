package beast.evolution.tree;

import beast.core.Input;
import beast.core.StateNode;
import beast.core.StateNodeInitialiser;
import sranges.StratigraphicRange;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandra Gavryushkina
 *
 * A labeled oriented tree on stratigraphic ranges under budding speciation.
 * At every internal node, the branch leading to the left child represents the ancestral species
 * and the branch leading to the right child represents the descendant species.
 * Branches starting with sampled ancestors inheret the orientation from the parental branches:
 * if a node is fake then the non-direct ancestor child gets the same left/right orientation as the fake node.
 * If the root is a fake node then the non-direct ancestor child is always left.
 */
public class SRTree extends Tree {

    public Input<List<StratigraphicRange>> stratigraphicRangeInput = new Input<>("stratigraphicRange", "all stratigraphic ranges", new ArrayList<>());

    protected ArrayList<StratigraphicRange> sRanges;
    protected ArrayList<StratigraphicRange> storedSRanges;



    protected void initSRanges() {
        sRanges = (ArrayList) stratigraphicRangeInput.get();;
        List<Node> externalNodes = getExternalNodes();
        for (StratigraphicRange range:sRanges) {
            range.removeAllNodes();
            for (Node node:externalNodes) {
                if(node.getID().equals(range.getFirstOccurrenceID()) && !range.isSingleFossilRange()) {
                    if (!node.isDirectAncestor()) {
                        throw new RuntimeException("The first occurrence always has to be a sampled ancestor but " + range.getFirstOccurrenceID() + " is not a sampled ancestor. Something went wrong in initializing the stratigraphic range tree."  );
                    }
                    range.setFirstOccurrenceNode(node.getParent());
                }
                if (node.getID().equals(range.getLastOccurrenceID())) {
                    if (node.isDirectAncestor()) {
                        range.setLastOccurrenceNode(node.getParent());
                    } else {
                        range.setLastOccurrenceNode(node);
                    }


                }
            }
            range.initAndValidate();
        }
        initStoredRanges();
    }

    public void initStoredRanges() {
        storedSRanges = new ArrayList<>();
        for (int i=0; i<sRanges.size(); i++) {
            StratigraphicRange range_src = sRanges.get(i);
            StratigraphicRange range_sink = new StratigraphicRange();
            ArrayList<Node> nodes_src = (ArrayList) range_src.getNodes();
            for (int j=0; j<nodes_src.size(); j++) {
                range_sink.addNode(nodes_src.get(j));
            }
            range_sink.setFirstOccurrenceID(range_src.getFirstOccurrenceID());
            range_sink.setLastOccurrenceID(range_src.getLastOccurrenceID());
            storedSRanges.add(range_sink);
        }

    }

    /**
     * copy of all values from existing tree *
     */
    @Override
    public void assignFrom(final StateNode other) {
        final Tree tree = (Tree) other;
        final Node[] nodes = new Node[tree.getNodeCount()];//tree.getNodesAsArray();
        for (int i = 0; i < tree.getNodeCount(); i++) {
            nodes[i] = newNode();
        }
        setID(tree.getID());
        //index = tree.index;
        root = nodes[tree.root.getNr()];
        root.assignFrom(nodes, tree.root);
        root.parent = null;
        nodeCount = tree.nodeCount;
        internalNodeCount = tree.internalNodeCount;
        leafNodeCount = tree.leafNodeCount;
        initArrays();
        if(stratigraphicRangeInput.get()!= null) {
            initSRanges();
        }
    }

    /**
     * as assignFrom, but only copy tree structure *
     */
    @Override
    public void assignFromFragile(final StateNode other) {
        // invalidate cache
        postCache = null;

        final Tree tree = (Tree) other;
        if (m_nodes == null) {
            initArrays();
        }
        root = m_nodes[tree.root.getNr()];
        final Node[] otherNodes = tree.m_nodes;
        final int rootNr = root.getNr();
        assignFrom(0, rootNr, otherNodes);
        root.height = otherNodes[rootNr].height;
        root.parent = null;
        if (otherNodes[rootNr].getLeft() != null) {
            root.setLeft(m_nodes[otherNodes[rootNr].getLeft().getNr()]);
        } else {
            root.setLeft(null);
        }
        if (otherNodes[rootNr].getRight() != null) {
            root.setRight(m_nodes[otherNodes[rootNr].getRight().getNr()]);
        } else {
            root.setRight(null);
        }
        assignFrom(rootNr + 1, nodeCount, otherNodes);
        if(stratigraphicRangeInput.get()!= null) {
            initSRanges();
        }
    }

    /**
     * helper to assignFromFragile *
     */
    private void assignFrom(final int start, final int end, final Node[] otherNodes) {
        for (int i = start; i < end; i++) {
            Node sink = m_nodes[i];
            Node src = otherNodes[i];
            sink.height = src.height;
            sink.parent = m_nodes[src.parent.getNr()];
            if (src.getLeft() != null) {
                sink.setLeft(m_nodes[src.getLeft().getNr()]);
                if (src.getRight() != null) {
                    sink.setRight(m_nodes[src.getRight().getNr()]);
                } else {
                    sink.setRight(null);
                }
            }
        }
    }

    /**
     * StateNode implementation *
     */
    @Override
    protected void store() {

        storeNodes(0, nodeCount);
        storedRoot = m_storedNodes[root.getNr()];
        for (StratigraphicRange range_src:sRanges) {
            int index = sRanges.indexOf(range_src);
            StratigraphicRange range_sink = storedSRanges.get(index);
            range_sink.removeAllNodes();
            for (int i=0; i< range_src.getNodes().size(); i++) {
                Node node = range_src.getNodes().get(i);
                range_sink.addNode(node);
            }
        }
    }

    /**
     * Stores nodes with index i, for start <= i < end
     * (i.e. including start but not including end)
     *
     * @param start the first index to be stored
     * @param end   nodes are stored up to but not including this index
     */
    private void storeNodes(final int start, final int end) {
        // Use direct members for speed (we are talking 5-7% or more from total time for large trees :)
        for (int i = start; i < end; i++) {
            final SRNode sink = (SRNode)m_storedNodes[i];
            final SRNode src = (SRNode)m_nodes[i];
            sink.height = src.height;

            if ( src.parent != null ) {
                sink.parent = m_storedNodes[src.parent.getNr()];
            } else {
                // currently only called in the case of sampled ancestor trees
                // where root node is not always last in the list
                sink.parent = null;
            }

            final List<Node> children = sink.children;
            final List<Node> srcChildren = src.children;

            if( children.size() == srcChildren.size() ) {
                // shave some more time by avoiding list clear and add
                for (int k = 0; k < children.size(); ++k) {
                    final SRNode srcChild = (SRNode)srcChildren.get(k);
                    // don't call addChild, which calls  setParent(..., true);
                    final Node c = m_storedNodes[srcChild.getNr()];
                    c.parent = sink;
                    children.set(k, c);
                }
            } else {
                children.clear();
                //sink.removeAllChildren(false);
                for (final Node srcChild : srcChildren) {
                    // don't call addChild, which calls  setParent(..., true);
                    final Node c = m_storedNodes[srcChild.getNr()];
                    c.parent = sink;
                    children.add(c);
                    //sink.addChild(c);
                }
            }
        }
    }

    @Override
    public void restore() {

        // necessary for sampled ancestor trees
        nodeCount = m_storedNodes.length;

        final Node[] tmp = m_storedNodes;
        m_storedNodes = m_nodes;
        m_nodes = tmp;
        root = m_nodes[storedRoot.getNr()];

        // necessary for sampled ancestor trees,
        // we have the nodes, no need for expensive recursion
        leafNodeCount = 0;
        for( Node n : m_nodes ) {
            leafNodeCount += n.isLeaf() ? 1 : 0;
        }

        //leafNodeCount = root.getLeafNodeCount();

        hasStartedEditing = false;

        for( Node n : m_nodes ) {
            n.isDirty = Tree.IS_CLEAN;
        }

        postCache = null;

        ArrayList<StratigraphicRange> tmp_ranges = storedSRanges;
        storedSRanges = sRanges;
        sRanges=tmp_ranges;
    }


    // SRange methods:

    public ArrayList<StratigraphicRange> getSRanges() {
        return  sRanges;
    }

    public ArrayList<Integer> getSRangesInternalNodeNrs() { // nodes that do not represent the first occurrences, does not include nodes
        // from single fossil range
        ArrayList<Integer> internalNodeNrs = new ArrayList<>();
        for (StratigraphicRange range: sRanges) {
            internalNodeNrs.addAll(range.getInternalNodeNrs());
        }
        return internalNodeNrs;
    }

    public StratigraphicRange sRangesContainsID(String taxonName) {
        for (StratigraphicRange range:sRanges) {
            if (range.getFirstOccurrenceID().equals(taxonName)) {
                return range;
            }
            if (range.getLastOccurrenceID().equals(taxonName)) {
                return range;
            }
        }
        return null;
    }

    public StratigraphicRange getRangeOfNode(Node node) {
        int nodeNr = node.getNr();
        for (StratigraphicRange candidate_range:sRanges) {
            if (candidate_range.containsNodeNr(nodeNr)) {
                return candidate_range;

            }
        }
        return null;
    }



    public boolean belongToSameSRange(Node node1, Node node2) {
        int node1Nr = node1.getNr();
        int node2Nr = node2.getNr();
        for (StratigraphicRange range:sRanges) {
            if (range.containsNodeNr(node1Nr) && range.containsNodeNr(node2Nr)) {
                return true;
            }
        }
        return false;
    }

}
