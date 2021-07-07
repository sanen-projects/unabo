package online.sanen.unabo.api.exception;

import online.sanen.unabo.api.structure.enums.ProductType;

/**
 * 
 * @author LazyToShow
 * Date: 2018/06/12
 * Time: 09:17
 */
public class SupportsException extends RuntimeException{

	private static final long serialVersionUID = 9071750235589645848L;
	
	public SupportsException(ProductType productType) {
		super("Database types are not currently supported："+productType.toString());
	}


}
