package sranges;

import beast.core.BEASTObject;
import beast.core.Input;

import java.util.List;

/**
 * Created by gavryusa on 19/12/16.
 */
public class StratigraphicRangeSet extends BEASTObject {

    public Input<List<StratigraphicRange>> chronospeciesInput = new Input<>("chronospesies", "all chronospesies defined by the first and last occurrences");

    @Override
    public void initAndValidate() {

    }
}
