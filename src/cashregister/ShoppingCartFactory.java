/* 
 * Name: Paul Reisch
 * Matrikelnummer: 1529144
 */

package cashregister;

public class ShoppingCartFactory {

	static private long SHOPPING_CART_ID = 0;	
	
	public ShoppingCartFactory(){
		
	}
	
	public static IShoppingCart createShoppingCart(){
		return new ShoppingCart(SHOPPING_CART_ID++);
	}
}
