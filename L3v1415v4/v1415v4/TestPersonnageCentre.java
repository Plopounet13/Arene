


import java.rmi.RemoteException;

import controle.Console;
//import element.Element;
//import element.Potion;
//import element.LifePot;
//import element.MixPot;
import element.Personnage;
import element.Barbare;

/**
 * Test de la Console avec un Element qui s'ajoute a l'Arene (apres lancement Arene et IHM). A lancer en plusieurs exemplaires.
 */
public class TestPersonnageCentre {

	/**
	 * @param args
	 * @throws RemoteException 
	 */
	public static void main(String[] args) {

		try {
			int port=5099;	//par defaut, port de l'arene=5099
			
			if (args.length!=0) port=Integer.parseInt(args[0]);
			
			String ipArene = "localhost";
			if (args.length!=0) if (args[1]!="") ipArene=args[1];
			
			Personnage truc = new Barbare("Crom");
			//Personnage truc2 = new Rodeur("Aragorn");
			//Element potionDeVie = new LifePot();
			//Element pot = new MixPot();
			//Element potion = new Potion("Potion",50,40);
			
			//new Console(potionDeVie, 50, 40, port, ipArene);
			//new Console(pot, 45, 40, port, ipArene);
			//new Console(potion, 50, 40, port, ipArene);
			//new Console(truc2, 35, 30, port, ipArene);
			new Console(truc, 40, 45, port, ipArene);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
