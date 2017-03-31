package operators;

import beast.core.Input;
import beast.evolution.operators.TreeOperator;
import sranges.StratigraphicRangeSet;

/**
 * Created by gavryusa on 19/12/16.
 */
public class ChrWilsonBalding extends TreeOperator {

    public Input<StratigraphicRangeSet> chronospeciesInput = new Input<StratigraphicRangeSet>("chronospesies", "all chronospesies defined by the first and last occurrences");


    @Override
    public void initAndValidate() {

    }

    @Override
    public double proposal() {
        return 0;
    }
}
