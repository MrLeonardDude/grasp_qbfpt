package problems.qbf.solvers;

import java.io.IOException;

public class Tripla {
	public static void main(String[] args) throws IOException {
		int N = 20;
		int G;
		int H;
		int triplasProibidas[][] =new int[N][3];
		teste(triplasProibidas);
		
		System.out.println(triplasProibidas[1][1]);
		for (int u = 0; u < N; u++) {

			G = g(u, N);
			H = h(u, N, G);
			System.out.println("[" + u + 1 + ", " + G + ", " + H + "]");

		}

	}
	
	public static void teste(int n[][]) {
		n[1][1]=2;
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

}
