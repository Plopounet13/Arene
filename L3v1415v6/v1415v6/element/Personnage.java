/**
 * 
 */
package element;


import interaction.Actions;
import interaction.Deplacements;
import interfaceGraphique.VueElement;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import utilitaires.Calculs;

/**
 * Un personnage: un element possedant des caracteristiques et etant capable
 * de jouer une strategie. 
 */
public class Personnage extends Element implements IPersonnage {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Reference du leader de ce personnage, -1 si aucun.
	 */
	private int leader;
	
	/**
	 * Reference des personnages de l'equipe de ce personnage. 
	 * Vide si le leader n'est pas egal a -1.
	 */
	private ArrayList<Integer> equipe;

	/**
	 * Constructeur d'un personnage avec un nom et une quantite de force et de charisme.
	 * Au depart, le personnage n'a ni leader ni equipe.
	 * @param nom
	 * @param force
	 * @param charisme
	 * @param hp
	 * @param vitesse
	 * @param defense
	 */
	public Personnage(String nom, int force, int charisme, int hp, int vitesse, int defense) {
		super(nom);
		ajouterCaract("force", force);
		ajouterCaract("charisme", charisme);
		
		/* -------------------------------------------
		 * Nouvelles stats
		 * ---------------------------------------- */
		ajouterCaract("hp", hp);
		ajouterCaract("vitesse", vitesse);
		ajouterCaract("defense", defense);
				
		leader = -1;
		equipe = new ArrayList<Integer>();
	}
	
	/* ----------------------------------------------------------
	 * ACCESSEURS
	 * ---------------------------------------------------------- */
	
	/** ---------------------------------------------------------
	 * 		public int getForce
	 *  ---------------------------------------------------------
	 * Retourne la valeur de force.
	 * @return bonus de force
	 *  ------------------------------------------------------ */
	public int getForce() {
		return getCaract("force");
	}
	
	
	/** ---------------------------------------------------------
	 * 		public int getCharisme
	 *  ---------------------------------------------------------
	 * Retourne la valeur de charisme.
	 * @return bonus de charisme
	 *  ------------------------------------------------------ */
	public int getCharisme() {
		return getCaract("charisme");
	}
	
	/** ---------------------------------------------------------
	 * 		public int getHP
	 *  ---------------------------------------------------------
	 * Retourne la valeur de la vie.
	 * @return bonus de vie
	 *  ------------------------------------------------------ */
	public int getHP() {
		return getCaract("hp");
	}
	
	/** ---------------------------------------------------------
	 * 		public int getVitesse
	 *  ---------------------------------------------------------
	 * Retourne la valeur de vitesse.
	 * @return bonus de vitesse
	 *  ------------------------------------------------------ */
	public int getVitesse() {
		return getCaract("vitesse");
	}
	
	/** ---------------------------------------------------------
	 * 		public int getDefense
	 *  ---------------------------------------------------------
	 * Retourne la valeur de defense.
	 * @return bonus de defense
	 *  ------------------------------------------------------ */
	public int getDefense(){
		return getCaract("defense");
	}

	
	/** ----------------------------------------------------------
	 * 		public int getLeader()
	 *  ----------------------------------------------------------
	 * Retourne le leader.
	 * @return leader (-1 si aucun)
	 *  ---------------------------------------------------------- */
	public int getLeader() {
		return leader;
	}

	
	/** ----------------------------------------------------------
	 * 		public ArrayList<Integer> getEquipe
	 *  ----------------------------------------------------------
	 * Retourne la liste des personnages de l'equipe.
	 * @return equipe
	 * -------------------------------------------------------- */
	public ArrayList<Integer> getEquipe() {
		return equipe;
	}
	

