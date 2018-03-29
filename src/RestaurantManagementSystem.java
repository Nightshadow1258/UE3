
/* 
 * Name: Paul Reisch
 * Matrikelnummer: 1529144
 */

import cashregister.CashRegisterFactory;
import cashregister.ICashRegister;
import cashregister.ui.CashRegisterGUI;
import domain.product.JointProduct;
import domain.product.ProductCategory;
import domain.product.SimpleProduct;
import managementserver.IManagementServer;
import managementserver.ManagementServer;
import managementtools.ManagementServerViewer;
import warehouse.IWarehouse;
import warehouse.IWarehouseListener;
import warehouse.Warehouse;
import warehouse.ui.WarehouseManager;

public class RestaurantManagementSystem {

	public static void main(String[] args) {

//		// Create ManagementServer and Warehouse
		IManagementServer mngServer = ManagementServer.GET_INSTANCE();
		IWarehouse warehouse = Warehouse.GET_INSTANCE();
		
		// TODO: register mngServer as listener at the warehouse
		((Warehouse) warehouse).registerListener((IWarehouseListener)mngServer);

		// TODO: add Products to warehouse
		JointProduct jprod = new JointProduct("Menue", (float) 10);
		SimpleProduct sp1= new SimpleProduct("Schnitzel", (float) 11.99);
		SimpleProduct sp2= new SimpleProduct("Apfelsaft", (float) 2.99);
				

		SimpleProduct kuchen = new SimpleProduct("Sachertorte", (float) 4.99);
		kuchen.setCategory(ProductCategory.FOOD);
		
		
		jprod.addProduct(sp1);
		jprod.addProduct(sp2);
		jprod.addProduct(kuchen);
		
		warehouse.addProduct(sp1);
		warehouse.addProduct(sp2);
		warehouse.addProduct(kuchen);
		warehouse.addProduct(jprod);
		
		// TODO: create CashRegister and register it as an observer at the mngServer
		ICashRegister cashReg = CashRegisterFactory.createCashRegister();
		mngServer.addCashRegister(cashReg);
		
		// TODO: Create GUI for CashRegister
		CashRegisterGUI cashRegUI = new CashRegisterGUI(cashReg);
		
		new WarehouseManager(warehouse);
		new ManagementServerViewer(ManagementServer.GET_INSTANCE());

		// TODO: register CashRegisterGUI as an UI at the previously created cashRegister
		cashReg.registerCashRegisterUI(cashRegUI);
	}
}
