/* 
 * Name: Paul Reisch
 * Matrikelnummer: 1529144
 */

package cashregister;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import bonus.QRCodeGenerator;
import bonus.template;
import cashregister.ui.ICashRegisterUI;
import container.*;
import domain.product.IShoppingCartElement;
import domain.product.Product;
import domain.record.IInvoice;
import domain.record.Invoice;
import managementserver.ISubjectManagementServer;
import paymentprovider.IPayment;
import tree.*;
import util.Tuple;
import domain.product.*;

public class CashRegister implements ICashRegister, IObserver {

	private long id;
	private Container<IInvoice> records = new Container<IInvoice>();
	private Container<ICashRegisterUI> uis = new Container<ICashRegisterUI>();
	private ITree<IProduct> products = new ProductTree();
	private Container<Tuple<ISubjectManagementServer, Boolean>> subjects = new Container<Tuple<ISubjectManagementServer, Boolean>>();
	private Container<IShoppingCart> shoppingCarts = new Container<IShoppingCart>();

	public CashRegister(long id) {
		this.id = id;
	}

	public void activateNotifications(ISubjectManagementServer subject) {

		for (Tuple<ISubjectManagementServer, Boolean> tmp : this.subjects) {
			if (tmp.equals(subject)) {
				tmp.setValueB(true);
				return;
			}
		}
		if (subject != null)
			this.subjects.add(new Tuple<ISubjectManagementServer, Boolean>(subject, true));
	}

	public void deactivateNotifications(ISubjectManagementServer subject) {
		for (Tuple<ISubjectManagementServer, Boolean> tmp : this.subjects) {
			if (tmp.equals(subject)) {
				tmp.setValueB(false);
				return;
			}
		}
		if (subject != null)
			this.subjects.add(new Tuple<ISubjectManagementServer, Boolean>(subject, false));
	}

	public void notifyChange(ISubjectManagementServer subject) {

		for (Tuple<ISubjectManagementServer, Boolean> tmp : this.subjects) {
			if (tmp.valueA.equals(subject) && tmp.valueB == true) {
				this.products = subject.getChanges();
			}
		}
	}

	public boolean addProductToShoppingCart(long id, IShoppingCartElement element) {

		for (IShoppingCart tmp : this.shoppingCarts) {
			if (tmp.getShoppingCartID().equals(id)) {
				tmp.addElement(element.deepCopy());
				return true;
			}
		}
		return false;
	}

	public Long addShoppingCart() {

		IShoppingCart tmp = ShoppingCartFactory.createShoppingCart();
		shoppingCarts.add(tmp);
		return tmp.getShoppingCartID();
	}

	public void displayProducts() {
		for (ICashRegisterUI tmp : this.uis) {
			tmp.displayProducts(this.products);
		}
	}

	public void displayRecords() {
		for (ICashRegisterUI ui : this.uis) {
			ui.displayRecords(this.records);
		}
	}

	public void displayShoppingCart(long id) {
		for (IShoppingCart tmp : this.shoppingCarts) {
			if (tmp.getShoppingCartID().equals(id)) {
				for (ICashRegisterUI ui : this.uis) {
					ui.displayShoppingCart(tmp);
				}
			}
		}
	}

	public void displayShoppingCarts() {
		for (ICashRegisterUI tmp : this.uis) {
			tmp.displayShoppingCarts(this.shoppingCarts);
		}
	}

	public long getID() {
		return this.id;
	}

	public Collection<IShoppingCart> getShoppingCarts() {

		return this.shoppingCarts;
	}

	public IInvoice payShoppingCart(long id, IPayment provider) throws ShoppingCartNotFoundException {

		for (IShoppingCart tmp : this.shoppingCarts) {
			if (tmp.getShoppingCartID().equals(id)) {
				IInvoice invoice = new Invoice();

				Container<IShoppingCartElement> listofelements = new Container<IShoppingCartElement>();
				for (IShoppingCartElement temp : tmp.currentElements()) {
					listofelements.add(temp.deepCopy());
				}

				invoice.setInvoiceProducts(listofelements);
				invoice.addPaymentTransaction(provider.pay(tmp.getTotalPriceOfElements()));
				tmp.currentElements().removeAll(tmp.currentElements());
				this.records.add(invoice);

				try {
					createHtmlFile(invoice);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return invoice;
			}
		}
		throw new ShoppingCartNotFoundException(id, this.id);
	}

	private void createHtmlFile(IInvoice invoice) throws IOException {

		// create QRCode
		File file = new File("bonus/QRCode"+invoice.getPaymentTransaction().getTransactionId()+".png");;
		FileOutputStream fop = new FileOutputStream(file);
		
		if (!file.exists()) {
			file.createNewFile();
		}

		QRCodeGenerator.generateQRCode(fop, invoice.toString());

		fop.flush();
		fop.close();

		if (fop != null) {
			fop.close();
		}

		// create html file with the QRCode
		File html = new File("bonus/Rechnung"+invoice.getPaymentTransaction().getTransactionId()+".html");

		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile("bonus/template");

		mustache.execute(new PrintWriter(html), (new template(invoice)).hmap).flush();

		Desktop.getDesktop().browse(html.toURI());
		
	}

	public void registerCashRegisterUI(ICashRegisterUI ui) {
		this.uis.add(ui);
	}

	public IShoppingCartElement selectProduct(String product) {

		for (IShoppingCart tmp : this.shoppingCarts) {
			for (IShoppingCartElement elem : tmp.currentElements()) {
				if (elem.getName().equals(product)) {
					return elem;
				}
			}
		}
		return null;
	}

	public IShoppingCartElement selectProduct(Product product) {

		for (IShoppingCart tmp : this.shoppingCarts) {
			for (IShoppingCartElement elem : tmp.currentElements()) {
				if (elem.equals(product)) {
					return elem;
				}
			}
		}
		return null;
	}

	public void unregisterCashRegisterUI(ICashRegisterUI ui) {
		for (ICashRegisterUI tmp : this.uis) {
			if (tmp.equals(ui)) {
				this.uis.remove(tmp);
			}

		}
	}

}