	@Override
	/** ----------------------------------------------------------
	 * 		public String toString
	 *  ----------------------------------------------------------
	 *  Cette méthode détermine l'affichage des différentes stats
	 *  ----------------------------------------------------------
	 * @return string
	 * ------------------------------------------------------- */
	public String toString(){
		String lead = (leader != -1)? ", leader: " + leader: "";
		String eq = "";
		
		if(!equipe.isEmpty()) {
			eq += ", equipe: ";
			
			for(int i = 0; i < equipe.size(); i++) {
				eq += equipe.get(i) + " ";
				
				if(i < equipe.size() - 1) {
					eq += " ";
				}
			}
		}
		
		return super.toString() + "[" + "HP : " + getHP() + ", For : " +  getForce() + ", Cha :" + getCharisme() 
				+  ", Def : " + getDefense() + ", Vit : " + getVitesse() +  lead + eq + "]";
	}
	
	
	
	
	@Override
	/** ----------------------------------------------------------
	 * 		public void setLeader()
	 *  ----------------------------------------------------------
	 * Cette méthode définie qui est le leader
	 * ------------------------------------------------------- */
	public void setLeader(int ref) throws RemoteException {
		leader = ref;
	}
	
	@Override
	/** ----------------------------------------------------------
	 * 		public void clearLeader()
	 *  ----------------------------------------------------------
	 * Cette méthode supprime le leader
	 * ------------------------------------------------------- */
	public void clearLeader() throws RemoteException {
		leader = -1;
	}
	
	@Override
	/** ----------------------------------------------------------
	 * 		public void ajouterEquipe(int ref)
	 *  ----------------------------------------------------------
	 * Cette méthode permet d'ajouter un personnage dans l'équipe
	 * -----------------------------------------------------------
	 * @param int ref
	 * ------------------------------------------------------- */
	public void ajouterEquipe(int ref) throws RemoteException {
		equipe.add((Integer) ref);
	}

	@Override
	/** ----------------------------------------------------------
	 * 		public void enleverEquipe(int ref)
	 *  ----------------------------------------------------------
	 * Cette méthode permet d'enlever un personnage dans l'équipe
	 * -----------------------------------------------------------
	 * @param int ref
	 * ------------------------------------------------------- */
	public void enleverEquipe(int ref) throws RemoteException {
		equipe.remove((Integer) ref);
	}

	@Override
	/** ----------------------------------------------------------
	 * 		public void enleverTouteEquipe()
	 *  ----------------------------------------------------------
	 * Cette méthode permet d'enlever toute l'équipe
	 * -------------------------------------------------------- */
	public void enleverTouteEquipe() throws RemoteException {
		equipe.clear();
	}
	
	
	/**
	 * Determine la direction de laquelle s'éloigner. 
	 * Par le calcul d'un baricentre pondéré positivement par les ennemis trop forts
	 * et négativement par les ennemis plus faible.
	 * 
	 * Attention les tableaux ne doivent pas être tous les deux vides.
	 * 
	 * @param tabFort tableau des positions des personnages plus fort
	 * @param tabFaible tableau des positions des personnages plus faible
	 * @return PosAFuir.
	 */
	private Point calculFuite (Point[] tabFort, Point[] tabFaible){
		Point posAFuir = new Point(0,0);
		int nb;
		for (nb = 0; nb < tabFort.length; ++nb) {
			posAFuir.translate((int) tabFort[nb].getX(), (int) tabFort[nb].getY());
		}
		for (int i = 0; i < tabFaible.length; i++) {
			posAFuir.translate((int) -tabFaible[i].getX(), (int) -tabFaible[i].getY());
			--nb;
		}
		posAFuir.move((int) posAFuir.getX()/nb, (int)posAFuir.getY()/nb);
		return posAFuir;
	}
	
	/**
	 * Calcule la direction dans laquelle fuir à partir d'une position et du point à fuir.
	 * 
	 * @param posMoi
	 * @param posAFuir
	 * @return direction dans laquelle fuir
	 */
	
	private Point dirFuite (Point posMoi, Point posAFuir){
		posMoi.move((int) (2*posMoi.getX() - posAFuir.getX()), (int) (2*posMoi.getY() - posAFuir.getY()));
		return (posMoi);
	}
	
