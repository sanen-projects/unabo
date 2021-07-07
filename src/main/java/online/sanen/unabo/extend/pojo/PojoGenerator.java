package online.sanen.unabo.extend.pojo;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory;

import com.mhdt.io.FileIO;
import com.mhdt.toolkit.Assert;
import com.mhdt.toolkit.Collections;
import com.mhdt.toolkit.StringUtility;

import lombok.Data;
import online.sanen.unabo.api.Bootstrap;
import online.sanen.unabo.core.Behavior;
import online.sanen.unabo.template.jpa.Id;
import online.sanen.unabo.template.jpa.NoUpdate;
import online.sanen.unabo.template.jpa.Table;

/**
 * Automatically generate POJO code, The default global setting is<br>
 * 
 * @author lazyToShow <br>
 *         Date: 2020-9-18 <br>
 *         Time: 10:35:40 <br>
 */
public class PojoGenerator {

	String packageName;
	String className;

	List<String> imports = new LinkedList<>();
	List<String> annotations = new LinkedList<>();
	List<String> interfaces = new LinkedList<>();
	List<PojoColumn> columns = new LinkedList<>();

	private Bootstrap bootstrap;
	private File targetDirectory;

	Map<String, List<String>> addAnnotationByColumn = new HashMap<>();

	public static Consumer<PojoGenerator> GLOBAL_CONFIGURATION_CONSUMER = pojoGenerator -> {

		pojoGenerator.addAnnotation(Data.class)
				.addAnnotation(Table.class, "Table(name=\"${tableName}\")")
				.addAnnotationByColumn("id", Id.class, NoUpdate.class)
				.addInterface(Behavior.class, "Behavior<${className}>");

	};

	Function<String, String> classNameProcess = (tableName) -> {

		String[] split = tableName.split("_");

		List<String> toJoinItems = Collections.asList(split).stream().map(StringUtility::firstToUpperCase)
				.collect(Collectors.toList());

		return StringUtility.join(toJoinItems, "");
	};

	/**
	 * 
	 * @param bootstrap       - A {@link Bootstrap} connection is available
	 * @param targetDirectory - Generate package directory
	 */
	public PojoGenerator(Bootstrap bootstrap, File targetDirectory) {

		this.targetDirectory = targetDirectory;
		this.bootstrap = bootstrap;
		this.packageName = targetDirectoryToPackageName();
	}

	public PojoGenerator addAnnotation(Class<?> cls) {

		if (!annotations.contains(cls.getSimpleName())) {
			annotations.add(cls.getSimpleName());
			addImport(cls);
		}

		return this;
	}

	public PojoGenerator addAnnotation(Class<?> cls, String name) {

		if (!annotations.contains(name)) {
			annotations.add(name);
			addImport(cls);
		}

		return this;
	}

	public PojoGenerator addInterface(Class<?> cls) {

		if (!interfaces.contains(cls.getSimpleName())) {
			interfaces.add(cls.getSimpleName());
			addImport(cls);
		}

		return this;
	}

	public PojoGenerator addInterface(Class<?> interfaceClass, String interfaceName) {

		if (!interfaces.contains(interfaceName)) {
			interfaces.add(interfaceName);
			addImport(interfaceClass);
		}

		return this;
	}

	public PojoGenerator addImport(Class<?> cls) {

		if (!imports.contains(cls.getName()))
			imports.add(cls.getName());

		return this;

	}

	public PojoGenerator addImport(String cls) {

		if (!imports.contains(cls))
			imports.add(cls);

		return this;
	}

	private String targetDirectoryToPackageName() {

		String path = targetDirectory.getAbsolutePath().replace("\\", ".").replace("/", ".");

		if (path.contains("main.java.")) {
			path = path.substring(path.indexOf("main.java.") + "main.java.".length());
		} else if (path.contains("test.java.")) {
			path = path.substring(path.indexOf("test.java.") + "test.java.".length());
		} else {
			throw new RuntimeException(String.format("The packet path is not recognized : %s", path));
		}

		return path;
	}

	/**
	 * All available tables generate Pojo
	 */
	public void generalAll() {

		List<String> tableNames = bootstrap.dataInformation().getTableNames();
		for (String tableName : tableNames) {
			general(tableName);
		}
	}

	public File general(String tableName) {

		Assert.notNull(targetDirectory, "TargetDirectory is null %s", targetDirectory);
		Assert.state(targetDirectory.exists(), "TargetDirectory is not exists: %s", targetDirectory);
		Assert.state(targetDirectory.isDirectory(), "TargetDirectory is not a directory : %s", targetDirectory);

		if (PojoGenerator.GLOBAL_CONFIGURATION_CONSUMER != null)
			PojoGenerator.GLOBAL_CONFIGURATION_CONSUMER.accept(this);

		this.className = classNameProcess.apply(tableName);
		this.columns = bootstrap.dataInformation().getColumns(tableName).stream()
				.sorted((o1, o2) -> o1.getIspk() ? -1 : 1).map(mapper -> {
					PojoColumn pc = new PojoColumn();
					pc.setName(mapper.getName());
					pc.setType(mapper.getClsByDataType().getSimpleName());
					if (this.addAnnotationByColumn.containsKey(mapper.getName())) {
						pc.setAnnotations(this.addAnnotationByColumn.get(mapper.getName()));
					}

					return pc;

				}).collect(Collectors.toList());

		GroovyScriptEngineFactory scriptEngineFactory = new GroovyScriptEngineFactory();
		ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
		Bindings bindings = new SimpleBindings();

		bindings.put("packageName", this.packageName);
		bindings.put("tableName", tableName);
		bindings.put("className", this.className);
		bindings.put("imports", this.imports);
		bindings.put("annotations", this.annotations);
		bindings.put("interfaces", this.interfaces);
		bindings.put("columns", this.columns);

		String eval = null;

		try {
			InputStream resourceAsStream = PojoGenerator.class.getResourceAsStream("Pojo_template.groovy");
			eval = (String) scriptEngine.eval(FileIO.getContent(resourceAsStream), bindings);
		} catch (ScriptException e) {
			e.printStackTrace();
		}

		File outFile = new File(targetDirectory.getAbsolutePath() + File.separator + className + ".java");
		FileIO.write(outFile, eval, false);
		return outFile;

	}

	@Data
	public static class PojoColumn {
		String type;
		String name;
		List<String> annotations = new LinkedList<>();
	}

	public PojoGenerator processClassName(Function<String, String> function) {
		this.classNameProcess = function;
		return this;
	}

	public PojoGenerator addAnnotationByColumn(String columnName, Class<?>... annotations) {

		for (Class<?> cls : annotations)
			addImport(cls);

		addAnnotationByColumn.put(columnName, Collections.asList(annotations).stream()
				.map(mapper -> mapper.getSimpleName()).collect(Collectors.toList()));

		return this;
	}

}
