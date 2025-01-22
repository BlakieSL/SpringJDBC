package org.example.springjdbc.repository.implementation.library;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.sql.Types;

import static org.example.springjdbc.helper.QueryStatements.DELETE_LIBRARY;

@Component
public class DeleteLibrary extends SqlUpdate {
    DeleteLibrary(DataSource ds) {
        super(ds, DELETE_LIBRARY);
        super.declareParameter(new SqlParameter("id", Types.BIGINT));
    }
}
