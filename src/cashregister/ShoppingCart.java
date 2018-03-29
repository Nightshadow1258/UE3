/* 
 * Name: Paul Reisch
 * Matrikelnummer: 1529144
 */

package cashregister;

import java.util.Collection;

import container.Container;
import domain.product.IShoppingCartElement;

public class ShoppingCart implements IShoppingCart {

	private long id;
	private Container<IShoppingCartElement> shoppingCartElements = new Container<IShoppingCartElement>();
	
	public ShoppingCart(long id){
		this.id = id;
	}
	
	
	public void addElement(IShoppingCartElement shoppingCartElement) {
		this.shoppingCartElements.add(shoppingCartElement);
	}

	public Collection<IShoppingCartElement> currentElements() {
		return this.shoppingCartElements;
	}

	public int getNumberOfElements() {
		return this.shoppingCartElements.size();
	}

	public Long getShoppingCartID() {
		return this.id;
	}

	public float getTotalPriceOfElements() {
		float i = 0;
		for(IShoppingCartElement tmp : this.shoppingCartElements){
			i=i+tmp.getPrice();
		}
		return i;
	}

	public boolean removeElement(IShoppingCartElement element) {
		
		if(this.shoppingCartElements.contains(element))
			return false;
		else
		{
			this.shoppingCartElements.remove(element);
			return true;
		}		
	}
	
	public String toString(){
		return this.shoppingCartElements.toString();
	}

}
