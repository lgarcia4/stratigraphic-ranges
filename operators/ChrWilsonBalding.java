package operators;

import beast.core.Input;
import beast.evolution.operators.TreeOperator;
import chronospecies.ChronospeciesSet;

/**
 * Created by gavryusa on 19/12/16.
 */
public class ChrWilsonBalding extends TreeOperator {

    public Input<ChronospeciesSet> chronospeciesInput = new Input<ChronospeciesSet>("chronospesies", "all chronospesies defined by the first and last occurrences");


    @Override
    public void initAndValidate() {

    }

    @Override
    public double proposal() {
        return 0;
    }
}
