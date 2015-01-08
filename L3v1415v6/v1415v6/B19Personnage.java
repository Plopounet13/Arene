import java.rmi.RemoteException;

import controle.Console;
import element.Personnage;

/**
 * @author Bernard/Paulin
 *
 */
public class B19Personnage {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			int port = 8000;
			if (args.length > 0) {
				port = Integer.parseInt(args[0]);
			}
			
			String ipArene = "vm-113-239";
			if (args.length > 1) { 
				ipArene = args[1];
			}
			
			Personnage bidule = new Personnage("B19-Huh!", 99, 0, 1, 1, 0);
			
			new Console(bidule, 40, 40, port, ipArene);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
