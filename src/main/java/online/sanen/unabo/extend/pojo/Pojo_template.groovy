def sb = new StringBuilder();
def engine = new groovy.text.GStringTemplateEngine()
def binding = [
	tableName:tableName,
	className:className,
]

sb.append("package ${packageName};")
sb.append("\r\n");
sb.append("\r\n");

for(var item : imports) {
	sb.append("import ${item};");
	sb.append("\r\n");
}

sb.append("\r\n");

for(item : annotations) {
	sb.append(engine.createTemplate("@${item}").make(binding).toString());
	sb.append("\r\n");
}

sb.append("public class ${className}")
if(!interfaces.isEmpty()) {
	sb.append(" implements ");
	for(item : interfaces) {
		sb.append(engine.createTemplate(item).make(binding).toString());
	}
}
sb.append("{");
sb.append("\r\n");
sb.append("\r\n");

for(item : columns) {

	if(!item.annotations.isEmpty()) {
		for(annotation : item.annotations) {
			sb.append("\t");
			sb.append("@${annotation}");
			sb.append("\r\n");
		}
	}
	
	sb.append("\t");
	sb.append("${item.getType()} ${item.getName()};");
	sb.append("\r\n");
}


sb.append("\r\n");
sb.append("}");

sb.toString();