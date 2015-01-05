/**
 * 
 */
package element;

/**
 * @author lois
 *
 */
public class LifePot extends Potion {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LifePot (){
		super("Potion de vie", 0, 0);
	}	
	
	public String toString(){
		return nom + "[ Ajoute une vie ]";
	}
}
