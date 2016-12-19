package chronospecies;

import beast.core.Input;
import beast.core.parameter.RealParameter;
import beast.evolution.speciation.SpeciesTreeDistribution;
import beast.evolution.tree.TreeInterface;

/**
 * Created by gavryusa on 19/12/16.
 */
public class ChronospeciesFBD extends SpeciesTreeDistribution {

    public Input<ChronospeciesSet> chronospeciesInput = new Input<ChronospeciesSet>("chronospesies", "all chronospesies defined by the first and last occurrences");

    //'direct' parameters
    public Input<RealParameter> originInput =
            new Input<RealParameter>("origin", "The time when the process started", (RealParameter)null);
    public Input<RealParameter> birthRateInput =
            new Input<RealParameter>("birthRate", "Birth rate", Input.Validate.REQUIRED);
    public Input<RealParameter> deathRateInput =
            new Input<RealParameter>("deathRate", "Death rate", Input.Validate.REQUIRED);
    public Input<RealParameter> samplingRateInput =
            new Input<RealParameter>("samplingRate", "Sampling rate per individual", Input.Validate.REQUIRED);

    // r parameter
    public Input<RealParameter> removalProbability =
            new Input<RealParameter>("removalProbability", "The probability that an individual is removed from the process after the sampling", Input.Validate.REQUIRED);

    public Input<RealParameter> rhoProbability =
            new Input<RealParameter>("rho", "Probability of an individual to be sampled at present", (RealParameter)null);

    @Override
    public double calculateTreeLogLikelihood(TreeInterface tree) {
        return 0;
    }
}
