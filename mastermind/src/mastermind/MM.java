package mastermind;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 
 * @author jjpey
 *
 */
public class MM {

	enum BATON{
		ROUGE,
		VERT,
		BLEU,
		JAUNE,
		ROSE
		;
		
		static BATON hazard() {
			return BATON.values()[r.nextInt(MAX)];
		}
	}
	
	final static int MAX = BATON.values().length;
	final static int MAX_ESSAI = 6;
	final static int TAILLE = 6;
	
	static class Solution{
		int bonnes_couleurs = 0;
		int bonnes_positions = 0;
		
		@Override
		public int hashCode() {
			return Objects.hash(bonnes_couleurs, bonnes_positions);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Solution other = (Solution) obj;
			return bonnes_couleurs == other.bonnes_couleurs && bonnes_positions == other.bonnes_positions;
		}
		
		boolean isTrouve() {
			if(bonnes_positions==TAILLE) {
				return true;
			}else {
				return false; 
			}
		}
		
		@Override
		public String toString() {
			return "Solution [bonnes_couleurs=" + bonnes_couleurs + ", bonnes_positions=" + bonnes_positions + "]";
		}
	}
	
	static boolean is_couleur_trouvee(BATON couleur, BATON[] solution) {
		for(int i=0; i<solution.length; i++) {
			BATON b = solution[i];
			if(b==couleur)
				return true;
		}
		return false;
	}
	
	static Solution toSolution(BATON[] tentative, BATON[] solution){
		Solution s = new Solution();
		for(int i=0;i<tentative.length;i++) {
			if(tentative[i]==solution[i]) {
				s.bonnes_positions++;
			}else if(is_couleur_trouvee(tentative[i], solution)) {
				s.bonnes_couleurs++;
			}
		}
		return s;
	}
	
	static Random r = new Random();
	
	static String toString(BATON[] s) {
		StringBuilder sb = new StringBuilder();
		for(BATON b:s) {
			sb.append(b);
			sb.append(" ");
		}
		
		return sb.toString();	
	}
	
	static BATON[] hazard() {
		BATON[] courant = new BATON[TAILLE];
		for(int i =0; i< TAILLE; i++) {
			courant[i] = BATON.hazard();
		}
		return courant;
	}
	
	static void copy(BATON[] source, BATON[] dest) {
		for(int i=0; i < source.length; i++) {
			dest[i] = source[i];
		}
	}
	
	/**
	 * On transforme un tableau de baton en un index linéaire.
	 * L'idée est faire un déclallage de MAX BATTON par position.
	 * @param combi
	 * @return
	 */
	static int toIndex(BATON[] combi) {
		int i = 0;
		for(int j=0; j < combi.length; j++) {
			i = i * MAX + combi[j].ordinal();
		}
		return i;
	}
	
	private static void rempli(BATON[] batons[], BATON[] combinaison, int profondeur) {
		if(profondeur<TAILLE) {
			for(BATON baton:BATON.values()) {
				combinaison[profondeur] = baton;
				rempli(batons, combinaison, profondeur+1);
			}
		}else {
			copy(combinaison, batons[toIndex(combinaison)]);
			return;
		}
	}
	
	/**
	 * La taille correspond à l'ensemble des combinaisons possibles.
	 * @return
	 */
	static int taille() {
		int s = 1;
		int l = BATON.values().length;
		for(int i=0; i<TAILLE;i++) {
			s = s * l;
		}
		return s;
	}
	
	static BATON[][] toutesSolutions(){
		int s = taille();
		BATON[][] solutions = new BATON[s][TAILLE];
		rempli(solutions, new BATON[TAILLE], 0);
		return solutions;
	}
	
	public static void main(String[] args) {
		System.out.println(String.format("résolution du mastermind en utilisant %d essais", MAX_ESSAI));
		System.out.println(String.format("On est dans le contexte de %d couleurs et de %d longueur", MAX, TAILLE));
		BATON[][] solutions = toutesSolutions(); 
		
		if(false)
			for(BATON[] batons:solutions) {
				System.out.println(toString(batons));
			}
		
		int essai = 0;

		BATON[] solution = hazard();
		List<BATON[]> confrontations = new LinkedList<>();
		confrontations.addAll(Arrays.asList(solutions));
		BATON[] tentative = hazard();
		
		while(essai<MAX_ESSAI) {
			System.out.println("essai numéro " + (essai+1));
			System.out.println(toString(tentative));
			Solution confrontation = toSolution(tentative, solution);
			if(confrontation.isTrouve()) {
				System.out.println("on a trouvé la solution");
				System.exit(0);
			}else {
				System.out.println("la solution n'est pas correcte : "+ confrontation);
				List<BATON[]> confrontations2 = new LinkedList<>();
				for(BATON[] batons:confrontations) {
					/* 
					 * l'idée est de comparer la fonction de confrontation entre
					 * la tentative et toutes les possibilités.
					 * On utilise à chaque fois qu'un sous-groupes des possibilités 
					 * restantes afin de réduire les choix au fur et à mesure.
					 */
					Solution sol = toSolution(tentative, batons);
					if(sol.equals(confrontation)) {
						confrontations2.add(batons);
					}
				}
				confrontations = confrontations2;
				tentative = confrontations.get(0);
			}
			essai++;
		}
		System.out.println("on n'a pas trouvé la solution !!!");
		System.out.println(toString(solution));
		System.exit(-1);
	}
}
