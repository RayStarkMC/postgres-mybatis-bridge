package raystark.pmb.range;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@Execution(ExecutionMode.CONCURRENT)
public class RangeTypeHandlerTest {
    @Container
    private final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.1-alpine"));
    SqlSession sqlSession;
    TestMapper mapper;

    @BeforeEach
    void beforeEach() {
        var typehandler = new TSRangeTypeHandler();

        var dataSource = new PGSimpleDataSource();
        dataSource.setUser(postgreSQLContainer.getUsername());
        dataSource.setPassword(postgreSQLContainer.getPassword());
        dataSource.setUrl(postgreSQLContainer.getJdbcUrl());

        var environment = new Environment.Builder("test")
            .transactionFactory(new JdbcTransactionFactory())
            .dataSource(dataSource)
            .build();

        var configuration = new Configuration(environment);
        configuration.getTypeHandlerRegistry().register(typehandler);
        configuration.getMapperRegistry().addMapper(TestMapper.class);

        this.sqlSession = new SqlSessionFactoryBuilder().build(configuration).openSession();

        mapper = sqlSession.getMapper(TestMapper.class);
        mapper.createTable();
    }

    @AfterEach
    void afterEach() {
        sqlSession.close();
    }

    @Mapper
    interface TestMapper {
        @Update(
            """
            set timezone to 'UTC';
            """
        )
        void setTimeZoneToUTC();

        @Update(
            """
            set timezone to 'Asia/Tokyo';
            """
        )
        void setTimeZoneToAsiaTokyo();

        @Update(
            """
            create table t (
                range tsrange
            );
            """
        )
        void createTable();

        @Update(
            """
            truncate t;
            """
        )
        void truncateTable();

        @Insert(
            """
            insert into t(range) values (#{tsRange})
            """
        )
        void insert(@Param("tsRange") TSRange tsRange);

        @Select(
            """
            select * from t;
            """
        )
        TSRange select();
    }

    @Test
    void testCase() {
        var testCases = List.<TSRange>of(
            new TSRange.Empty(),
            new TSRange.NonEmpty(
                new Bound.Infinity(),
                new Bound.Infinity()
            ),
            new TSRange.NonEmpty(
                new Bound.Inclusive(OffsetDateTime.of(2022, 12, 8, 12, 0, 0, 0, ZoneOffset.UTC).toInstant()),
                new Bound.Exclusive(OffsetDateTime.of(2022, 12, 8, 13, 0, 0, 0, ZoneOffset.UTC).toInstant())
            ),
            new TSRange.NonEmpty(
                new Bound.Exclusive(OffsetDateTime.of(2022, 12, 8, 12, 0, 0, 0, ZoneOffset.UTC).toInstant()),
                new Bound.Inclusive(OffsetDateTime.of(2022, 12, 8, 13, 0, 0, 0, ZoneOffset.UTC).toInstant())
            )
        );

        mapper.setTimeZoneToUTC();

        for (var testCase : testCases) {
            mapper.insert(testCase);
            var actual = mapper.select();
            assertEquals(testCase, actual);
            mapper.truncateTable();
        }

        mapper.setTimeZoneToAsiaTokyo();

        for (var testCase : testCases) {
            mapper.insert(testCase);
            var actual = mapper.select();
            assertEquals(testCase, actual);
            mapper.truncateTable();
        }
    }
}
