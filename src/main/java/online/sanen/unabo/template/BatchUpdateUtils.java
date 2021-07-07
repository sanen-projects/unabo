package online.sanen.unabo.template;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.mhdt.degist.Validate;
import com.mhdt.toolkit.Reflect;

public abstract class BatchUpdateUtils {

	public static int[] executeBatchUpdate(String sql, final List<Object[]> batchValues,
			JdbcOperations jdbcOperations) {

		return jdbcOperations.batchUpdate(sql, new PreparedStatementSetterBatch() {

			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Object[] values = batchValues.get(i);
				setStatementParameters(values, ps);
			}

			public int getBatchSize() {
				return batchValues.size();
			}

		});
	}

	protected static void setStatementParameters(Object[] values, PreparedStatement ps) throws SQLException {

		int colIndex = 0;
		for (Object value : values) {
			colIndex++;

			try {

				if (Validate.isEnum(value)) {
					ps.setString(colIndex, String.valueOf(value));
				} else {
					Method method = Reflect.getMethod(ps, "set" + value.getClass().getSimpleName(), int.class,
							value.getClass());
					method.invoke(ps, colIndex, value);
				}

			} catch (Exception e) {
				ps.setObject(colIndex, value);
			}

		}
	}

}
