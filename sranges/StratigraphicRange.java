package sranges;

import beast.core.BEASTObject;
import beast.core.Input;
import beast.evolution.alignment.Taxon;
import beast.evolution.tree.Node;

import java.util.List;

/**
 * Created by gavryusa on 19/12/16.
 */
public class StratigraphicRange extends BEASTObject {

    public final Input<Taxon> taxonFirstOccurrenceInput = new Input<Taxon>("firstOccurrence", "A BEAST taxon object that corresponds to the first " +
            "occurrence of the taxon");
    public final Input<Taxon> taxonLastOccurrenceInput = new Input<Taxon>("lastOccurrence", "A BEAST taxon object that corresponds to the last " +
            "occurrence of the taxon");

    private List<Node> nodes;

    @Override
    public void initAndValidate() {

    }
}
