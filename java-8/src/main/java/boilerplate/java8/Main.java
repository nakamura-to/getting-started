package boilerplate.java8;

import boilerplate.java8.dao.AppDao;
import boilerplate.java8.dao.AppDaoImpl;
import boilerplate.java8.entity.Employee;
import boilerplate.java8.repository.EmployeeRepository;
import java.util.Arrays;
import java.util.List;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.JdbcLogger;
import org.seasar.doma.jdbc.Slf4jJdbcLogger;
import org.seasar.doma.jdbc.criteria.tuple.Tuple2;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.H2Dialect;
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource;
import org.seasar.doma.jdbc.tx.LocalTransactionManager;
import org.seasar.doma.jdbc.tx.TransactionManager;

public class Main {

  public static void main(String[] args) {
    Config config = createConfig();
    TransactionManager tm = config.getTransactionManager();

    // setup database
    AppDao appDao = new AppDaoImpl(config);
    tm.required(appDao::create);

    // read and update
    tm.required(
        () -> {
          EmployeeRepository repository = new EmployeeRepository(config);
          List<Employee> list =
              repository.selectByTuple2(Arrays.asList(new Tuple2<>(1, 0), new Tuple2<>(2, 0)));
          list.forEach(System.out::println);
        });
  }

  private static Config createConfig() {
    Dialect dialect = new H2Dialect();
    LocalTransactionDataSource dataSource =
        new LocalTransactionDataSource("jdbc:h2:mem:tutorial;DB_CLOSE_DELAY=-1", "sa", null);
    JdbcLogger jdbcLogger = new Slf4jJdbcLogger();
    TransactionManager transactionManager = new LocalTransactionManager(dataSource, jdbcLogger);
    return new DbConfig(dialect, dataSource, jdbcLogger, transactionManager);
  }
}
