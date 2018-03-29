/* 
 * Name: Paul Reisch
 * Matrikelnummer: 1529144
 */

package cashregister;

public class ShoppingCartNotFoundException extends Exception{

	private static final long serialVersionUID = 1L;

	 ShoppingCartNotFoundException(long a, long b){ // a = shoppingcardID	b = CashregisterIsD
		 System.out.println("Error: The Cart " + a + "could not be found in the Cashregister" + b +" !");
	 }
}
