/* 
 * Name: Paul Reisch
 * Matrikelnummer: 1529144
 */

package bonus;

import domain.record.IInvoice;

import java.util.HashMap;

public class template {
	
		public HashMap<String, Object> hmap;
	
		public template(IInvoice invoice) {
			
			
			hmap = new HashMap<String, Object>();
			hmap.put("time", invoice.getPaymentTransaction().getTimestamp().toString());
			hmap.put("id", invoice.getPaymentTransaction().getTransactionId().toString());
			hmap.put("provider", invoice.getPaymentTransaction().getPaymentProviderName().toString()); 
			hmap.put("gpreis", invoice.getPaymentTransaction().getPaidPrice().toString());
			hmap.put("products" , invoice.attachedShoppingCardElements());
		    
		}

}