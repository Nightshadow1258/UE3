/* 
 * Name: Paul Reisch
 * Matrikelnummer: 1529144
 */

package cashregister;

public class CashRegisterFactory {

	private static long CASH_REGISTER_ID = 0 ;
	
	public CashRegisterFactory(){
		
	}
	
	public static ICashRegister createCashRegister(){
		return new CashRegister(CASH_REGISTER_ID++);
	}
}
