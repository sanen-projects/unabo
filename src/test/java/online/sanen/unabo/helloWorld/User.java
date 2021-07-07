package online.sanen.unabo.helloWorld;

import lombok.Data;
import online.sanen.unabo.core.Behavior;
import online.sanen.unabo.template.jpa.BootstrapId;
import online.sanen.unabo.template.jpa.Id;
import online.sanen.unabo.template.jpa.Table;

@Data
@BootstrapId("Input boostrap id")
@Table(name="user")
public class User implements Behavior<User>{
	
	@Id
	Integer id;
	String name;
	Integer age;
}
