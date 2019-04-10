package problems.qbf;

import solutions.Solution;

import java.io.*;
import java.util.Arrays;

public class QBFPT extends QBF {

    /**
     * The matrix A of coefficients for the QBF f(x) = x'.A.x
     */
    public Double[][] A;

    /**
     * The constructor for QuadracticBinaryFunction class. The filename of the
     * input for setting matrix of coefficients A of the QBF. The dimension of
     * the array of variables x is returned from the {@link #readInput} method.
     *
     * @param filename
     *            Name of the file containing the input for setting the QBF.
     * @throws IOException
     *             Necessary for I/O operations.
     */
    public QBFPT(String filename) throws IOException {
        super(filename);
    }

    /**
     * Evaluates the value of a solution by transforming it into a vector. This
     * is required to perform the matrix multiplication which defines a QBF.
     *
     * @param sol
     *            the solution which will be evaluated.
     */
    public void setVariables(Solution<Integer> sol) {

        resetVariables();
        if (!sol.isEmpty()) {
            for (Integer elem : sol) {
                variables[elem] = 1.0;
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see problems.Evaluator#getDomainSize()
     */
    @Override
    public Integer getDomainSize() {
        return size;
    }

    /**
     * {@inheritDoc} In the case of a QBF, the evaluation correspond to
     * computing a matrix multiplication x'.A.x. A better way to evaluate this
     * function when at most two variables are modified is given by methods
     * {@link #evaluateInsertionQBF(int)}, {@link #evaluateRemovalQBF(int)} and
     * {@link #evaluateExchangeQBF(int,int)}.
     *
     * @return The evaluation of the QBF.
     */
    @Override
    public Double evaluate(Solution<Integer> sol) {

        setVariables(sol);
        return sol.cost = evaluateQBF();

    }

    /*
     * (non-Javadoc)
     *
     * @see problems.Evaluator#evaluateInsertionCost(java.lang.Object,
     * solutions.Solution)
     */
    @Override
    public Double evaluateInsertionCost(Integer elem, Solution<Integer> sol) {

        setVariables(sol);
        return evaluateInsertionQBF(elem);

    }

    /**
     * Determines the contribution to the QBF objective function from the
     * insertion of an element.
     *
     * @param i
     *            Index of the element being inserted into the solution.
     * @return Ihe variation of the objective function resulting from the
     *         insertion.
     */
    public Double evaluateInsertionQBF(int i) {

        if (variables[i] == 1)
            return 0.0;

        return evaluateContributionQBF(i);
    }

    /*
     * (non-Javadoc)
     *
     * @see problems.Evaluator#evaluateRemovalCost(java.lang.Object,
     * solutions.Solution)
     */
    @Override
    public Double evaluateRemovalCost(Integer elem, Solution<Integer> sol) {

        setVariables(sol);
        return evaluateRemovalQBF(elem);

    }

    /**
     * Determines the contribution to the QBF objective function from the
     * removal of an element.
     *
     * @param i
     *            Index of the element being removed from the solution.
     * @return The variation of the objective function resulting from the
     *         removal.
     */
    public Double evaluateRemovalQBF(int i) {

        if (variables[i] == 0)
            return 0.0;

        return -evaluateContributionQBF(i);

    }

    /*
     * (non-Javadoc)
     *
     * @see problems.Evaluator#evaluateExchangeCost(java.lang.Object,
     * java.lang.Object, solutions.Solution)
     */
    @Override
    public Double evaluateExchangeCost(Integer elemIn, Integer elemOut, Solution<Integer> sol) {

        setVariables(sol);
        return evaluateExchangeQBF(elemIn, elemOut);

    }

    /**
     * Reserving the required memory for storing the values of the domain
     * variables.
     *
     * @return a pointer to the array of domain variables.
     */
    protected Double[] allocateVariables() {
        Double[] _variables = new Double[size];
        return _variables;
    }

    /**
     * Reset the domain variables to their default values.
     */
    public void resetVariables() {
        Arrays.fill(variables, 0.0);
    }

    /**
     * Prints matrix {@link #A}.
     */
    public void printMatrix() {

        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                System.out.print(A[i][j] + " ");
            }
            System.out.println();
        }

    }

}