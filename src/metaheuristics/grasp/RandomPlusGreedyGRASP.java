package metaheuristics.grasp;

import javafx.beans.binding.BooleanExpression;
import problems.Evaluator;
import solutions.Solution;

import java.util.ArrayList;
import java.util.Random;

public abstract class RandomPlusGreedyGRASP<E> {
    /**
     * flag that indicates whether the code should print more information on
     * screen
     */
    public static boolean verbose = false;

    /**
     * a random number generator
     */
    static Random rng = new Random(0);

    /**
     * the objective function being optimized
     */
    protected Evaluator<E> ObjFunction;

    /**
     * the GRASP greediness-randomness parameter
     */
    protected Double alpha;

    /**
     * the default GRASP greediness-randomness parameter, when alpha gets changed between greedy-random
     */
    protected Double alpha_default;

    /**
     * the best solution cost
     */
    protected Double bestCost;

    /**
     * the incumbent solution cost
     */
    protected Double incumbentCost;

    /**
     * the best solution
     */
    protected Solution<E> bestSol;

    /**
     * the incumbent solution
     */
    protected Solution<E> incumbentSol;

    /**
     * the number of iterations the GRASP main loop executes.
     */
    protected Integer iterations;

    /**
     * the Candidate List of elements to enter the solution.
     */
    protected ArrayList<E> CL;

    /**
     * the Restricted Candidate List of elements to enter the solution.
     */
    protected ArrayList<E> RCL;

    /**
     * p value crucial for Random Plus Greedy Implementation
     */
    protected Integer p;

    /**
     *
     */
    protected Double curSol;
    /**
     * Creates the Candidate List, which is an ArrayList of candidate elements
     * that can enter a solution.
     *
     * @return The Candidate List.
     */
    public abstract ArrayList<E> makeCL();

    /**
     * Creates the Restricted Candidate List, which is an ArrayList of the best
     * candidate elements that can enter a solution. The best candidates are
     * defined through a quality threshold, delimited by the GRASP
     * {@link #alpha} greedyness-randomness parameter.
     *
     * @return The Restricted Candidate List.
     */
    public abstract ArrayList<E> makeRCL();

    /**
     * Updates the Candidate List according to the incumbent solution
     * {@link #incumbentSol}. In other words, this method is responsible for
     * updating which elements are still viable to take part into the solution.
     */
    public abstract void updateCL();

    /**
     * Creates a new solution which is empty, i.e., does not contain any
     * element.
     *
     * @return An empty solution.
     */
    public abstract Solution<E> createEmptySol();

    /**
     * The GRASP local search phase is responsible for repeatedly applying a
     * neighborhood operation while the solution is getting improved, i.e.,
     * until a local optimum is attained.
     *
     * @return An local optimum solution.
     */
    public abstract Solution<E> localSearch();

    /**
     * Constructor for the AbstractGRASP class.
     *
     * @param objFunction
     *            The objective function being minimized.
     * @param alpha
     *            The GRASP greediness-randomness parameter (within the range
     *            [0,1])
     * @param iterations
     *            The number of iterations which the GRASP will be executed.
     */
    public RandomPlusGreedyGRASP(Evaluator<E> objFunction, Double alpha, Integer iterations, Integer p) {
        this.ObjFunction = objFunction;
        this.alpha = alpha;
        this.alpha_default = alpha;
        this.iterations = iterations;
        this.p = p;
    }

    /**
     * The GRASP constructive heuristic, which is responsible for building a
     * feasible solution by selecting in a greedy-random fashion, candidate
     * elements to enter the solution.
     *
     * @return A feasible solution to the problem being minimized.
     */
    public Solution<E> constructiveHeuristic() {
        int i = 0;

        CL = makeCL();
        RCL = makeRCL();
        incumbentSol = createEmptySol();
        incumbentCost = Double.POSITIVE_INFINITY;

        /* Main loop, which repeats until the stopping criteria is reached. */
        while (!constructiveStopCriteria()) {

            /*
               Checks if it still is in the first p Random interactions.
             */
            if(i < this.p ){
                alpha = 1.0;
            }

            else if(iterations.equals(i-2)){
                alpha = 0.0;
            }

            else{
                alpha = alpha_default;
            }

            double maxCost = Double.NEGATIVE_INFINITY, minCost = Double.POSITIVE_INFINITY;
            incumbentCost = ObjFunction.evaluate(incumbentSol);
            updateCL();

            /*
             * Explore all candidate elements to enter the solution, saving the
             * highest and lowest cost variation achieved by the candidates.
             */
            for (E c : CL) {
                Double deltaCost = ObjFunction.evaluateInsertionCost(c, incumbentSol);
                if (deltaCost < minCost)
                    minCost = deltaCost;
                if (deltaCost > maxCost)
                    maxCost = deltaCost;
            }
            /*
             * Among all candidates, insert into the RCL those with the highest
             * performance using parameter alpha as threshold.
             */
            for (E c : CL) {
                Double deltaCost = ObjFunction.evaluateInsertionCost(c, incumbentSol);
                if (deltaCost <= minCost + alpha * (maxCost - minCost)) {
                    RCL.add(c);
                }
            }
            /* Choose a candidate randomly from the RCL */
            int rndIndex = rng.nextInt(RCL.size());
            E inCand = RCL.get(rndIndex);
            CL.remove(inCand);
            incumbentSol.add(inCand);
            ObjFunction.evaluate(incumbentSol);
            RCL.clear();
            i++;
        }

        return incumbentSol;
    }

    /**
     * The GRASP mainframe. It consists of a loop, in which each iteration goes
     * through the constructive heuristic and local search. The best solution is
     * returned as result.
     *
     * @return The best feasible solution obtained throughout all iterations.
     */
    public Solution<E> solve() {

        Integer n_repeats = 0;
        long totalTime= 0;
        int i =0;
        bestSol = createEmptySol();
        while (Boolean.TRUE) {
            /*
               Checks if it still is in the first p Random interactions.
             */
            long startTime = System.currentTimeMillis();
            if(i < this.p ){
                this.alpha = 1.0;
            }

            else if(this.iterations.equals(i-2)){
                this.alpha = 0.0;
            }

            else{
                this.alpha = this.alpha_default;
            }

            constructiveHeuristic();
            localSearch();
            if (bestSol.cost > incumbentSol.cost) {
                bestSol = new Solution<E>(incumbentSol);
                if (verbose)
                    System.out.println("(Iter. " + i + ") BestSol = " + bestSol);
            }
//            if(i == 0){
//                curSol = bestSol.cost;
//            }
//            else{
//                if(curSol.equals(bestSol.cost) ){
//                    if(n_repeats == 100000)
//                        break;
//                    else
//                        n_repeats++;
//                }
//                else{
//                    n_repeats = 0;
//                }
//            }
            long endTime   = System.currentTimeMillis();
            totalTime += endTime - startTime;
            i++;
            if(totalTime > 1800000)
                break;
        }
        System.out.println("Numero de iterações" + i);

        return bestSol;
    }

    /**
     * A standard stopping criteria for the constructive heuristic is to repeat
     * until the incumbent solution improves by inserting a new candidate
     * element.
     *
     * @return true if the criteria is met.
     */
    public Boolean constructiveStopCriteria() {
        return (incumbentCost > incumbentSol.cost) ? false : true;
    }

}
