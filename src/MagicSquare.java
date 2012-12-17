import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

public class MagicSquare {
	
	static final int n = 4;
	
	public static void main(String[] args) {
		MagicSquare ms = new MagicSquare();
		ms.solve();
	}
	
	void solve() {
		//constants of the problem:
//		int n = 3;
		int M = n * (n * n + 1) / 2;
		
		System.out.println("(n, M) = (" + n + ", " + M + ")");
		
		// Our model
		Model m = new CPModel();
		IntegerVariable[][] cells = new IntegerVariable[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				cells[i][j] = Choco.makeIntVar("cell" + j, 1, n * n);
			}
		}
		//Constraints
		// ... over rows
		Constraint[] rows = new Constraint[n];
		for (int i = 0; i < n; i++) {
			rows[i] = Choco.eq(Choco.sum(cells[i]), M);
		}
		m.addConstraints(rows);
		//... over columns
		// first, get the columns, with a temporary array
		IntegerVariable[][] cellsDual = new IntegerVariable[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				cellsDual[i][j] = cells[j][i];
			}
		}
		Constraint[] cols = new Constraint[n];
		for (int i = 0; i < n; i++) {
			cols[i] = Choco.eq(Choco.sum(cellsDual[i]), M);
		}
		m.addConstraints(cols);
		//... over diagonals
		IntegerVariable[][] diags = new IntegerVariable[2][n];
		for (int i = 0; i < n; i++) {
			diags[0][i] = cells[i][i];
			diags[1][i] = cells[i][(n - 1) - i];
		}
		m.addConstraint(Choco.eq(Choco.sum(diags[0]), M));
		m.addConstraint(Choco.eq(Choco.sum(diags[1]), M));
		//All cells are different from each other
		IntegerVariable[] allVars = new IntegerVariable[n * n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				allVars[i * n + j] = cells[i][j];
			}
		}
		m.addConstraint(Choco.allDifferent(allVars));
		//Our solver
		Solver s = new CPSolver();
		//read the model
		s.read(m);
		//solve the problem
		s.solve();
		//Print the values
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(s.getVar(cells[i][j]).getVal() + " ");
			}
			System.out.println();
		}
	}
}
