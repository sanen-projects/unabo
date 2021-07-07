package online.sanen.unabo.core.handle;

import java.util.Collection;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.enums.ProductType;
import online.sanen.unabo.template.SqlTemplate;


/**
 * 
 *
 * @author LazyToShow <br>
 *         Date: 2018/09/14 <br>
 *         Time: 10:09:06
 */
public class CreateAndInsertSqlHandler implements SimpleHandler,Handel {

	String newTableName;

	public CreateAndInsertSqlHandler(String newTableName) {
		this.newTableName = newTableName;
	}

	@Override
	public Object handel(ChannelContext structure, Object product) {
		String modifier = ProductType.applyTableModifier(structure.productType());
		structure.getSql().insert(0, "CREATE TABLE " + modifier + newTableName + modifier + " AS ");
		Collection<Object> paramers = structure.getParamers();
		SqlTemplate template = (SqlTemplate) structure.getTemplate();
		template.update(structure.getSql().toString(), paramers.toArray());
		return this.getSql(structure);
	}

}
