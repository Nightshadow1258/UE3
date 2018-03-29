/* 
 * Name: Paul Reisch
 * Matrikelnummer: 1529144
 */

package managementserver;

import java.util.Collection;

import cashregister.CashRegister;
import cashregister.ICashRegister;
import cashregister.IObserver;
import cashregister.NotRegisteredException;
import container.Container;
import domain.product.IProduct;
import domain.product.JointProduct;
import domain.product.ProductCategory;
import tree.ITree;
import tree.ProductTree;
import tree.node.CategoryTreeNode;
import tree.node.ITreeNode;
import tree.node.ProductCategoryTreeNode;
import tree.node.ProductTreeNode;
import util.searchable.ProductNameFilter;
import warehouse.IWarehouseListener;

public class ManagementServer implements IManagementServer, ISubjectManagementServer, IWarehouseListener {

	private Container<ICashRegister> cashRegister = new Container<ICashRegister>();
	private Container<IObserver> observer = new Container<IObserver>();
	private ITree<IProduct> productAssortment;
	private static ManagementServer INSTANCE = null;

	private ManagementServer() {
		this.productAssortment = new ProductTree();
		this.productAssortment.setRoot(new CategoryTreeNode<IProduct, String>("Products"));
		this.productAssortment.getRoot().getChildren().add(new ProductCategoryTreeNode<IProduct>(ProductCategory.FOOD));
		this.productAssortment.getRoot().getChildren().add(new ProductCategoryTreeNode<IProduct>(ProductCategory.BEVERAGE));
		this.productAssortment.getRoot().getChildren().add(new ProductCategoryTreeNode<IProduct>(ProductCategory.DEFAULT));
	}

	public static IManagementServer GET_INSTANCE() {
		if (INSTANCE == null) {
			INSTANCE = new ManagementServer();
		}
		return INSTANCE;
	}

	public ITree<IProduct> getChanges() {
		return this.productAssortment.deepCopy();
	}

	public boolean register(IObserver obs) {
		if (obs == null) {
			return false;
		}
		if (this.observer.contains(obs)) {
			obs.activateNotifications(this);
			obs.notifyChange(this);
			return false;
		} else {
			this.observer.add(obs);
			obs.activateNotifications(this);
			obs.notifyChange(this);
			return true;
		}
	}

	public boolean unregister(IObserver obs) {
		if (this.observer.contains(obs)) {
			this.observer.remove(obs);
			obs.deactivateNotifications(this);
			return true;
		}
		return false;
	}

	public void addCashRegister(ICashRegister cashRegister) {
		if (cashRegister == null)
			return;

		if (!this.cashRegister.contains(cashRegister)) {
			this.cashRegister.add(cashRegister);
			if ((CashRegister) cashRegister instanceof IObserver) {
				this.register((IObserver) cashRegister);
			}
		}
		return;
	}

	public void notifyChange(IProduct product) {
		
		if (product != null) {
			Collection<ITreeNode<IProduct>> tmp; // liste von gefunden Elementen
													// mit object

			tmp = this.productAssortment.searchByFilter(new ProductNameFilter(), product);

			for (ITreeNode<IProduct> it : tmp) { // Iteration der gefunden
													// Matches

				if (it instanceof JointProduct) { // Fall: JointProduct
					this.FallJointProduct((JointProduct) product, it);
				}

				// Fall: Kein jointProduct

				if (it.nodeValue().getCategory().equals(product.getCategory())) {// Categories
					// gleich
					// System.out.println("die selbe Category");
					it.nodeValue().update(product); // scheint zu gehen

				} else // nicht die selbe Category
				{
					for (ITreeNode<IProduct> catelist : this.productAssortment.getRoot().getChildren()) {
						if (!((ProductCategoryTreeNode<IProduct>) catelist).getCategory()
								.equals(it.nodeValue().getCategory())) {
							for (ITreeNode<IProduct> delete : this.productAssortment.getRoot().getChildren()) {
								if (delete.getChildren().contains(it)) {
									delete.removeNodeByNode(it);
									//System.out.println("nicht die selbe Category" + test);
								}
							}
							// add Funktion
							for (ITreeNode<IProduct> add : this.productAssortment.getRoot().getChildren()) {
								if (product.getCategory()
										.equals(((ProductCategoryTreeNode<IProduct>) add).getCategory())) {
									add.getChildren().add(new ProductTreeNode(product));
									return;
								}

								if (ProductCategory.DEFAULT
										.equals(((ProductCategoryTreeNode<IProduct>) add).getCategory())
										&& product.getCategory() != ProductCategory.BEVERAGE
										&& product.getCategory() != ProductCategory.FOOD) {
									add.getChildren().add(new ProductTreeNode(product));
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	private void FallJointProduct(JointProduct product, ITreeNode<IProduct> tmpNode) {
		// Producktliste vom JointProduct
		boolean treffer = false;

		for (IProduct prod : product.getProducts()) {

			treffer = true;
			// Subtree vom übergebenen Knoten
			for (ITreeNode<IProduct> kind : tmpNode.getChildren()) {
				
				if (prod.equals(kind.nodeValue())) { // gefunden!
					treffer = true;

					if (kind.nodeValue() instanceof JointProduct) // JointProduct
						FallJointProduct((JointProduct) prod, kind);
				}
			}
			if(treffer == false){
				tmpNode.getChildren().add(new ProductTreeNode(prod));
			}
		}
	}

	public void productAdded(IProduct product) {
		if (product == null)
			return;

		for (ITreeNode<IProduct> tmp : this.productAssortment.getRoot().getChildren()) {
			if (product.getCategory().equals(((ProductCategoryTreeNode<IProduct>) tmp).getCategory())) {
				tmp.getChildren().add(new ProductTreeNode(product));
				this.propagateProducts();
				return;
			}

			if (((ProductCategoryTreeNode<IProduct>) tmp).getCategory().equals(ProductCategory.DEFAULT)
					&& product.getCategory() != ProductCategory.BEVERAGE
					&& product.getCategory() != ProductCategory.FOOD) {
				tmp.getChildren().add(new ProductTreeNode(product));
				this.propagateProducts();
				return;
			}
		}

	}

	public void productRemoved(IProduct product) {
		this.productAssortment.removeNode(product);
		this.propagateProducts();
	}

	public void propagateProducts() {
		for (IObserver tmp : this.observer) {
			tmp.notifyChange(INSTANCE);
		}
	}

	public ITree<IProduct> retrieveProductSortiment() {
		return this.productAssortment.deepCopy();
	}

	public ICashRegister retrieveRegisteredCashRegister(Long cashRegisterId) throws NotRegisteredException {
		for (ICashRegister tmp : this.cashRegister) {
			if (tmp.getID() == cashRegisterId) {
				return tmp;
			}
		}
		throw new NotRegisteredException("NotRegisteredException");
	}

	public Collection<ICashRegister> retrieveRegisteredCashRegisters() {
		return this.cashRegister;
	}

	public void unregisterCashRegister(ICashRegister cashRegister) throws NotRegisteredException {
		if (cashRegister == null)
			return;

		if (this.cashRegister.remove(cashRegister)) {
			if (this.unregister((IObserver) cashRegister))
				return;
		}

		throw new NotRegisteredException("NotRegisteredException");
	}

}
