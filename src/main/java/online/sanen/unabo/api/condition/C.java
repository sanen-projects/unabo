package online.sanen.unabo.api.condition;


import static online.sanen.unabo.api.condition.Condition.Cs.*;

import java.util.function.Consumer;

import online.sanen.unabo.api.condition.Condition.Associated;
import online.sanen.unabo.api.condition.Condition.Cs;

/**
 * Condition build
 * 
 * @author LazyToShow
 * Date: 2018/06/12
 * Time: 09:17
 */
public class C{
	
	/**
	 * @param fieldName
	 * @param cs - {@link Cs}
	 * @return
	 */
	public static SimpleCondition buid(String fieldName, Cs cs) {
		return new SimpleCondition(fieldName, cs);
	}
	
	/**
	 * @param fieldName
	 * @param cs - {@link Cs}
	 * @param associated - {@link Associated}
	 * @return
	 */
	public static SimpleCondition buid(String fieldName, Cs cs, Associated associated) {
		return new SimpleCondition(fieldName, cs, associated);
	}
	
	
	/**
	 * @param fieldName
	 * @param cs
	 * @param value
	 * @return
	 */
	public static SimpleCondition buid(String fieldName, Cs cs, Object value) {
		return new SimpleCondition(fieldName, cs, value);
	}
	
	

	/**
	 * @param fieldName
	 * @param cs - {@link Cs}
	 * @param value
	 * @param associated - {@link Associated}
	 * @return
	 */
	public static SimpleCondition buid(String fieldName, Cs cs, Object value, Associated associated) {
		return new SimpleCondition(fieldName, cs, value, associated);
	}
	
	/**
	 * @param composite
	 * @return
	 */
	public static CompositeCondition buid(Consumer<CompositeCondition> composite) {
		CompositeCondition compositeCondition = new CompositeCondition();
		composite.accept(compositeCondition);
		
		return compositeCondition;
	}
	
	public static CompositeCondition buid(Consumer<CompositeCondition> composite,Associated associated) {
		CompositeCondition compositeCondition = new CompositeCondition();
		compositeCondition.setAssociated(associated);
		composite.accept(compositeCondition);
		
		return compositeCondition;
	}
	
	public static CompositeCondition composite(Consumer<CompositeCondition> composite) {
		return buid(composite);
	}
	
	public static CompositeCondition composite(Consumer<CompositeCondition> composite,Associated associated) {
		return buid(composite,associated);
	}
	
	
	/**
	 * @param fieldName
	 * @return
	 */
	public static SimpleCondition buid(String fieldName) {
		SimpleCondition condition = new SimpleCondition();
		condition.setFieldName(fieldName);
		return condition;
	}
	
	public static SimpleCondition buid(String fieldName,Associated associated) {
		SimpleCondition condition = new SimpleCondition();
		condition.setFieldName(fieldName);
		condition.setAssociated(associated);
		return condition;
	}

	public static SimpleCondition eq(String fieldName,Object value) {
		return buid(fieldName).eq(value);
	}
	
	public static SimpleCondition eq(String fieldName,Object value,Associated associated) {
		return buid(fieldName,associated).eq(value);
	}

	public static SimpleCondition neq(String fieldName,Object value) {
		return buid(fieldName).neq(value);
	}
	
	public static SimpleCondition neq(String fieldName,Object value,Associated associated) {
		return buid(fieldName,associated).neq(value);
	}

	public static SimpleCondition gt(String fieldName,Object value) {
		return buid(fieldName).gt(value);
	}
	
	public static SimpleCondition gt(String fieldName,Object value,Associated associated) {
		return buid(fieldName,associated).gt(value);
	}

	public static SimpleCondition gte(String fieldName,Object value) {
		return buid(fieldName).gte(value);
	}
	
	public static SimpleCondition gte(String fieldName,Object value,Associated associated) {
		return buid(fieldName,associated).gte(value);
	}

	public static SimpleCondition lt(String fieldName,Object value) {
		return buid(fieldName).lt(value);
	}
	
	public static SimpleCondition lt(String fieldName,Object value,Associated associated) {
		return buid(fieldName,associated).lt(value);
	}

	public static SimpleCondition lte(String fieldName,Object value) {
		return buid(fieldName).lte(value);
	}
	
	public static SimpleCondition lte(String fieldName,Object value,Associated associated) {
		return buid(fieldName,associated).lte(value);
	}

	public static SimpleCondition isNull(String fieldName) {
		return buid(fieldName).nullOf();
	}
	
	public static SimpleCondition isNull(String fieldName,Associated associated) {
		return buid(fieldName,associated).nullOf();
	}

	public static SimpleCondition isNotNull(String fieldName) {
		return buid(fieldName).notNull();
	}
	
	public static SimpleCondition isNotNull(String fieldName,Associated associated) {
		return buid(fieldName,associated).notNull();
	}

	public static SimpleCondition isEmpty(String fieldName) {
		return buid(fieldName).empty();
	}
	
	public static SimpleCondition isEmpty(String fieldName,Associated associated) {
		return buid(fieldName,associated).empty();
	}

	public static SimpleCondition isNotEmpty(String fieldName) {
		return buid(fieldName).notEmpty();
	}
	
