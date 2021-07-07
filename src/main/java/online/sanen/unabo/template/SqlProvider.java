package online.sanen.unabo.template;


/**
 * Interface to be implemented by objects that can provide SQL strings.
 *
 * <p>Typically implemented by PreparedStatementCreators, CallableStatementCreators
 * and StatementCallbacks that want to expose the SQL they use to create their
 * statements, to allow for better contextual information in case of exceptions.
 *
 * <pre>
 *
 * @author online.sanen
 * Date:2018年11月10日
 * Time:下午1:56:26
 * </pre>
 */
public interface SqlProvider {

	/**
	 * Return the SQL string for this object, i.e.
	 * typically the SQL used for creating statements.
	 * @return the SQL string, or {@code null}
	 */
	String getSql();


}