package org.example.springjdbc.repository.implementation.library;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.sql.Types;

import static org.example.springjdbc.helper.QueryStatements.INSERT_LIBRARY;

@Component
public class InsertLibrary extends SqlUpdate {
    InsertLibrary(DataSource dataSource) {
        super(dataSource, INSERT_LIBRARY);
        super.declareParameter(new SqlParameter("name", Types.VARCHAR));
        super.setGeneratedKeysColumnNames("id");
        super.setReturnGeneratedKeys(true);
    }
}
