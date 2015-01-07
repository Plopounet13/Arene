package element;

public class Jesus extends Personnage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Jesus (){
		super("Jesus", 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, 4);
		System.out.println("Halleluja !");
		setVie(2);
	}
}
