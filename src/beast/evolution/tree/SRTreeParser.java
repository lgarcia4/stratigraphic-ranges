package beast.evolution.tree;

import beast.core.Input;
import beast.util.TreeParser;

/**
 * Created by lauragarcia on 14/06/17.
 */
public class SRTreeParser extends SRTree {

    public Input<String> newickInput = new Input<>("newick",
            "Newick string describing tree topology.",
            Input.Validate.REQUIRED);

    @Override
    public void initAndValidate() {

        Tree tree = new TreeParser(newickInput.get(), false, true, true, 0);

        assignFromWithoutID(tree);

        super.initAndValidate();
    }
}
