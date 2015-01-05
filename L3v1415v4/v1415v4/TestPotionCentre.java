import java.rmi.RemoteException;

import controle.Console;
import element.Element;
import element.LifePot;

/**
 * 
 */

/**
 * @author lois
 *
 */
public class TestPotionCentre {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			int port=5099;	//par defaut, port de l'arene=5099
			if (args.length!=0) port=Integer.parseInt(args[0]);
			String ipArene = "localhost";
			if (args.length!=0) if (args[1]!="") ipArene=args[1];
			Element potionDeVie = new LifePot();
	
			new Console(potionDeVie, 40, 40, port,ipArene);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
