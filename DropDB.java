import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class DropDB {
    public static void main(String... args) {
        Driver driver = GraphDatabase.driver("neo4j+s://2cbfe1f1.databases.neo4j.io", AuthTokens.basic("neo4j", "XwCuobDWqUlc4O-q0HI-Kfwzmd-wObreUrF30DXD1JM"));
        try (Session session = driver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
        }
        driver.close();
    }
}
