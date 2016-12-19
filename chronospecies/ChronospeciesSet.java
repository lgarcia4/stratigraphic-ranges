package chronospecies;

import beast.core.BEASTObject;
import beast.core.Input;

import java.util.List;

/**
 * Created by gavryusa on 19/12/16.
 */
public class ChronospeciesSet extends BEASTObject {

    public Input<List<Chronospecies>> chronospeciesInput = new Input<>("chronospesies", "all chronospesies defined by the first and last occurrences");

    @Override
    public void initAndValidate() {

    }
}
