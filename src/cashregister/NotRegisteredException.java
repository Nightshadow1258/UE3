/* 
 * Name: Paul Reisch
 * Matrikelnummer: 1529144
 */

package cashregister;

public class NotRegisteredException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public NotRegisteredException(String message){
		super(message);
	}
}
