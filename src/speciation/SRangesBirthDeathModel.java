package speciation;

import java.util.List;

import beast.core.BEASTInterface;
import beast.core.Citation;
import beast.core.Description;

import beast.evolution.speciation.SABirthDeathModel;
import beast.evolution.tree.*;
import beast.evolution.tree.SRTree;

import static com.sun.tools.doclint.Entity.lambda;

/**
 * @author Alexandra Gavryushkina
 */


@Description("A variant of the fossilized birth-death model under budding (asymmetric) speciation with stratigraphic ranges")
@Citation("Stadler T, Gavryushkina A, Warnock RCM, Drummond AJ, Heath TA (2017) \n" +
        "The fossilized birth-death model under different types of speciation")
@Citation("Gavryushkina A, Warnock RCM, Drummond AJ, Heath TA, Stadler T (2017) \n" +
        "Bayesian total-evidence dating under the fossilized birth-death model with stratigraphic ranges.")
public class SRangesBirthDeathModel extends SABirthDeathModel {

    private double q_tilde(double t, double c1, double c2) {
        return Math.sqrt(Math.exp(t*(lambda + mu + psi))*q(t,c1,c2));
    }

    private double log_q(double t, double c1, double c2) {
        return Math.log(q(t,c1,c2));
    }

    private double log_q_tilde(double t, double c1, double c2) {
        return 0.5*(t*(lambda + mu + psi) + log_q(t,c1,c2));
    }

    private double log_oneMinusP0(double t, double c1, double c2) {
        return Math.log(oneMinusP0(t,c1,c2));
    }

    private double log_oneMinusP0Hat(double t, double c1, double c2) {
        return Math.log(oneMinusP0Hat(t,c1,c2));
    }

    private double log_p0s(double t, double c1, double c2) {
        return Math.log(p0s(t,c1,c2));
            }

    @Override
    public double calculateTreeLogLikelihood(TreeInterface tree)
    {
        int nodeCount = tree.getNodeCount();
        updateParameters();
        if (lambdaExceedsMu && lambda <= mu) {
            return Double.NEGATIVE_INFINITY;
        }

        if (lambda < 0 || mu < 0 || psi < 0) {
            return Double.NEGATIVE_INFINITY;
        }

        //double x0 = tree.getRoot().getHeight() + origToRootDistance;
        double x0 = origin;

//        if (taxonInput.get() != null) { //TODO rewrite this part for SA sRanges
//
//            if (taxonAge > origin) {
//                return Double.NEGATIVE_INFINITY;
//            }
//            double logPost = 0.0;
//
//            if (conditionOnSamplingInput.get()) {
//                logPost -= Math.log(oneMinusP0(x0, c1, c2));
//            }
//
//            if (conditionOnRhoSamplingInput.get()) {
//                logPost -= Math.log(oneMinusP0Hat(x0, c1, c2));
//            }
//
//            if (SATaxonInput.get().getValue() == 0) {
//                logPost += Math.log(1 - oneMinusP0(taxonAge, c1, c2));
//            } else {
//                logPost += Math.log(oneMinusP0(taxonAge, c1, c2));
//            }
//
//            return logPost;
//        }

        double x1=tree.getRoot().getHeight();

        if (x0 < x1 ) {
            return Double.NEGATIVE_INFINITY;
        }

        double logPost;
        if (!conditionOnRootInput.get()){
            logPost = -log_q(x0, c1, c2);
        } else {
            if (tree.getRoot().isFake()){   //when conditioning on the root we assume the process
                //starts at the time of the first branching event and
                //that means that the root can not be a sampled ancestor
                return Double.NEGATIVE_INFINITY;
            } else {
                logPost = -log_q(x1, c1, c2);
            }
        }

        if (conditionOnSamplingInput.get()) {
            logPost -= log_oneMinusP0(x0, c1, c2);
        }

        if (conditionOnRhoSamplingInput.get()) {
            if (conditionOnRootInput.get()) {
                logPost -= Math.log(lambda) + log_oneMinusP0Hat(x1, c1, c2)+ log_oneMinusP0Hat(x1, c1, c2);
            }  else {
                logPost -= log_oneMinusP0Hat(x0, c1, c2);
            }
        }

        for (int i = 0; i < nodeCount; i++) {
            Node node = tree.getNode(i);
            if (node.isLeaf()) {
                if  (!node.isDirectAncestor())  {
                    if (node.getHeight() > 0.000000000005 || rho == 0.) {
                        Node fossilParent = node.getParent();
                        if (((SRTree)tree).belongToSameSRange(node, fossilParent)) {
                            logPost += Math.log(psi) + log_q_tilde(node.getHeight(), c1, c2) + log_p0s(node.getHeight(), c1, c2);
                        } else {
                            logPost += Math.log(psi) + log_q(node.getHeight(), c1, c2) + log_p0s(node.getHeight(), c1, c2);
                        }
                    } else {
                        logPost += Math.log(4*rho);
                    }
                }
            } else {
                Node child;
                if (node.isFake()) {
                    logPost += Math.log(psi);
                    Node parent = node.getParent();
                    child = node.getNonDirectAncestorChild();
                    if (parent != null && ((SRTree)tree).belongToSameSRange(parent,node)) {
                        logPost += log_q_tilde(node.getHeight(), c1, c2) - log_q(node.getHeight(), c1, c2);
                    }
                    if (child != null && ((SRTree)tree).belongToSameSRange(node, child)) {
                        logPost += log_q(node.getHeight(), c1, c2)-  log_q_tilde(node.getHeight(), c1, c2);
                    }
                } else {
                    logPost += Math.log(lambda) - log_q(node.getHeight(), c1, c2);
                    child = node.getLeft();
                }
                if (((SRTree)tree).belongToSameSRange(node, child)) { //This term accounts for not including fossil samples within a stratigraphic range.
                    logPost += psi*(node.getHeight() - child.getHeight());
                }
            }
        }

        return logPost;
    }

}

