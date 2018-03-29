/* 
 * Name: Paul Reisch
 * Matrikelnummer: 1529144
 */

package cashregister.ui;

import java.util.Collection;

import cashregister.IShoppingCart;
import domain.product.IProduct;
import domain.product.IShoppingCartElement;
import domain.record.IInvoice;
import domain.record.PaymentTransaction;
import tree.ITree;

public class CashRegisterConsoleUI implements ICashRegisterUI {

	public void displayProducts(ITree<IProduct> products) {
		products.generateConsoleView("__");

	}

	public void displayRecords(Collection<IInvoice> records) {
		System.out.println(records.toString());

	}

	public void displayShoppingCart(IShoppingCart shoppingCart) {
		
		for(IShoppingCartElement c : shoppingCart.currentElements()){
			System.out.println(c.getName() + " " + c.getName() );
		}

	}

	public void displayShoppingCarts(Collection<IShoppingCart> shoppingCarts) {
		
		for(IShoppingCart c : shoppingCarts){
			System.out.println(c.getShoppingCartID());
			displayShoppingCart(c);
		}

	}

	public void displayTransaction(PaymentTransaction transaction) {
		String c = "ID: " + transaction.getTransactionId() + " Provider: " + transaction.getPaymentProviderName();
		c = c + " Price: " + transaction.getPaidPrice() + " Time: " + transaction.getTimestamp();
		System.out.println(c);

	}

	public void pushUpdateInformation(ITree<IProduct> products, Collection<IShoppingCart> carts,
			Collection<IInvoice> records) {
	// nichts hier richtig??
	}

}
