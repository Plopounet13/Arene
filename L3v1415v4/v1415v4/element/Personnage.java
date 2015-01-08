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
	
	private float vitesse;

	/**
	 * Constructeur d'un personnage avec un nom et une quantite de force, de charisme, d'armure et de determination.
	 * Au depart, le personnage n'a ni leader ni equipe.
	 * @param nom
	 * @param force
	 * @param charisme
	 * @param armure
	 * @param determination
	 * @param vitesse
	 */
	public Personnage(String nom, int force, int charisme, int armure, int deter, float vitesse) {
		super(nom);
		ajouterCaract("force", force);
		ajouterCaract("charisme", charisme);
		ajouterCaract("armure", armure);
		ajouterCaract("deter", deter);
		this.vitesse = vitesse;

		leader = -1;
		equipe = new ArrayList<Integer>();
	}
	
	/**
	 * Retourne la valeur de force.
	 * @return bonus de force
	 */
	public int getForce() {
		return getCaract("force");
	}
	
	/**
	 * Retourne la valeur d'armure.
	 * @return bonus d'armure
	 */
	public int getArmure() {
		return getCaract("armure");
	}
	
	/**
	 * Retourne la valeur de charisme.
	 * @return bonus de charisme
	 */
	public int getCharisme() {
		return getCaract("charisme");
	}
	
	/**
	 * Retourne la valeur de determination.
	 * @return bonus de determination
	 */
	public int getDeter() {
		return getCaract("deter");
	}
	
	/**
	 * Retourne la valeur de vitesse.
	 * @return bonus de vitesse
	 */
	public float getVitesse() {
		return vitesse;
	}

	/**
	 * Retourne le leader.
	 * @return leader (-1 si aucun)
	 */
	public int getLeader() {
		return leader;
	}

	/**
	 * Retourne la liste des personnages de l'equipe.
	 * @return equipe
	 */
	public ArrayList<Integer> getEquipe() {
		return equipe;
	}
	

	@Override
	public String toString(){
		String lead = (leader != -1)? ", leader: " + leader: "";
		String eq = "";
		
		if(!equipe.isEmpty()) {
			eq += ", equipe: ";
			
			for(int i = 0; i < equipe.size(); i++) {
				eq += equipe.get(i);
				
				if(i < equipe.size() - 1) {
					eq += " ";
				}
			}
		}
		
		return super.toString() + "[" + getVie() + ", " + getForce() + ", " + getCharisme() + ", " + getArmure() + ", " + getDeter() + lead + eq + "]";
	}
	
	
	
	
	@Override
	public void setLeader(int ref) throws RemoteException {
		leader = ref;
	}
	
	@Override
	public void clearLeader() throws RemoteException {
		leader = -1;
	}
	
	@Override
	public void ajouterEquipe(int ref) throws RemoteException {
		equipe.add((Integer) ref);
	}

	@Override
	public void enleverEquipe(int ref) throws RemoteException {
		equipe.remove((Integer) ref);
	}

	@Override
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
	@SuppressWarnings("unused")
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
	
	@SuppressWarnings("unused")
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
	@SuppressWarnings("unused")
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
	@SuppressWarnings("unused")
	private Point[] trouverPosVoisinsFaible (Hashtable<Integer,VueElement> voisins) throws RemoteException {
		Point tabFaible[] = new Point[1000];
		int key, i = 0;
		
		Set<Integer> set = voisins.keySet();
		Iterator<Integer> itr = set.iterator();
		while (itr.hasNext()) {
	    	key = itr.next();
	    	if(voisins.get(key).getControleur().getElement().getCaract("force") <= this.getForce()
	    		|| voisins.get(key).getControleur().getElement().getCaract("charisme") < this.getCharisme())
	    		tabFaible[i] = voisins.get(key).getPoint();
	    		++i;
		}
		
		return tabFaible;
	}
	
	/**
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
	}
}
