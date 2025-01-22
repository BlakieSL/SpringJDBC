package org.example.springjdbc.repository.implementation.library;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.sql.Types;

import static org.example.springjdbc.helper.QueryStatements.UPDATE_LIBRARY;

@Component
public class UpdateLibrary extends SqlUpdate {
    UpdateLibrary(DataSource ds) {
        super(ds, UPDATE_LIBRARY);
        super.declareParameter(new SqlParameter("name", Types.VARCHAR));
        super.declareParameter(new SqlParameter("id", Types.BIGINT));
    }
}
