package com.tableturn.error;

/** 409 /errors/table-too-small: the table has fewer seats than the party has covers.*/
public class TableTooSmall extends DomainException {

     public TableTooSmall(String table, int seats, int covers) {
        super(409, "/errors/table-too-small", "Table too small",
                "Table " + table + " seats " + seats + "; the party is " + covers);
    }
}