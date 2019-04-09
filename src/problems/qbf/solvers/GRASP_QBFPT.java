package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;

import metaheuristics.grasp.AbstractGRASP;
import problems.qbf.QBF_Inverse;
import solutions.Solution;

/**
 * Metaheuristic GRASP (Greedy Randomized Adaptive Search Procedure) for
 * obtaining an optimal solution to a QBF (Quadractive Binary Function --
 * {@link }). Since by default this GRASP considers minimization problems, an
 * inverse QBF function is adopted.
 * 
 * @author ccavellucci, fusberti
 */
public class GRASP_QBFPT extends AbstractGRASP<Integer> {

	/**
	 * Constructor for the GRASP_QBF class. An inverse QBF objective function is
	 * passed as argument for the superclass constructor.
	 * 
	 * @param alpha      The GRASP greediness-randomness parameter (within the range
	 *                   [0,1])
	 * @param iterations The number of iterations which the GRASP will be executed.
	 * @param filename   Name of the file for which the objective function
	 *                   parameters should be read.
	 * @throws IOException necessary for I/O operations.
	 */
	public GRASP_QBFPT(Double alpha, Integer iterations, String filename) throws IOException {
		super(new QBF_Inverse(filename), alpha, iterations);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeCL()
	 */
	@Override
	public ArrayList<Integer> makeCL() {

		ArrayList<Integer> _CL = new ArrayList<Integer>();
		for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
			Integer cand = new Integer(i);
			_CL.add(cand);
		}

		return _CL;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeRCL()
	 */
	@Override
	public ArrayList<Integer> makeRCL() {

		ArrayList<Integer> _RCL = new ArrayList<Integer>();

		return _RCL;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#updateCL()
	 */
	@Override
	public void updateCL() {

		// do nothing since all elements off the solution are viable candidates.

	}

	/*
	 * Criação das triplas proibidas
	 */
	public static void generateTriples(int triples[][], int size) {
		int G, H;
		int x[] = new int[3];
		for (int u = 0; u < size; u++) {
			G = g(u, size);
			H = h(u, size, G);

			x[0] = u;
			x[1] = G;
			x[2] = H;
			System.out.println("[" + x[0] + ", " + x[1] + ", " + x[2] + "]");
			ordena(x);
			System.out.println("[" + x[0] + ", " + x[1] + ", " + x[2] + "]");
			triples[u][0] = x[0];
			triples[u][1] = x[1];
			triples[u][2] = x[2];
		}
	}

	public static void ordena(int x[]) {
		int aux;
		int a = x[0], b = x[1], c = x[2];

		if (a > b) {
			aux = a;
			a = b;
			b = aux;
			if (b > c) {
				aux = b;
				b = c;
				c = aux;
				if (a > b) {
					aux = a;
					a = b;
					b = aux;
				}
			}

		} else if (b > c) {
			aux = b;
			b = c;
			c = aux;
		}
		x[0] = a;
		x[1] = b;
		x[2] = c;
	}

	public static int h(int u, int n, int g) {
		int aux = l(u, 193, 1093, n);

		if (aux != u && aux != g) {
			return aux;
		} else {
			aux = 1 + (aux % n);
			if (aux != u && aux != g)
				return aux;
			else
				return 1 + ((aux + 1) % n);
		}
	}

	public static int g(int u, int n) {
		int aux = l(u, 131, 1031, n);

		if (aux != u)
			return aux;
		else
			return 1 + (aux % n);

	}

	public static int l(int u, int pi1, int pi2, int n) {
		return 1 + ((pi1 * u + pi2) % n);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This createEmptySol instantiates an empty solution and it attributes a zero
	 * cost, since it is known that a QBF solution with all variables set to zero
	 * has also zero cost.
	 */
	@Override
	public Solution<Integer> createEmptySol() {
		Solution<Integer> sol = new Solution<Integer>();
		sol.cost = 0.0;
		return sol;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The local search operator developed for the QBF objective function is
	 * composed by the neighborhood moves Insertion, Removal and 2-Exchange.
	 */
	@Override
	public Solution<Integer> localSearch() {

		Double minDeltaCost;
		Integer bestCandIn = null, bestCandOut = null;

		do {
			minDeltaCost = Double.POSITIVE_INFINITY;
			updateCL();

			// Evaluate insertions
			for (Integer candIn : CL) {
				double deltaCost = ObjFunction.evaluateInsertionCost(candIn, incumbentSol);
				if (deltaCost < minDeltaCost) {
					minDeltaCost = deltaCost;
					bestCandIn = candIn;
					bestCandOut = null;
				}
			}
			// Evaluate removals
			for (Integer candOut : incumbentSol) {
				double deltaCost = ObjFunction.evaluateRemovalCost(candOut, incumbentSol);
				if (deltaCost < minDeltaCost) {
					minDeltaCost = deltaCost;
					bestCandIn = null;
					bestCandOut = candOut;
				}
			}
			// Evaluate exchanges
			for (Integer candIn : CL) {
				for (Integer candOut : incumbentSol) {
					double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, incumbentSol);
					if (deltaCost < minDeltaCost) {
						minDeltaCost = deltaCost;
						bestCandIn = candIn;
						bestCandOut = candOut;
					}
				}
			}
			// Implement the best move, if it reduces the solution cost.
			if (minDeltaCost < -Double.MIN_VALUE) {
				if (bestCandOut != null) {
					incumbentSol.remove(bestCandOut);
					CL.add(bestCandOut);
				}
				if (bestCandIn != null) {
					incumbentSol.add(bestCandIn);
					CL.remove(bestCandIn);
				}
				ObjFunction.evaluate(incumbentSol);
			}
		} while (minDeltaCost < -Double.MIN_VALUE);

		return null;
	}

	/**
	 * A main method used for testing the GRASP metaheuristic.
	 * 
	 */
	public static void main(String[] args) throws IOException {

		long startTime = System.currentTimeMillis();
		int N = 20;
		int triplasProibidas[][] = new int[N][3];
		generateTriples(triplasProibidas, N);
		GRASP_QBFPT grasp = new GRASP_QBFPT(0.05, 1000, "instances/qbf020");
		Solution<Integer> bestSol = grasp.solve();
		System.out.println("maxVal = " + bestSol);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");

	}

}