	public static SimpleCondition isNotEmpty(String fieldName,Associated associated) {
		return buid(fieldName,associated).notEmpty();
	}

	public static SimpleCondition startWith(String fieldName,Object value) {
		return buid(fieldName).startWith(value);
	}
	
	public static SimpleCondition startWith(String fieldName,Object value,Associated associated) {
		return buid(fieldName,associated).startWith(value);
	}

	public static SimpleCondition endWith(String fieldName,Object value) {
		return buid(fieldName).endWith(value);
	}
	
	public static SimpleCondition endWith(String fieldName,Object value,Associated associated) {
		return buid(fieldName,associated).endWith(value);
	}

	public static SimpleCondition contains(String fieldName,Object value) {
		return buid(fieldName).contains(value);
	}
	
	public static SimpleCondition contains(String fieldName,Object value,Associated associated) {
		return buid(fieldName,associated).contains(value);
	}

	public static SimpleCondition noContains(String fieldName,Object value) {
		return buid(fieldName).noContains(value);
	}
	
	public static SimpleCondition noContains(String fieldName,Object value,Associated associated) {
		return buid(fieldName,associated).noContains(value);
	}

	public static SimpleCondition in(String fieldName,String[] value) {
		
		if(value==null || value.length<1) {
			throw new IllegalStateException("value size must gt 1,but length is 0 or value is null");
		}
		
		return buid(fieldName).in(value);
	}
	
	public static SimpleCondition in(String fieldName,String[] value,Associated associated) {
		
		if(value==null || value.length<1) {
			throw new IllegalStateException("value size must gt 1,but length is 0 or value is null");
		}
		
		return buid(fieldName,associated).in(value);
	}

	public static SimpleCondition in(String fieldName,Integer[] value) {
		
		if(value==null || value.length<1) {
			throw new IllegalStateException("value size must gt 1,but length is 0 or value is null");
		}
		
		return buid(fieldName).in(value);
	}
	
	public static SimpleCondition in(String fieldName,Integer[] value,Associated associated) {
		
		if(value==null || value.length<1) {
			throw new IllegalStateException("value size must gt 1,but length is 0 or value is null");
		}
		
		return buid(fieldName,associated).in(value);
	}

	public static SimpleCondition in(String fieldName,Double[] value) {
		
		if(value==null || value.length<1) {
			throw new IllegalStateException("value size must gt 1,but length is 0 or value is null");
		}
		
		return buid(fieldName).in(value);
	}
	
	public static SimpleCondition in(String fieldName,Double[] value,Associated associated) {
		
		if(value==null || value.length<1) {
			throw new IllegalStateException("value size must gt 1,but length is 0 or value is null");
		}
		
		return buid(fieldName,associated).in(value);
	}

	public static SimpleCondition notIn(String fieldName,String[] value) {
		
		if(value==null || value.length<1) {
			throw new IllegalStateException("value size must gt 1,but length is 0 or value is null");
		}
		
		return buid(fieldName).notIn(value);
	}
	
	public static SimpleCondition notIn(String fieldName,String[] value,Associated associated) {
		
		if(value==null || value.length<1) {
			throw new IllegalStateException("value size must gt 1,but length is 0 or value is null");
		}
		
		return buid(fieldName,associated).notIn(value);
	}

	public static SimpleCondition notIn(String fieldName,Integer[] value) {
		
		if(value==null || value.length<1) {
			throw new IllegalStateException("value size must gt 1,but length is 0 or value is null");
		}
		
		return buid(fieldName).notIn(value);
	}
	
	public static SimpleCondition notIn(String fieldName,Integer[] value,Associated associated) {
		
		if(value==null || value.length<1) {
			throw new IllegalStateException("value size must gt 1,but length is 0 or value is null");
		}
		
		return buid(fieldName,associated).notIn(value);
	}

	public static SimpleCondition notIn(String fieldName,Double[] value) {
		
		if(value==null || value.length<1) {
			throw new IllegalStateException("value size must gt 1,but length is 0 or value is null");
		}
		
		return buid(fieldName).notIn(value);
	}
	
	public static SimpleCondition notIn(String fieldName,Double[] value,Associated associated) {
		
		if(value==null || value.length<1) {
			throw new IllegalStateException("value size must gt 1,but length is 0 or value is null");
		}
		
		return buid(fieldName,associated).notIn(value);
	}

	public static SimpleCondition between(String fieldName,int start, int end) {
		return buid(fieldName).between(start, end);
	}
	
	public static SimpleCondition between(String fieldName,int start, int end,Associated associated) {
		return buid(fieldName,associated).between(start, end);
	}

	public static SimpleCondition between(String fieldName,String start, String end) {
		return buid(fieldName).between(start, end);
	}
	
	public static SimpleCondition between(String fieldName,String start, String end,Associated associated) {
		return buid(fieldName,associated).between(start, end);
	}
	
	public static SimpleCondition contains(String fieldName,String value) {
		
		return buid(fieldName,CONTAINS, value);
	}
	
	public static SimpleCondition contains(String fieldName,String value,Associated associated) {
		return buid(fieldName, CONTAINS, value,associated);
	}

	
}
