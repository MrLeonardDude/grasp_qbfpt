package problems.qbf.solvers;

import java.io.IOException;

public class Tripla {
	public static void main(String[] args) throws IOException {
		int N = 20;
		int u = 1;
		int G;
		int H;

		for (int i = 0; i < N; i++) {

			G = g(u - 1, N);
			H = h(u - 1, N, G);
			System.out.println("[" + u + ", " + G + ", " + H + "]");
			u++;
		}

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
