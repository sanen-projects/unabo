package online.sanen.unabo.api;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import online.sanen.unabo.api.structure.Column;
import online.sanen.unabo.api.structure.StreamConsumer;


/**
 * 
 *
 * @author LazyToShow	<br>
 * Date:	2018年10月16日	<br>
 * Time:	下午3:30:46
 */
public interface Stream {

	void stream(int buffersize, Consumer<List<Map<String, Object>>> rows);

	void stream(int buffersize, Consumer<List<Map<String, Object>>> rows, Map<String, String> aliases);
	
	void stream(int bufferSize, Function<List<Column>,Object> rows, StreamConsumer datas, Map<String, String> aliases);

}
