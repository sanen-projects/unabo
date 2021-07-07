package online.sanen.unabo.helloWorld;

import java.io.IOException;
import java.util.List;

import online.sanen.unabo.api.Bootstrap;
import online.sanen.unabo.api.structure.Configuration.DataSouseType;
import online.sanen.unabo.api.structure.Configuration.TransactionFactoryEnum;
import online.sanen.unabo.api.structure.enums.DriverOption;
import online.sanen.unabo.core.factory.Unabo;

public class HelloWorld {

	public static void main(String[] args) throws IOException {

		Bootstrap bootstrap = Unabo.load("default", configuration -> {
			configuration.setUrl(
					"jdbc:mysql://127.0.0.1:3306/test?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
			configuration.setDriverOption(DriverOption.MYSQL_CJ);
			configuration.setDataSouseType(DataSouseType.HikariCP);
			configuration.setUsername("zs");
			configuration.setPassword("123456");
			configuration.setShowSql(true);
			configuration.setTransactionFactory(TransactionFactoryEnum.JdbcTransactionFactory);
		});

		// List<String> tableNames = bootstrap.dataInformation().getTableNames();
		// System.out.println(tableNames);

		// bootstrap.createSQL("select * from user").list();

		// bootstrap.createSQL("select * from user where id=? and
		// age>?",10,18).unique();

		// bootstrap.createSQL("update user set age=18 where id=?",10).update();

		/** 还是刚刚的列表查询语句，稍加改造 */
		// bootstrap.createSQL("select * from user").list();
//		List<User> users = bootstrap.createSQL("select * from user").list(User.class);
//		System.out.println(users);

		// QueryTable queryTable = bootstrap.queryTable("user");
//		List<User> list = bootstrap.queryTable(User.class).addCondition(eq(null, bootstrap)).setExceptFields(args).sort(Sorts.DESC, "id","name").list(User.class);
//		System.out.println(list);
		
//		User user = new User();
//		user.setName("new user");
//		user.setAge(18);
//		bootstrap.query(user).insert();
		
		List<User> users =  bootstrap.queryTable(User.class).list(User.class);
		bootstrap.query(users).setExceptFields("id").update();
		
//		User user = bootstrap.queryTable(User.class).addCondition(eq("id", 1)).unique(User.class);
//		user.setName("new Name");
//		bootstrap.query(user).update();
		
	}

}
