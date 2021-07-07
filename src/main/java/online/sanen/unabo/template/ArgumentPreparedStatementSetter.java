package online.sanen.unabo.template;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mhdt.degist.Validate;
import com.mhdt.toolkit.Reflect;

/**
 * 
 * <pre>
 * Simple adapter for {@link PreparedStatementSetter} that applies a given array of arguments.
 * &#64;author online.sanen
 * Date:2018年10月14日
 * Time:下午7:01:35
 * </pre>
 */
public class ArgumentPreparedStatementSetter implements PreparedStatementSetter {

	private final Object[] args;

	/**
	 * Create a new ArgPreparedStatementSetter for the given arguments.
	 * 
	 * @param args the arguments to set
	 */
	public ArgumentPreparedStatementSetter(Object[] args) {
		this.args = args;
	}

	public void setValues(PreparedStatement ps) throws SQLException {
		if (this.args != null) {
			for (int i = 0; i < this.args.length; i++) {
				Object arg = this.args[i];
				doSetValue(ps, i + 1, arg);
			}
		}
	}

	/**
	 * Set the value for prepared statements specified parameter index using the
	 * passed in value. This method can be overridden by sub-classes if needed.
	 * 
	 * @param ps
	 * @param parameterPosition
	 * @param argValue
	 * @throws SQLException
	 */
	protected void doSetValue(PreparedStatement ps, int parameterPosition, Object argValue) throws SQLException {

		try {

			
			if (Validate.isEnum(argValue)) {
				ps.setString(parameterPosition, String.valueOf(argValue));
			} else {
				Method method = Reflect.getMethod(ps, "set" + argValue.getClass().getSimpleName(), int.class,argValue.getClass());
				method.invoke(ps, parameterPosition, argValue);
			}

		} catch (Exception e) {
			ps.setObject(parameterPosition, argValue);
		}

	}

}