	/**
	 * Renvoie un tableau de positions des voisins plus fort que l'element
	 * 
	 * @param voisins elements voisins de cet element
	 * @return tableau des voisins plus fort
	 * @throws RemoteException 
	 */
	private Point[] trouverPosVoisinsFort (Hashtable<Integer,VueElement> voisins) throws RemoteException {
		Point tabFort[] = new Point[1000];
		int key, i = 0;
		
		Set<Integer> set = voisins.keySet();
		Iterator<Integer> itr = set.iterator();
		while (itr.hasNext()) {
	    	key = itr.next();
	    	if(voisins.get(key).getControleur().getElement().getCaract("force") > this.getForce()
	    		&& voisins.get(key).getControleur().getElement().getCaract("charisme") >= this.getCharisme())
	    		tabFort[i] = voisins.get(key).getPoint();
	    		++i;
		}
		
		return tabFort;
	}
	
	/**
	 * Renvoie un tableau de positions des voisins plus faible que l'element
	 * 
	 * @param voisins elements voisins de cet element
	 * @return tableau des voisins plus faible
	 * @throws RemoteException 
	 */
	private Point[] trouverPosVoisinsFaible (Hashtable<Integer,VueElement> voisins) throws RemoteException {
		Point tabFaible[] =  new Point[1000];
		int key, i = 0;
		
		Set<Integer> set = voisins.keySet();
		Iterator<Integer> itr = set.iterator();
		while (itr.hasNext()) {
	    	key = itr.next();
	    	if(voisins.get(key).getControleur().getElement().getCaract("force") <= this.getForce()
	    		|| voisins.get(key).getControleur().getElement().getCaract("charisme") < this.getCharisme())
	    		tabFaible[i++] = voisins.get(key).getPoint();
		}
		
		return tabFaible;
	}
	
