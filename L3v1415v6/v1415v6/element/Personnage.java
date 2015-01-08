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

	private Point procDir;
	private int nbAvance;
	private boolean retour;
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
		nbAvance = 0;
		retour = false;
		procDir = null;
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
	 * 
	 * @param tabFort tableau NON VIDE des positions des personnages plus fort
	 * @return PosAFuir.
	 */
	private Point calculFuite (Point[] tabFort){
		Point posAFuir = new Point(0,0);
		int nb;
		for (nb = 0; nb < tabFort.length; ++nb) {
			posAFuir.translate( tabFort[nb].x,  tabFort[nb].y);
		}
		posAFuir.move( posAFuir.x/nb, posAFuir.y/nb);
		return posAFuir;
	}
	
	/**
	 * Calcule la norme 2 d'un vecteur
	 * 
	 * @param vec
	 * @return norme2
	 */
	private double norme(Point vec){
		return Math.sqrt(Math.pow(vec.x, 2)+ Math.pow(vec.y, 2));
	}
	
	/**
	 * calcule le produit scalaire de deux vecteurs
	 * 
	 * @param vecA
	 * @param vecB
	 * @return
	 */
	private double prodScal(Point vecA, Point vecB){
		return (vecA.x*vecB.x + vecA.y*vecB.y);
	}
	
	/**
	 * Calcule si un point se trouve dans un champs de 60° devant la direction de fuite.
	 * 
	 * @param posMoi
	 * @param dirFuite
	 * @param posLui
	 * @return booleen
	 */
	private boolean dansLeChamps(Point posMoi, Point dirFuite, Point posLui){
		Point vecMoiLui = new Point( (posLui.x - posMoi.x),(posLui.y - posMoi.y));
		Point vecMoiFuite = new Point( (dirFuite.x - posMoi.x),(dirFuite.y - posMoi.y));
		//|cos(angle)| <= sqrt(3)/2 
		//|angle| <= 30°
		return (Math.abs(prodScal(vecMoiLui, vecMoiFuite)/(norme(vecMoiLui)*norme(vecMoiFuite))) <= Math.sqrt(3)/2);
	}
	
	/**
	 * Calcule la direction dans laquelle fuir à partir d'une position et du point à fuir.
	 * En prenant en compte le fait qu'il ne faut pas fuir vers un ennemis plus fort que soit.
	 * 
	 * @param posMoi
	 * @param posAFuir
	 * @return direction dans laquelle fuir
	 */
	private Point dirFuite (Point posMoi, Point posAFuir, Point[] tabFort){
		Point dirFuite = new Point( (posMoi.x - posAFuir.x),  (posMoi.y - posAFuir.y));
		boolean chpLibre = true;
		int i = 0;
		double sqr3sur2 = Math.sqrt(3)/2;
		while (chpLibre && i < tabFort.length){
			chpLibre = chpLibre && (!dansLeChamps(posMoi, dirFuite, tabFort[i++]));
		}
		if (chpLibre)
			return (new Point((dirFuite.x+posMoi.x), (dirFuite.y + posMoi.y)));
		else{
			dirFuite.move(tabFort[--i].x - posMoi.x, tabFort[i].y - posMoi.y);
			dirFuite.move((int)(dirFuite.x * sqr3sur2 - dirFuite.y/2), (int)(dirFuite.x/2 + dirFuite.y * sqr3sur2));
			return dirFuite;
		}
			
	}
	
	/**
	 * Calcule si le perso à attaquer est plus fort que l'élement courant
	 * 
	 * @param attForce Perso à vérifier
	 * @param attCharisme Perso à vérifier
	 * @param defForce Element courant
	 * @param defCharisme Element courant
	 * @return booléen si le perso est plus fort ou non
	 */
	private boolean persoPlusFort(int attForce, int attCharisme){
		return (attForce >= this.getCharisme() || attCharisme > this.getForce());
	}
	
	/**
	 *  Calcule si le perso à attaquer est plus faible que l'élément courant
	 * @param attForce Perso à vérifier
	 * @param attCharisme Perso à vérifier
	 * @return booléen si le perso est plus faible ou non
	 */
	private boolean persoPlusFaible(int attForce, int attCharisme){
		return (attForce < this.getCharisme() || attCharisme <= this.getForce());
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
	    	if(voisins.get(key).getControleur().getElement() instanceof Personnage)
	    		if(persoPlusFort(voisins.get(key).getControleur().getElement().getCaract("force"),
	    		   voisins.get(key).getControleur().getElement().getCaract("charisme")))
	    			tabFort[i++] = voisins.get(key).getPoint();
		}
		
		return tabFort;
	}
	/**
	 * Calcul la direction dans laquelle doit errer le personnage afin de parcourir intelligemment la carte
	 * 
	 * @param dir
	 * @param pos
	 * @return nextDir
	 */
	private Point calculProcDir (Point dir, Point pos){
		int x =  pos.x;
		int y =  pos.y;
		
		//Premier déplacement
		if (dir == null){
			return new Point(1,0);
		}
		
		//Arrive en bas à droite
		if(x >= 90 && y >= 90){
			retour = true;
			return new Point(-1,-1);
		}
		
		//sur le chemin du retour
		if (retour)
			if(x < 10)
				if(y<10){
					retour = false;
					return new Point (1,0);
				}else{
					return new Point (0, -1);
				}
			else if (y < 10)
				return new Point (-1,0);
			else
				return new Point(-1,-1);

		//En chemin
		if (x >= 90){
			if(nbAvance <= 20){
				++nbAvance;
				return new Point(0,1);
			}else{
				nbAvance = 0;
				return new Point(-1, 0);
			}
		}else if (x < 10){
			if (nbAvance <= 20 && y < 90){
				++nbAvance;
				return new Point(0,1);
			}else{
				nbAvance = 0;
				return new Point(1, 0);
			}
		}else
			return dir;
	}
	/**
	 * Renvoie un booleen vrai si la potion est utile au personnage selon sa strat orienté vers la force.
	 * 
	 * @param elemPlusProche
	 * @return booleen
	 */
	private boolean potionUtile(Element elemPlusProche){
		return(elemPlusProche.getCaract("force") == 0 
					&& elemPlusProche.getCaract("vie") >= 0 
					&& elemPlusProche.getCaract("vie") <= 100-this.getVie());
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
	public void strategie(VueElement ve, Hashtable<Integer,VueElement> voisins, Integer refRMI) throws RemoteException {
		
        Actions actions = new Actions(ve, voisins); //je recupere les voisins (distance < 10)
        Deplacements deplacements = new Deplacements(ve,voisins);
        Point pos = ve.getPoint();
        
        // Si aucun voisins, j'erre
        if (0 == voisins.size()){
        	parler("J'erre intelligemment...", ve);
        	procDir = calculProcDir(procDir, pos);
        	deplacements.seDirigerVers(new Point((procDir.x+pos.x), (procDir.y + pos.y)));
        } else {
			// Parcours de la table pour récupérer les voisins plus forts
        	Point[] tabFort = trouverPosVoisinsFort(voisins);
        	
        	// Calcul de la direction à ne jamais aller pour ce tour
        	Point pointAFuir = calculFuite(tabFort);
        	
        	// Prend le voisin le plus proche de la hashtable voisins
			VueElement cible = Calculs.chercherElementProche(ve, voisins);
			
			int distPlusProche = Calculs.distanceChebyshev(pos, cible.getPoint());
			
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
					if (potionUtile(elemPlusProche)){
						parler("Je ramasse une potion", ve);
						actions.ramasser(refRMI, refPlusProche, ve.getControleur().getArene());
					}else{
						parler("J'erre prudemment...", ve);
						deplacements.seDirigerVers(dirFuite(pos,pointAFuir, tabFort));
					}
				} else // Personnage
					if(!memeEquipe)
						if(persoPlusFaible(elemPlusProche.getCaract("force"),elemPlusProche.getCaract("charisme"))){
							parler("Je fais un duel avec " + refPlusProche, ve);
							actions.interaction(refRMI, refPlusProche, ve.getControleur().getArene());
						} else
							deplacements.seDirigerVers(dirFuite(pos,cible.getPoint(), tabFort));
					else // Coup d'état si on est sûr de gagner le duel
						if(elemPlusProche.getCaract("charisme") < this.getCharisme()){
							parler("Je fais un duel avec " + refPlusProche, ve);
							actions.interaction(refRMI, refPlusProche, ve.getControleur().getArene());
						} else {
							parler("J'erre prudemment...", ve);
			        		deplacements.seDirigerVers(dirFuite(pos,pointAFuir,tabFort));
						}
			else // S'il y a des voisins mais plus éloignés : Potion on se rapproche, sinon on erre prudemment
				if(elemPlusProche instanceof Potion ){
					if(potionUtile(elemPlusProche)) {
						parler("Je vais vers la potion " + refPlusProche, ve);
						deplacements.seDirigerVers(refPlusProche);
					}else{
						parler("J'erre prudemment...", ve);
						deplacements.seDirigerVers(dirFuite(pos,pointAFuir,tabFort));
					}
				} else { // Personnage
					if(persoPlusFort(elemPlusProche.getCaract("force"),elemPlusProche.getCaract("charisme"))){
						parler("J'erre prudemment...", ve);
						deplacements.seDirigerVers(dirFuite(pos,pointAFuir,tabFort));
					} else {
						deplacements.seDirigerVers(refPlusProche);
					}
				}
        }
	}
}