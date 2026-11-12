package com.tableturn.error;

/** 409 /errors/table-not-cleared: the table still has a seating on it - nobody has cleared it yet. */
public class TableNotCleared extends DomainException {

    public TableNotCleared(String table) {
        super(409, "/errors/table-not-cleared", "Table not cleared", "Table " + table + " has not been cleared");
    }
}