	/** --------------------------------------------------------------------
	 * 	public void strategie(VueElement ve, Hashtable<Integer,VueElement> voisins, Integer refRMI) throws RemoteException
	 *  --------------------------------------------------------------------
	 *  Classe à modifier pour la stratégie à adopter par votre personnage.
	 *  --------------------------------------------------------------------
	 * Met en place la strategie. On ne peut utiliser que les methodes de la 
	 * classe Actions.
	 * @param ve vue de l'element
	 * @param voisins element voisins de cet element
	 * @param refRMI reference attribuee a cet element par le serveur
	 * @throws RemoteException
	 */
	/*public void strategie(VueElement ve, Hashtable<Integer,VueElement> voisins, Integer refRMI) throws RemoteException {
        Actions actions = new Actions(ve, voisins); //je recupere les voisins (distance < 10)
        Deplacements deplacements = new Deplacements(ve,voisins);
        
        if (0 == voisins.size()) { // je n'ai pas de voisins, j'erre
        	parler("J'erre...", ve);
        	deplacements.seDirigerVers(0); //errer
            
        } else {
			VueElement cible = Calculs.chercherElementProche(ve, voisins);
			
			int distPlusProche = Calculs.distanceChebyshev(ve.getPoint(), cible.getPoint());
			
			int refPlusProche = cible.getRef();
			Element elemPlusProche = cible.getControleur().getElement();
			
			// dans la meme equipe ?
			boolean memeEquipe = false;
			
			if(elemPlusProche instanceof Personnage) {
				memeEquipe = (leader != -1 && leader == ((Personnage) elemPlusProche).getLeader()) || // meme leader
						leader == refPlusProche || // cible est le leader de this
						((Personnage) elemPlusProche).getLeader() == refRMI; // this est le leader de cible
			}
			
			if(distPlusProche <= 2) { // si suffisamment proches
				if(elemPlusProche instanceof Potion) { // potion
					// ramassage
					parler("Je ramasse une potion", ve);
					actions.ramasser(refRMI, refPlusProche, ve.getControleur().getArene());
					
				} else { // personnage
					if(!memeEquipe) { // duel seulement si pas dans la meme equipe (pas de coup d'etat possible dans ce cas)
						// duel
						parler("Je fais un duel avec " + refPlusProche, ve);
						actions.interaction(refRMI, refPlusProche, ve.getControleur().getArene());
					} else {
			        	parler("J'erre...", ve);
			        	deplacements.seDirigerVers(0); // errer
					}
				}
			} else { // si voisins, mais plus eloignes
				if(!memeEquipe) { // potion ou enemmi 
					// je vais vers le plus proche
		        	parler("Je vais vers mon voisin " + refPlusProche, ve);
		        	deplacements.seDirigerVers(refPlusProche);
		        	
				} else {
		        	parler("J'erre...", ve);
		        	deplacements.seDirigerVers(0); // errer
				}
			}
        }
	}*/
	public void strategie(VueElement ve, Hashtable<Integer,VueElement> voisins, Integer refRMI) throws RemoteException {
		
        Actions actions = new Actions(ve, voisins); //je recupere les voisins (distance < 10)
        Deplacements deplacements = new Deplacements(ve,voisins);
        
        // Si aucun voisins, j'erre
        if (0 == voisins.size()){
        	parler("J'erre...", ve);
        	deplacements.seDirigerVers(0);
        } else {			
			// Parcours de la table pour récupérer les voisins plus faibles
        	Point[] tabFaible = trouverPosVoisinsFaible(voisins);
        	
			// Parcours de la table pour récupérer les voisins plus forts
        	Point[] tabFort = trouverPosVoisinsFort(voisins);
        	
        	// Calcul de la direction à ne jamais aller pour ce tour
        	Point pointAFuir = calculFuite(tabFort, tabFaible);
        	
        	// Prend le voisin le plus proche de la hashtable voisins
			VueElement cible = Calculs.chercherElementProche(ve, voisins);
			
			int distPlusProche = Calculs.distanceChebyshev(ve.getPoint(), cible.getPoint());
			
			int refPlusProche = cible.getRef();
			Element elemPlusProche = cible.getControleur().getElement();
			
			// Dans la même équipe ?
			boolean memeEquipe = false;
			
			if(elemPlusProche instanceof Personnage) {
				memeEquipe = (leader != -1 && leader == ((Personnage) elemPlusProche).getLeader()) || // Même leader
						leader == refPlusProche || // cible est le leader de this
						((Personnage) elemPlusProche).getLeader() == refRMI; // this est le leader de cible
			}
			
			// Suffisamment proche pour interagir
			if(distPlusProche <= 2)
				if(elemPlusProche instanceof Potion) {
					parler("Je ramasse une potion", ve);
					actions.ramasser(refRMI, refPlusProche, ve.getControleur().getArene());
				} else // Personnage
					if(!memeEquipe)
						if(elemPlusProche.getCaract("charisme") > this.getCharisme()){
							parler("Je fais un duel avec " + refPlusProche, ve);
							actions.interaction(refRMI, refPlusProche, ve.getControleur().getArene());
						} else
							if(elemPlusProche.getCaract("charisme") <= this.getForce()){
								parler("Je fais un duel avec " + refPlusProche, ve);
								actions.interaction(refRMI, refPlusProche, ve.getControleur().getArene());
							} else
								deplacements.seDirigerVers(dirFuite(ve.getPoint(),cible.getPoint()));
					else // Coup d'état si on est sûr de gagner le duel
						if(elemPlusProche.getCaract("charisme") < this.getCharisme()){
							parler("Je fais un duel avec " + refPlusProche, ve);
							actions.interaction(refRMI, refPlusProche, ve.getControleur().getArene());
						} else {
							parler("J'erre prudemment...", ve);
			        		deplacements.seDirigerVers(dirFuite(ve.getPoint(),pointAFuir));
						}
			else // S'il y a des voisins mais plus éloignés : Potion on se rapproche, sinon on erre prudemment
				if(elemPlusProche instanceof Potion) {
					parler("Je vais vers la potion " + refPlusProche, ve);
	        		deplacements.seDirigerVers(refPlusProche);
				} else { // Personnage
					parler("J'erre prudemment...", ve);
					deplacements.seDirigerVers(dirFuite(ve.getPoint(),pointAFuir));
				}
        }
	}
}