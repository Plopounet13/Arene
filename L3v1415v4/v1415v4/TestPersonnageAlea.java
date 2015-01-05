

import java.rmi.RemoteException;
import java.util.Random;

import controle.Console;
import element.Personnage;
import element.Politicien;
import element.Barbare;
import element.Envouteur;
//import element.Jesus;
import element.Paladin;
import element.Rodeur;

//import element.Element;
//import element.LifePot;

/**
 * Test de la Console avec un Element qui s'ajoute a l'Arene (apres lancement Arene et IHM). A lancer en plusieurs exemplaires.
 */
public class TestPersonnageAlea {

	/**
	 * @param args
	 * @throws RemoteException 
	 */
	public static void main(String[] args) {
		
		try {
			int port=5099;	//par defaut, port de l'arene=5099
			if(args.length != 0) port = Integer.parseInt(args[0]);
			
			String ipArene = "localhost";
			if(args.length != 0) if(args[1] != "") ipArene = args[1];
	
			Personnage politicien = new Politicien("Valls");
			//Personnage jesus = new Jesus();
			Personnage barbare = new Barbare("Crom");
			Personnage envouteur = new Envouteur("Envout");
			Personnage paladin = new Paladin("Palouf");
			Personnage rodeur = new Rodeur("Rodeur");
			
			//Element potionDeVie = new LifePot();
			
			Random r = new Random();
			new Console(politicien, r.nextInt(100), r.nextInt(100), port, ipArene);
			//new Console(jesus, r.nextInt(100), r.nextInt(100), port, ipArene);
			new Console(barbare, r.nextInt(100), r.nextInt(100), port, ipArene);
			new Console(envouteur, r.nextInt(100), r.nextInt(100), port, ipArene);
			new Console(paladin, r.nextInt(100), r.nextInt(100), port, ipArene);
			new Console(rodeur, r.nextInt(100), r.nextInt(100), port, ipArene);
			//new Console(potionDeVie, 40, 40, port, ipArene);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
